package org.lanmao.listener

import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
@RabbitListener(queues = ["mail"])
class MailQueueListener(
    val sender: JavaMailSender,
    @Value("\${spring.mail.username}")
    val username: String,
) {
    @RabbitHandler
    fun sendMailMessage(data: Map<String, Any>) {
        val email = data["email"] as String
        val code = data["code"] as Int
        val type = data["type"] as String
        val message = when (type) {
            "register" -> createMessage(
                "欢迎注册我们的网站",
                "您的邮件注册验证码为: $code，有效时间3分钟，为了保障您的账户安全，请勿向他人泄露验证码信息。",
                email
            )
            "reset" -> createMessage(
                "您的密码重置邮件",
                "你好，您正在执行重置密码操作，验证码: $code，有效时间3分钟，如非本人操作，请无视。",
                email
            )
            "modify" -> createMessage(
                "您的邮件修改验证邮件",
                "您好，您正在绑定新的电子邮件地址，验证码：$code，有效时间三分钟，如非本人操作，请无视。",
                email
            )
            else -> null
        }
        if(message == null)
            return
        sender.send(message)
    }

    private fun createMessage(title: String, content: String, email: String): SimpleMailMessage {
        val message = SimpleMailMessage()
        message.subject = title
        message.text = content
        message.setTo(email)
        message.from = username
        return message
    }
}