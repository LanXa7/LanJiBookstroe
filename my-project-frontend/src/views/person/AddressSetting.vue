<script setup>

import {useStore} from "@/store";
import {onMounted, reactive, ref} from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import {Edit, Plus} from "@element-plus/icons-vue";

onMounted(() => {
  getAddress()
})

const addDialogFormVisible = ref(false)
const store = useStore()
const addresses = ref([])
//addresses.value = store.user.addresses
const dialogFormVisible = ref(false)
const addressFormRef = ref(null)
const addAddressFormRef = ref(null)
const addressForm = reactive({
      id: -1,
      name: '',
      phone: '',
      address: ''
    }
)


const addAddressForm = reactive({
      name: '',
      phone: '',
      address: '',
      accountIds: []
    }
)

const deleteDialogDeleteVisible = ref(false)

function validatePhone(rule, value, callback) {
  const phoneRegex = /^1[3-9]\d{9}$/;//匹配11位中国手机号的正则表达式
  if (!value) {
    return callback(new Error('请输入电话号码'))
  } else if (!phoneRegex.test(value)) {
    return callback(new Error('请输入有效的11位电话号码'))
  } else {
    return callback()
  }
}

const rules = {
  name: [
    {required: true, message: '请输入名字', trigger: 'blur'}
  ],
  address: [
    {required: true, message: '请输入地址', trigger: 'blur'}
  ],
  phone: [
    {required: true, message: '请输入电话号码', trigger: 'blur'},
    {validator: validatePhone, trigger: 'blur'}
  ]

}

const showEditDialog = async (row) => {
  // 在点击修改按钮时，显示弹窗，并填充表单数据
  dialogFormVisible.value = true
  const selectedBook = addresses.value.find(address => address.id === row.id)
  if (selectedBook) {
    Object.assign(addressForm, selectedBook)
  }
}

const showDeleteDialog = async (row) => {
  deleteDialogDeleteVisible.value = true
  addressForm.id = row.id
}

const showAddDialog = () => {
  addDialogFormVisible.value = true
}

function getAddress() {
  get('/address/get-address', (data) => {
    addresses.value = data[0].addresses
    store.user.addresses = addresses.value
  })
}

function updateAddress() {
  addressFormRef.value.validate(isValid => {
    if (isValid) {
      post('/address/change', addressForm, () => {
        ElMessage.success('收货信息修改成功')
        getAddress()
        dialogFormVisible.value = false
      })
    }
  })
}

function addAddress() {
  addAddressFormRef.value.validate(isValid => {
    if (isValid) {
      console.log(store.user.id)
      addAddressForm.accountIds.push(store.user.id)
      console.log(addAddressForm.accountIds)
      post('/address/add', addAddressForm, () => {
        ElMessage.success('收货信息添加成功')
        getAddress()
        addDialogFormVisible.value = false
      })
    }
  })
}

function deleteAddress() {
  get('/address/delete?addressId=' + addressForm.id, () => {
    ElMessage.success("删除图书成功")
    getAddress()
    deleteDialogDeleteVisible.value = false
  }, (message) => {
    ElMessage.warning(message)
  })
}

</script>

<template>
  <el-table :data="addresses" style="width: 100%">
    <el-table-column type="selection" width="55" align="center"/>
    <el-table-column prop="id" label="ID" width="50" align="center"/>
    <el-table-column prop="name" label="收件人姓名" width="100" align="center"/>
    <el-table-column prop="phone" label="收件人手机号" width="120" align="center"/>
    <el-table-column prop="address" label="收件人地址" align="center"/>
    <el-table-column label="操作" width="150px" align="center">
      <template #default="scope">
        <el-row>
          <el-col :span="12">
            <el-button type="primary" plain size="small" @click="showEditDialog(scope.row)">
              修改
            </el-button>
          </el-col>
          <el-col :span="12">
            <el-button type="danger" plain size="small" @click="showDeleteDialog(scope.row)">
              删除
            </el-button>
          </el-col>
        </el-row>
      </template>
    </el-table-column>
  </el-table>

  <div class="bottom-right-button">
    <el-button type="primary" :icon="Plus" circle @click="showAddDialog"/>
  </div>

  <el-dialog v-model="dialogFormVisible" title="编辑收件地址" width="500px" align-center>
    <el-form :model="addressForm" ref="addressFormRef" :rules="rules" class="inline-form">
      <el-form-item label="收件人姓名" prop="name">
        <el-input v-model="addressForm.name"></el-input>
      </el-form-item>
      <el-form-item label="收件人电话" prop="phone">
        <el-input maxlength="11" v-model="addressForm.phone"></el-input>
      </el-form-item>
      <el-form-item label="收件人地址" prop="address">
        <el-input v-model="addressForm.address"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button @click="updateAddress" type="primary">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="addDialogFormVisible" title="添加收件地址" width="500px" align-center>
    <el-form :model="addAddressForm" ref="addAddressFormRef" :rules="rules" class="inline-form">
      <el-form-item label="收件人姓名" prop="name">
        <el-input v-model="addAddressForm.name"></el-input>
      </el-form-item>
      <el-form-item label="收件人电话" prop="phone">
        <el-input maxlength="11" v-model="addAddressForm.phone"></el-input>
      </el-form-item>
      <el-form-item label="收件人地址" prop="address">
        <el-input v-model="addAddressForm.address"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button @click="addAddress" type="primary">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
      v-model="deleteDialogDeleteVisible"
      title="Tips"
      width="500"
  >
    <span>你确定要删除这个收件信息吗?</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="deleteDialogDeleteVisible = false">取消</el-button>
        <el-button type="danger" plain @click="deleteAddress">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

</template>

<style scoped>
.inline-form .el-form-item {
  display: flex;
  align-items: center;
}

.inline-form .el-form-item__label {
  margin-right: 10px; /* 调整标签与输入框之间的间距 */
}

.inline-form .el-form-item__content {
  flex-grow: 1;
}

.bottom-right-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  transform: scale(1.5); /* 放大 20% */
}
</style>