<script setup>
import {computed, onMounted, reactive, ref, toRefs} from 'vue';
import 'element-plus/dist/index.css';
import {accessHeader, get, post} from "@/net";
import {useStore} from "@/store";
import {ElMessage, ElMessageBox} from "element-plus";
import axios from "axios";

const store = useStore()

const books = ref([])
const pageIndex = ref(0)
const pageSize = ref(5)
const totalItems = ref(0)
const dialogFormVisible = ref(false)
const addDialogFormVisible = ref(false)
const deleteDialogDeleteVisible = ref(false)
const page = ref(1)
const searchText = ref('')
const bookFormRef = ref(null)
const addBookFormRef = ref(null)

const bookForm = reactive({
      id: -1,
      name: '',
      cover: '',
      author: '',
      press: '',
      isbn: '',
      number: -1,
      price: 0.0,
      typeId: -1,
      status: -1
    }
)

const defaultBookForm = {
  name: '',
  cover: '',
  author: '',
  press: '',
  isbn: '',
  number: -1,
  price: 0.0,
  typeId: '',
  status: 0
}

const addBookForm = reactive({
      name: '',
      cover: '',
      author: '',
      press: '',
      isbn: '',
      number: -1,
      price: 0.0,
      typeId: '',
      status: 0
    }
)

const validateNumber = (rule, value, callback) => {
  if (value < 0) {
    callback(new Error('数量不能小于0'));
  } else if (value !== 0 && !Number.isInteger(value)) {
    callback(new Error('数量必须为整数'));
  } else {
    callback();
  }
}

const validatePrice = (rule, value, callback) => {
  const regex = /^\d+(\.\d{1,2})?$/;
  if (value <= 0) {
    callback(new Error('价格必须大于0'));
  } else if (!regex.test(value)) {
    callback(new Error('价格最多两位小数'));
  } else {
    callback();
  }
}

const rules = {
  name: [
    {required: true, message: '请输入书名', trigger: 'blur'}
  ],
  author: [
    {required: true, message: '请输入作者', trigger: 'blur'}
  ],
  press: [
    {required: true, message: '请输入出版社', trigger: 'blur'}
  ],
  isbn: [
    {required: true, message: '请输入isbn', trigger: 'blur'}
  ],
  number: [
    {required: true, message: '数量大于等于0', trigger: 'blur'},
    {validator: validateNumber, trigger: 'blur'}
  ],
  price: [
    {required: true, message: '数量大于等于0', trigger: 'blur'},
    {validator: validatePrice, trigger: 'blur'}
  ],
  status: []
}

const showAddDialog = () => {
  addDialogFormVisible.value = true
}

const showEditDialog = async (row) => {
  // 在点击修改按钮时，显示弹窗，并填充表单数据
  dialogFormVisible.value = true
  //console.log(store.avatarUrl)
  //console.log(row.cover)
  //console.log(`${axios.defaults.baseURL}/images${bookForm.cover}`)
  // 填充表单数据，例如：
  const selectedBook = books.value.find(book => book.id === row.id)
  if (selectedBook) {
    Object.assign(bookForm, selectedBook)
  }
  //console.log(`${axios.defaults.baseURL}/images${bookForm.cover}`)
  //console.log(bookForm)
}

const changeBookStatus = async (row) => {
  const newStatus = row.status === 0 ? 1 : 0
  //console.log(row.status)
  bookForm.id = row.id
  bookForm.status = newStatus
  // console.log(bookForm.status)
  // console.log(bookForm)
  post('/book/change-status', bookForm, () => {
    if (row.status === 0) {
      ElMessage.success("下架成功")
    } else {
      ElMessage.success("上架成功")
    }
    getBooks(pageIndex.value, pageSize.value, searchText.value)
  }, (message) => {
    ElMessage.warning(message)
  })
}

const showDeleteDialog = async (row) => {
  deleteDialogDeleteVisible.value = true
  bookForm.id = row.id
}

const deleteBook = async () => {
  console.log(bookForm.id)
  get('/book/delete?id=' + bookForm.id, () => {
    ElMessage.success("删除图书成功")
    getBooks(pageIndex.value, pageSize.value, searchText.value)
    deleteDialogDeleteVisible.value = false
  }, (message) => {
    ElMessage.warning(message)
  })
}


function getBooks(pageIndex, pageSize, searchText) {
  //console.log('/book/page?pageIndex=' + pageIndex + '&pageSize=' + pageSize + '&searchText=' + searchText)
  get('/book/admin-page?pageIndex=' + pageIndex + '&pageSize=' + pageSize + '&searchText=' + searchText, (data) => {
    books.value = data.rows
    console.log(data)
    totalItems.value = data.totalRowCount
  })
}

function getType() {
  get('/type/list', (data) => {
    store.type = data
  })
}


onMounted(() => {
  getBooks(pageIndex.value, pageSize.value, searchText.value);
  getType()
})


const handlePageChange = (newPage) => {
  page.value = newPage
  pageIndex.value = newPage - 1
  getBooks(pageIndex.value, pageSize.value, searchText.value)
}

function updateBook() {
  bookFormRef.value.validate(isValid => {
    if (isValid) {
      post('/book/update', bookForm, () => {
        ElMessage.success('图书信息修改成功')
        getBooks(pageIndex.value, pageSize.value, searchText.value)
        dialogFormVisible.value = false
      })
    }
  })
}

function addBook() {
  addBookFormRef.value.validate(isValid => {
    if (isValid) {
      post('/book/add', addBookForm, () => {
        ElMessage.success('新增图书成功')
        getBooks(pageIndex.value, pageSize.value, searchText.value)
        addDialogFormVisible.value = false
        Object.assign(addBookForm, defaultBookForm)
      })
    }
  })
}

