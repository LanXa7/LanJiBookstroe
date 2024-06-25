<script setup>

import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";

const store = useStore()

// const registerTime = computed(() => new Date(store.user.registerTime))
const registerTime = computed(() => {
  const date = new Date(store.user.registerTime);

  // 格式化为 YYYY-MM-DD HH:mm:ss
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
})

const synopsis = ref('')
const baseFormRef = ref()
const emailFormRef = ref()

const baseForm = reactive({
  username: '',
  gender: 1,
  age: 3,
  phone: '',
  identificationNumber: '',
  address: '',
  synopsis: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  } else {
    callback()
  }
}

function validateAge(rule, value, callback) {
  const age = Number(value);
  if (!Number.isInteger(age)) {
    callback(new Error('年龄必须是整数'));
  } else if (age < 0 || age > 100) {
    callback(new Error('年龄必须在0-100之间'));
  } else {
    callback();
  }
}

const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 10, message: '用户名的长度必须在2-10个字符之间', trigger: ['blur', 'change']},
  ], email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ], phone: [
    {required: true, message: '请输入电话号码', trigger: 'blur'},
    {pattern: /^\d{11}$/, message: '电话号码必须为11位数字', trigger: ['blur', 'change']}
  ], identificationNumber: [
    {required: true, message: '请输入身份证号', trigger: 'blur'},
    {
      pattern: /^\d{17}[\dXx]$/,
      message: '身份证号必须为18位，其中最后一位可以为数字或字母X',
      trigger: ['blur', 'change']
    }
  ], age: [
    {required: true, message: '请输入年龄,在0-100之间', trigger: 'blur'},
    {validator: validateAge, trigger: ['blur', 'change']}
  ]
}

const loading = reactive({
  form: true,
  base: false
})

function saveDetails() {
  baseFormRef.value.validate(isValid => {
    if (isValid) {
      loading.base = true
      post('/api/user/save-details', baseForm, () => {
        ElMessage.success('用户信息保存成功')
        store.user.usernamew = baseForm.username
        synopsis.value = baseForm.synopsis
        loading.base = false
      }, (message) => {
        ElMessage.warning(message)
        loading.base = false
      })
    }
  })
}

get('/api/user/details', data => {
  baseForm.username = store.user.username
  emailForm.email = store.user.email
  if (data) {
    baseForm.gender = data.gender
    baseForm.age = data.age
    baseForm.phone = data.phone
    baseForm.identificationNumber = data.identificationNumber
    baseForm.address = data.address
    baseForm.synopsis = synopsis.value = data.synopsis
  }
  loading.form = false
})

const coldTime = ref(0)
const isEmailValid = ref(true)

const onValidate = (prop, isValid) => {
  if (prop === 'email')
    isEmailValid.value = isValid
}

function sendEmailCode() {
  emailFormRef.value.validate(isValid => {
    console.log(1)
    console.log(isValid)
    if (isValid) {
      coldTime.value = 60
      console.log(2)
      get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`, () => {
        ElMessage.success(`验证码已成功发送到邮箱：${emailForm.email}，请注意查收`)
        const handle = setInterval(() => {
          coldTime.value--
          if (coldTime.value === 0) {
            clearInterval(handle)
          }
        }, 1000)
      }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
      })
    }
  })
}

function modifyEmail() {
  emailFormRef.value.validate(isValid => {
    if (isValid) {
      post('/api/user/modify-email', emailForm, () => {
        ElMessage.success('邮件修改成功')
        store.user.email = emailForm.email
        emailForm.code = ''
      })
    }
  })
}

function beforeAvatarUpload(rawFile) {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像只能是 JPG/PNG 格式的')
    return false
  } else if (rawFile.size / 1024 > 100) {
    ElMessage.error('头像大小不能大于 100KB')
    return false
  }
  return true
}

function uploadSuccess(response) {
  ElMessage.success('头像上传成功')
  store.user.avatar = response.data
}

</script>

<template>
  <div style="display: flex;max-width: 950px;margin: auto">
    <div class="setting-left">
      <card :icon="User" title="个人信息设置" desc="在这里编辑您的个人信息" v-loading="loading.form">
        <el-form :model="baseForm" :rules="rules" ref="baseFormRef" label-position="top"
                 style="margin: 0 10px 10px 10px;">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username" maxlength="10"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="年龄" prop="age">
            <el-input v-model="baseForm.age"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11"></el-input>
          </el-form-item>
          <el-form-item label="身份证号" prop="identificationNumber">
            <el-input v-model="baseForm.identificationNumber" maxlength="18"></el-input>
          </el-form-item>
          <el-form-item label="家庭住址" prop="address">
            <el-input v-model="baseForm.address"></el-input>
          </el-form-item>
          <el-form-item label="个人简介" prop="synopsis">
            <el-input show-word-limit v-model="baseForm.synopsis" type="textarea" :rows="6" maxlength="200"/>
          </el-form-item>
          <div>
            <el-button :icon="Select" @click="saveDetails" :loading="loading.base" type="success">保存用户信息
            </el-button>
          </div>
        </el-form>
      </card>
      <card style="margin-top: 10px" :icon="Message" title="电子邮件设置"
            desc="您可以在这里修改默认绑定的电子邮件地址">
        <el-form :rules="rules" @validate="onValidate" :model="emailForm" ref="emailFormRef"
                 label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮件" prop="email">
            <el-input v-model="emailForm.email"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row style="width: 100%" :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请获取验证码" v-model="emailForm.code"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" style="width: 100%" :disabled="!isEmailValid || coldTime > 0"
                           @click="sendEmailCode" plain>
                  {{ coldTime > 0 ? `请稍后 ${coldTime} 秒` : '获取验证码' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button :icon="Refresh" type="success" @click="modifyEmail">更新电子邮件</el-button>
          </div>
        </el-form>
      </card>
    </div>
    <div class="setting-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar :size="70" :src="store.avatarUrl"/>
            <div style="margin: 5px 0">
              <el-upload
                  :action="axios.defaults.baseURL+'/api/image/avatar'"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :on-success="uploadSuccess"
                  :headers="accessHeader()"
              >
                <el-button round size="small">修改头像</el-button>
              </el-upload>
            </div>
            <div style="font-weight: bold">你好，{{ store.user.username }}</div>
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;color: grey;padding: 10px">
            {{ synopsis || '这个用户很懒，没有填写个人简介~' }}
          </div>
        </card>
        <card style="margin-top: 10px;font-size: 14px">
          <div>账号注册时间: {{ registerTime }}</div>
          <div style="color: grey">欢迎加入我们的书店！</div>
        </card>
      </div>
    </div>
  </div>
</template>

<style scoped>

.setting-left {
  flex: 1;
  margin: 20px;
}

.setting-right {
  width: 300px;
  margin: 20px;
}

</style>