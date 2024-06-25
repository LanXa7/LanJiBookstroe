<script setup>

import Card from "@/components/Card.vue";
import {Setting, Switch, Lock} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";

const form = reactive({
  password: '',
  new_password: '',
  new_password_repeat: ''
})
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.new_password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}
const rules = {
  password: [
    { required: true, message: '请输入原来的密码', trigger: 'blur' }
  ],
  new_password: [
    { required: true, message: '请输入新的密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur'] }
  ],
  new_password_repeat: [
    { required: true, message: '请再次输入新的密码', trigger: 'blur' },
    { validator: validatePassword, trigger: ['blur', 'change'] },
  ]
}
const formRef = ref()
const valid = ref(false)
const onValidate = (prop, isValid) => valid.value = isValid

function resetPassword(){
  formRef.value.validate(valid => {
    if(valid) {
      post('/api/user/change-password', form, () => {
        ElMessage.success('修改密码成功！')
        formRef.value.resetFields();
      })
    }
  })
}

</script>

<template>
  <div style="margin: auto;max-width: 600px">
    <div style="margin-top: 20px">
      <card style="margin: 20px 0" :icon="Setting"
            title="修改密码" desc="修改密码请在这里进行操作，请务必牢记您的密码">
        <el-form :rules="rules" :model="form" ref="formRef" @validate="onValidate" label-width="100" style="margin: 20px">
          <el-form-item label="当前密码" prop="password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.password"
                      placeholder="当前密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password"
                      placeholder="新密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="new_password_repeat">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password_repeat"
                      placeholder="重新输入新密码" maxlength="16"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button @click="resetPassword" :icon="Switch" type="success">立即重置密码</el-button>
          </div>
        </el-form>
      </card>
    </div>
  </div>
</template>

<style scoped>

</style>