const uploadData = computed(() => {
  return {id: bookForm.id}
})

const insertUploadData = computed(() => {
  return {id: 0}
})


function beforeCoverUpload(rawFile) {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片只能是 JPG/PNG 格式的')
    return false
  } else if (rawFile.size / 1024 > 100) {
    ElMessage.error('图片大小不能大于 100KB')
    return false
  }
  return true
}

function uploadSuccess(response) {
  bookForm.cover = response.data
  ElMessage.success('图片上传成功')
}

function insertUpLoadSuccess(response) {
  addBookForm.cover = response.data
  ElMessage.success('图片上传成功')
}


function searchBooks() {
  console.log(searchText)
  getBooks(pageIndex.value, pageSize.value, searchText.value)
}


</script>

<template>
  <div class="book-manager">
    <div class="search-bar">
      <el-button type="primary" plain @click="showAddDialog">新增</el-button>
      <el-input v-model="searchText" placeholder="请输入书名/作者" clearable
                @clear="searchBooks"
                style="width: 200px; margin-right: 10px; margin-left: 800px"></el-input>
      <el-button type="primary" plain @click="searchBooks">搜索</el-button>
    </div>
    <el-table :data="books" style="width: 100%">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column prop="id" label="ID" width="50" align="center"/>
      <el-table-column prop="name" label="书名" width="100" align="center"/>
      <el-table-column prop="author" label="作者" align="center"/>
      <el-table-column label="种类" align="center">
        <template #default="{row}">
          {{ store.type[row.typeId - 1].name }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
            {{ scope.row.status === 0 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="number" label="数量" align="center"/>
      <el-table-column prop="price" label="单价" align="center"/>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-row>
            <el-col :span="8">
              <el-button type="primary" plain size="small" @click="showEditDialog(scope.row)">
                修改
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="warning" plain size="small" @click="changeBookStatus(scope.row)">
                {{ scope.row.status === 0 ? '下架' : '上架' }}
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="danger" plain size="small" @click="showDeleteDialog(scope.row)">
                删除
              </el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogFormVisible" title="编辑书籍信息" width="500px" align-center>
      <el-form :model="bookForm" :rules="rules" ref="bookFormRef" label-width="80px">
        <el-form-item label="封面" prop="cover">
          <el-upload
              :action="axios.defaults.baseURL+'/api/image/cover'"
              :data="uploadData"
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
              :on-success="uploadSuccess"
              :headers="accessHeader()"
          >
            <el-image :src="`${axios.defaults.baseURL}/images${bookForm.cover}`"
                      style="width: 100px; height: 150px; margin-bottom: 10px;"
                      fit="cover">
            </el-image>
          </el-upload>
        </el-form-item>
        <el-form-item label="书名" prop="name">
          <el-input v-model="bookForm.name"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author"></el-input>
        </el-form-item>
        <el-form-item label="出版社" prop="press">
          <el-input v-model="bookForm.press"></el-input>
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn"></el-input>
        </el-form-item>
        <el-form-item label="数量" prop="number">
          <el-input-number :min="0" v-model="bookForm.number" type="number"></el-input-number>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number :min="0" v-model="bookForm.price" type="number" step="0.01"></el-input-number>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="bookForm.typeId" placeholder="Select">
            <el-option
                v-for="option in store.type"
                :key="option.id"
                :label="option.name"
                :value="option.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button @click="updateBook" type="primary">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="addDialogFormVisible" title="新增图书" width="500px" align-center>
      <el-form :model="addBookForm" :rules="rules" ref="addBookFormRef" label-width="80px">
        <el-form-item label="封面" prop="cover">
          <el-upload
              :action="axios.defaults.baseURL+'/api/image/cover'"
              :data="insertUploadData"
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
              :on-success="insertUpLoadSuccess"
              :headers="accessHeader()"
          >
            <el-image :src="`${axios.defaults.baseURL}/images${addBookForm.cover}`"
                      style="width: 100px; height: 150px; margin-bottom: 10px;"
                      fit="cover">
            </el-image>
          </el-upload>
        </el-form-item>
        <el-form-item label="书名" prop="name">
          <el-input v-model="addBookForm.name"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="addBookForm.author"></el-input>
        </el-form-item>
        <el-form-item label="出版社" prop="press">
          <el-input v-model="addBookForm.press"></el-input>
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="addBookForm.isbn"></el-input>
        </el-form-item>
        <el-form-item label="数量" prop="number">
          <el-input-number :min="0" v-model="addBookForm.number" type="number"></el-input-number>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number :min="0" v-model="addBookForm.price" type="number" step="0.01"></el-input-number>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="addBookForm.typeId" placeholder="类型">
            <el-option
                v-for="option in store.type"
                :key="option.id"
                :label="option.name"
                :value="option.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addDialogFormVisible = false">取消</el-button>
          <el-button @click="addBook" type="primary">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
        v-model="deleteDialogDeleteVisible"
        title="Tips"
        width="500"
    >
      <span>你确定要删除这本图书吗?</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="deleteDialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" plain @click="deleteBook">
            确定
          </el-button>
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
  </div>

</template>

<style scoped>
.book-manager {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  padding: 20px;
}

.search-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 15px;
}

.el-table {
  flex-grow: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
}

.el-pagination {
  margin-top: 10px; /* 调整顶部间距 */
  /* 添加其他样式属性以适应你的设计需求 */
}

.el-form-item {
  margin-bottom: 5px; /* 调整表单项之间的下方间距 */
}

.el-table th, .el-table td {
  text-align: center;
}

</style>


