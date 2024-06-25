<script setup>
import {computed, onMounted, reactive, ref, watchEffect} from "vue";
import {accessHeader, get, post} from "@/net";
import {useStore} from "@/store";
import axios from "axios";
import {ElMessage} from "element-plus";

const store = useStore()
const page = ref(1)
const pageIndex = ref(0)
const pageSize = ref(5)
const totalItems = ref(0)
const searchText = ref('')
const indents = ref([])
const userId = ref(0)

// 转换时间格式的函数
function formatBeijingTime(isoString) {
  const date = new Date(isoString);
  const beijingTime = date.toLocaleString('zh-CN', {timeZone: 'Asia/Shanghai'});
  const [datePart, timePart] = beijingTime.split(' ');
  const [year, month, day] = datePart.split('/');
  const [hours, minutes, seconds] = timePart.split(':');

  return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')} ${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}:${seconds.padStart(2, '0')}`;
}


watchEffect(() => {
  userId.value = store.user.id
  console.log(userId.value)
})

onMounted(() => {
  getOrders(pageIndex.value, pageSize.value, searchText.value)
})

const handlePageChange = (newPage) => {
  page.value = newPage
  pageIndex.value = newPage - 1
  getOrders(pageIndex.value, pageSize.value, searchText.value)
}

function getOrders(pageIndex, pageSize, searchText) {
  get('/indent/person-page?pageIndex=' + pageIndex + '&pageSize=' + pageSize + '&searchText=' + searchText + '&accountId=' + userId.value, (data) => {
    console.log(data)
    indents.value = data.rows
    indents.value.forEach(order => {
      order.createTime = formatBeijingTime(order.createTime)
      if (order.paymentTime != null)
        order.paymentTime = formatBeijingTime(order.paymentTime)
      if (order.shippingTime != null)
        order.shippingTime = formatBeijingTime(order.shippingTime)
      if (order.finishTime != null)
        order.finishTime = formatBeijingTime(order.finishTime)
    })
    console.log(indents.value)
    totalItems.value = data.totalRowCount
  })
}

function searchOrders() {
  console.log(searchText)
  getOrders(pageIndex.value, pageSize.value, searchText.value)
}

function getStatusText(status) {
  if (status === 0) {
    return '未付款'
  } else if (status === 1) {
    return '未发货'
  } else if (status === 2) {
    return '已发货'
  } else if (status === 3) {
    return '已完成'
  }
}

function getStatusType(status) {
  if (status === 0) {
    return 'danger'
  } else if (status === 1) {
    return 'warning'
  } else if (status === 2) {
    return 'info'
  } else if (status === 3) {
    return 'success'
  }
}

const indentForm = reactive({
  addressName: '',
  addressPhone: '',
  addressAddress: '',
  bookName: '',
  createTime: '',
  shippingTime: '',
  finishTime: '',
  paymentTime: '',
  bookCover: '',
  bookAuthor: '',
  bookIsbn: '',
  bookPress: ''
})

const dialogFormVisible = ref(false)
const payVisible = ref(false)
const receiveVisible = ref(false)

const look = async (row) => {
  dialogFormVisible.value = true
  const selectedBook = indents.value.find(indent => indent.id === row.id)
  if (selectedBook) {
    Object.assign(indentForm, selectedBook)
  }
}

const payOrReceiveForm = reactive({
  id: -1,
  status: 0,
})

const showPay = async (row) => {
  payVisible.value = true
  payOrReceiveForm.id = row.id
  payOrReceiveForm.status = 1
}

const pay = async () => {
  console.log(payOrReceiveForm)
  post('/indent/status', payOrReceiveForm, () => {
    ElMessage.success("付款成功！")
    payVisible.value = false
    getOrders(pageIndex.value, pageSize.value, searchText.value)
  })
}

const showReceive = async (row) => {
  receiveVisible.value = true
  payOrReceiveForm.id = row.id
  payOrReceiveForm.status = 3
}

const receive = async () => {
  console.log(payOrReceiveForm)
  post('/indent/status', payOrReceiveForm, () => {
    ElMessage.success("确认收货成功！")
    receiveVisible.value = false
    getOrders(pageIndex.value, pageSize.value, searchText.value)
  })
}

const updateVisible = ref(false)
const updateFormRef = ref(null)
const updateForm = reactive({
  id: -1,
  totalNumber: 0,
  totalPrice: 0.0,
  addressId: ''
})

let price = ref(0)

const showUpdate = async (row) => {
  updateVisible.value = true
  updateForm.id = row.id
  updateForm.totalNumber = row.totalNumber
  console.log(row.totalPrice)
  price.value = row.totalPrice / row.totalNumber
  console.log(updateForm)
}

const updateIndent = async () => {
  if (updateForm.addressId === '') {
    ElMessage.error("请选择地址")
  } else {
    updateFormRef.value.validate(isValid => {
      if (isValid) {
        console.log(price.value)
        updateForm.totalPrice = updateForm.totalNumber * price.value
        console.log(updateForm)
        post('/indent/person-update', updateForm, () => {
          updateVisible.value = false
          ElMessage.success("修改成功！")
          getOrders(pageIndex.value, pageSize.value, searchText.value)
        })
      }
    })
  }
}

