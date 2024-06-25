<script setup>
import {onMounted, reactive, ref} from "vue";
import {get, post} from "@/net";
import axios from "axios";
import {ElMessage} from "element-plus";
import {useStore} from "@/store";

const store = useStore()
const page = ref(1)
const pageIndex = ref(0)
const pageSize = ref(5)
const totalItems = ref(0)
const searchText = ref('')
const indents = ref([])

onMounted(() => {
  getOrders(pageIndex.value, pageSize.value, searchText.value)
})

function formatBeijingTime(isoString) {
  const date = new Date(isoString);
  const beijingTime = date.toLocaleString('zh-CN', {timeZone: 'Asia/Shanghai'});
  const [datePart, timePart] = beijingTime.split(' ');
  const [year, month, day] = datePart.split('/');
  const [hours, minutes, seconds] = timePart.split(':');

  return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')} ${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}:${seconds.padStart(2, '0')}`;
}

const handlePageChange = (newPage) => {
  page.value = newPage
  pageIndex.value = newPage - 1
  getOrders(pageIndex.value, pageSize.value, searchText.value)
}

function getOrders(pageIndex, pageSize, searchText) {
  get('/indent/page?pageIndex=' + pageIndex + '&pageSize=' + pageSize + '&searchText=' + searchText, (data) => {
    console.log(data)
    indents.value = data.rows
    indents.value.forEach(order => {
      order.createTime = formatBeijingTime(order.createTime);
      if (order.paymentTime != null)
        order.paymentTime = formatBeijingTime(order.paymentTime)
      if (order.shippingTime != null)
        order.shippingTime = formatBeijingTime(order.shippingTime)
      if (order.finishTime != null)
        order.finishTime = formatBeijingTime(order.finishTime)
    })
    totalItems.value = data.totalRowCount
  })
}

function searchOrders() {
  console.log(searchText)
  getOrders(pageIndex.value, pageSize.value, searchText.value)
}

const indentForm = reactive({
  accountUsername: '',
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

const dialogFormVisible = ref(false)
const shippingVisible = ref(false)

const look = async (row) => {
  dialogFormVisible.value = true
  const selectedBook = indents.value.find(indent => indent.id === row.id)
  if (selectedBook) {
    Object.assign(indentForm, selectedBook)
  }
}

const shippingForm = reactive({
  id: -1,
  status: 0,
})

const showShip = async (row) => {
  shippingVisible.value = true
  shippingForm.id = row.id
  shippingForm.status = 2
}

const shipping = async () => {
  console.log(showShip)
  post('/indent/status', shippingForm, () => {
    ElMessage.success("确认收货成功！")
    shippingVisible.value = false
    getOrders(pageIndex.value, pageSize.value, searchText.value)
  })
}

const updateVisible = ref(false)
const updateFormRef = ref(null)
const updateForm = reactive({
  id: -1,
  addressId: ''
})


const showUpdate = async (row) => {
  console.log(1111)
  updateVisible.value = true
  updateForm.id = row.id
}

const updateIndent = async () => {
  if (updateForm.addressId === '') {
    ElMessage.error("请选择地址")
  } else {
        post('/indent/update', updateForm, () => {
          updateVisible.value = false
          ElMessage.success("修改成功！")
          getOrders(pageIndex.value, pageSize.value, searchText.value)
        })
      }
}


</script>

<template>
  <div class="search-bar">
    <el-input v-model="searchText" placeholder="请输入书名/订单id" clearable
              @clear="searchOrders"
              style="width: 200px; margin-right: 10px;"></el-input>
    <el-button type="primary" plain @click="searchOrders">搜索</el-button>
  </div>
  <div>
    <el-table :data="indents">
      <el-table-column prop="id" label="订单id"></el-table-column>
      <el-table-column prop="accountUsername" label="用户名"></el-table-column>
      <el-table-column prop="bookName" label="书名"></el-table-column>
      <el-table-column prop="totalNumber" label="数量"></el-table-column>
      <el-table-column prop="totalPrice" label="总价"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="230px" align="center">
        <template #default="scope">
          <el-row>
            <el-col :span="6" v-if="scope.row.status !== 4 ">
              <el-button type="primary" v-if="scope.row.status === 1 " plain size="small" @click="showShip(scope.row)">
                发货
              </el-button>
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="look(scope.row)" plain size="small">
                查看
              </el-button>
            </el-col>
            <el-col :span="6">
              <el-button type="warning" @click="showUpdate(scope.row)" plain v-if="scope.row.status!==3" size="small">
                修改
              </el-button>
            </el-col>
            <el-col :span="6">
              <el-button type="warning" plain size="small">
                删除
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
      <el-form-item label="用户名">
        {{ indentForm.accountUsername }}
      </el-form-item>
      <el-form-item label="书名">
        {{ indentForm.bookName }}
      </el-form-item>
      <el-form-item label="作者">
        {{ indentForm.bookAuthor }}
      </el-form-item>
      <el-form-item label="出版社">
        {{ indentForm.bookPress }}
      </el-form-item>
      <el-form-item label="ISBN">
        {{ indentForm.bookIsbn }}
      </el-form-item>
      <el-form-item label="收货信息">
        {{ indentForm.addressName + "，" + indentForm.addressPhone + "，" + indentForm.addressAddress }}
      </el-form-item>
      <el-form-item label="下单时间">
        {{ indentForm.createTime }}
      </el-form-item>
      <el-form-item label="支付时间">
        {{ indentForm.paymentTime }}
      </el-form-item>
      <el-form-item label="发货时间">
        {{ indentForm.shippingTime }}
      </el-form-item>
      <el-form-item label="完成时间">
        {{ indentForm.finishTime }}
      </el-form-item>

    </el-form>
  </el-dialog>

  <el-dialog
      v-model="shippingVisible"
      title="Tips"
      width="500"
  >
    <span>确认发货吗</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="shippingVisible = false">取消</el-button>
        <el-button type="danger" plain @click="shipping">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="updateVisible" title="修改订单" align-center>
    <el-form :model="updateForm">
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

  <el-dialog v-model="deleteIndentVisible" title="Tips" width="500">
    <span>你确定要删除这个订单吗?</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="deleteDialogDeleteVisible = false">取消</el-button>
        <el-button type="danger" plain @click="deleteBook">确定</el-button>
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
  margin-bottom: 3px; /* 调整表单项之间的下方间距 */
}
</style>