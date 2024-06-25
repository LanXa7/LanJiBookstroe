package org.lanmao.repository

import org.babyfish.jimmer.View
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.or
import org.lanmao.entity.vo.request.*
import org.lanmao.model.*
import org.lanmao.utils.Const
import org.lanmao.utils.FlowUtils
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.reflect.KClass

@Repository
class AccountRepository(
    val sqlClient: KSqlClient,
    val amqpTemplate: AmqpTemplate,
    val redisTemplate: StringRedisTemplate,
    val flowUtils: FlowUtils,
    val passwordEncoder: PasswordEncoder,
) : UserDetailsService {

    /**
     * 加载用户信息
     */
    override fun loadUserByUsername(username: String): UserDetails? {
        val account = this.findAccountByNameOrEmail(username) ?: throw UsernameNotFoundException("用户名或密码错误")
        println(account)
        return User
            .withUsername(username)
            .password(account.password)
            .roles(account.role)
            .build()
    }


    /**
     * 通过邮箱或者密码查找账户
     */
    fun findAccountByNameOrEmail(
        text: String,
    ): Account? =
        sqlClient
            .createQuery(Account::class) {
                where(
                    or(
                        table.username eq text,
                        table.email eq text
                    )
                )
                select(
                    table.fetchBy {
                        allScalarFields()
                    }
                )
            }
            .fetchOneOrNull()

    /**
     * 获取注册验证码
     */
    fun registerEmailVerifyCode(type: String, email: String, ip: String): String? {
        synchronized(ip.intern()) {
            if (!this.verifyLimit(ip))
                return "请求频繁，请稍后再试"
            val random = Random
            val code = random.nextInt(899999) + 100000
            val data = mapOf(
                "type" to type,
                "email" to email,
                "code" to code
            )
            amqpTemplate.convertAndSend("mail", data)
            redisTemplate.opsForValue()
                .set(Const.VERIFY_EMAIL_DATA + email, code.toString(), 3, TimeUnit.MINUTES)
            return null
        }
    }

    private fun verifyLimit(ip: String): Boolean =
        flowUtils.limitOnceCheck(Const.VERIFY_EMAIL_LIMIT + ip, 60)

    /**
     * 注册
     */
    fun registerEmailAccount(vo: EmailRegisterVO): String? {
        val code = this.getEmailVerifyCode(vo.email) ?: return "请先获取验证码"
        if (code != vo.code)
            return "验证码输入错误，请重新输入"
        if (this.existsAccountByEmail(vo.email))
            return "此电子邮件已被其他用户注册"
        if (this.existsAccountByUsername(vo.username))
            return "此用户名已被其他用户注册,请更新一个新的用户名"
        if (sqlClient.insert(new(Account::class).by {
                username = vo.username
                password = passwordEncoder.encode(vo.password)
                email = vo.email
                role = "user"
                registerTime = Date()
            }).totalAffectedRowCount > 0) {
            this.deleteEmailVerifyCode(vo.email)
            return null
        } else {
            return "内部错误，请联系管理员"
        }
    }

    /**
     * 通过邮件查找用户
     * @return 如果存在用户返回true
     */
    private fun existsAccountByEmail(email: String): Boolean {
        println(email)
        val haveAccount = sqlClient
            .createQuery(Account::class) {
                where(table.email.eq(email))
                select(
                    table.id
                )
            }
            .fetchOneOrNull()
        println(haveAccount)
        return haveAccount != null
    }

    /**
     * 通过用户名查找用户
     * @return 如果存在用户返回true
     */
    private fun existsAccountByUsername(username: String): Boolean {
        val haveAccount = sqlClient
            .createQuery(Account::class) {
                where(table.username.eq(username))
                select(
                    table.id
                )
            }
            .fetchOneOrNull()
        return haveAccount != null
    }

    fun resetConfirm(vo: ConfirmResetVO): String? {
        val code = this.getEmailVerifyCode(vo.email) ?: return "请先获取验证码"
        if (code != vo.code) return "验证码错误，请重新输入"
        return null;
    }

    fun resetEmailAccountPassword(vo: EmailResetVO): String? {
        val email = vo.email
        val verify = this.resetConfirm(ConfirmResetVO(email, vo.code))
        if (verify != null) return verify
        sqlClient
            .createUpdate(Account::class) {
                set(table.password, passwordEncoder.encode(vo.password))
                where(table.email.eq(email))
            }
            .execute()
        this.deleteEmailVerifyCode(email)
        return null;
    }

    /**
     * 移除Redis中存储的邮件验证码
     *
     * @param email 电邮
     */
    private fun deleteEmailVerifyCode(email: String) {
        val key = Const.VERIFY_EMAIL_DATA + email
        redisTemplate.delete(key)
    }

    /**
     * 获取Redis中存储的邮件验证码
     *
     * @param email 电邮
     * @return 验证码
     */
    private fun getEmailVerifyCode(email: String): String? {
        val key = Const.VERIFY_EMAIL_DATA + email
        return redisTemplate.opsForValue().get(key)
    }

    fun <V : View<Account>> findAccountById(
        id: Int,
        viewType: KClass<V>
    ): V? =
        sqlClient.findById(
            viewType,
            id
        )

    fun modifyEmail(id: Int, vo: ModifyEmailVO): String? {
        val code = this.getEmailVerifyCode(vo.email) ?: return "请先获取验证码"
        if (code != vo.code)
            return "验证码输入错误，请重新输入"
        this.deleteEmailVerifyCode(vo.email)
        val account = this.findAccountByNameOrEmail(vo.email)
        if (account != null && account.id != id)
            return "此电子邮件已经被其他用户绑定，无法完成此操作！";
        sqlClient.createUpdate(Account::class) {
            set(table.email, vo.email)
            where(table.id.eq(id))
        }.execute()

        return null
    }

    fun changePassword(id: Int, vo: ChangePasswordVO): String? {
        val password = sqlClient
            .createQuery(Account::class) {
                where(table.id.eq(id))
                select(
                    table.password
                )
            }
            .fetchOneOrNull()
        if (!passwordEncoder.matches(vo.password, password))
            return "原密码错误，请重新输入"
        sqlClient
            .createUpdate(Account::class) {
                set(table.password, passwordEncoder.encode(vo.new_password))
                where(table.id.eq(id))
            }
            .execute()
        return null
    }

}