const validateNumber = (rule, value, callback) => {
  if (value < 0) {
    callback(new Error('数量不能小于0'));
  } else if (value !== 0 && !Number.isInteger(value)) {
    callback(new Error('数量必须为整数'));
  } else {
    callback();
  }
}

const rules = {
  totalNumber: [
    {required: true, message: '数量大于等于0', trigger: 'blur'},
    {validator: validateNumber, trigger: 'blur'}
  ]
}
</script>

<template>
  <div class="search-bar">
    <el-input v-model="searchText" placeholder="请输入书名" clearable
              @clear="searchOrders"
              style="width: 200px; margin-right: 10px;"></el-input>
    <el-button type="primary" plain @click="searchOrders">搜索</el-button>
  </div>
  <div>
    <el-table :data="indents">
      <el-table-column prop="id" label="订单id"></el-table-column>
      <el-table-column prop="bookName" label="书名"></el-table-column>
      <el-table-column prop="totalNumber" label="数量"></el-table-column>
      <el-table-column prop="totalPrice" label="总价"></el-table-column>
      <el-table-column prop="createTime" label="下单时间"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180px" align="center">
        <template #default="scope">
          <el-row>
            <el-col :span="8" v-if="scope.row.status !== 4 ">
              <el-button type="primary" plain v-if="scope.row.status===0" size="small" @click="showPay(scope.row)">
                付款
              </el-button>
              <el-button type="primary" plain v-if="scope.row.status===2" size="small" @click="showReceive(scope.row)">
                收货
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button @click="look(scope.row)" type="primary" plain size="small">
                查看
              </el-button>
            </el-col>
            <el-col :span="8" v-if="scope.row.status<2">
              <el-button type="warning" plain size="small" @click="showUpdate(scope.row)">
                修改
              </el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
  </div>


  <el-dialog v-model="dialogFormVisible" title="查看订单" width="500px" align-center>
    <el-form :model="indentForm" label-width="80px">
      <el-form-item label="封面" prop="bookCover">
        <el-image :src="`${axios.defaults.baseURL}/images${indentForm.bookCover}`"
                  style="width: 100px; height: 150px; margin-bottom: 10px;"
                  fit="cover">
        </el-image>
      </el-form-item>
      <el-form-item label="书名" prop="bookName">
        {{ indentForm.bookName }}
      </el-form-item>
      <el-form-item label="作者" prop="author">
        {{ indentForm.bookAuthor }}
      </el-form-item>
      <el-form-item label="出版社" prop="press">
        {{ indentForm.bookPress }}
      </el-form-item>
      <el-form-item label="ISBN" prop="isbn">
        {{ indentForm.bookIsbn }}
      </el-form-item>
      <el-form-item label="收货信息">
        {{ indentForm.addressName + "，" + indentForm.addressPhone + "，" + indentForm.addressAddress }}
      </el-form-item>
      <el-form-item label="下单时间" prop="createTime">
        {{ indentForm.createTime }}
      </el-form-item>
      <el-form-item label="支付时间" prop="paymentTime">
        {{ indentForm.paymentTime }}
      </el-form-item>
      <el-form-item label="发货时间" prop="shippingTime">
        {{ indentForm.shippingTime }}
      </el-form-item>
      <el-form-item label="完成时间" prop="finishTime">
        {{ indentForm.finishTime }}
      </el-form-item>

    </el-form>
  </el-dialog>

  <el-dialog
      v-model="payVisible"
      title="Tips"
      width="500"
  >
    <span>确定支付吗</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="danger" plain @click="pay">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
      v-model="receiveVisible"
      title="Tips"
      width="500"
  >
    <span>确认收货吗</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="receiveVisible = false">取消</el-button>
        <el-button type="danger" plain @click="receive">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="updateVisible" title="修改订单" align-center>
    <el-form :model="updateForm" ref="updateFormRef" :rules="rules">
      <el-form-item prop="totalNumber" label="总数">
        <el-input-number :min="0" v-model="updateForm.totalNumber"></el-input-number>
      </el-form-item>
      <el-form-item>
        <el-select v-model="updateForm.addressId" placeholder="收货信息">
          <el-option
              v-for="option in store.user.addresses"
              :key="option.id"
              :label="`${option.name} ${option.phone } ${option.address}`"
              :value="option.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="updateVisible = false">取消</el-button>
        <el-button @click="updateIndent" type="primary">确定</el-button>
      </div>
    </template>
  </el-dialog>


  <div class="pagination-container">
    <el-pagination
        :current-page="page"
        :page-size="pageSize"
        layout="total, prev, pager, next"
        :total="totalItems"
        @current-change="handlePageChange">
    </el-pagination>
  </div>
</template>

<style scoped>
.search-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 15px;
}

.pagination-container {
  display: flex;
  justify-content: center;
}

.el-form-item {
  margin-bottom: 5px; /* 调整表单项之间的下方间距 */
}
</style>