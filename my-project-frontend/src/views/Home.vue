<script setup>

import Card from "@/components/Card.vue";
import {Notebook, Plus} from "@element-plus/icons-vue";
import {get, post, unauthorized} from "@/net";
import {computed, onMounted, reactive, ref} from "vue";
import axios from "axios";
import BookCard from "@/components/BookCard.vue";
import {useStore} from "@/store";
import {ElMessage} from "element-plus";
import router from "@/router";

onMounted(() => {
  getBookList()
  getType()
  getBooks(pageIndex.value, pageSize.value, '', '')
})

const store = useStore()

const pageIndex = ref(0);
const pageSize = ref(10);
const totalItems = ref(0);
const page = ref(1)
const searchText = ref('')

const handlePageChange = (newPage) => {
  page.value = newPage
  pageIndex.value = newPage - 1
  getBooks(pageIndex.value, pageSize.value, '', selectedType.value)
}

function getBookList() {
  get('/book/list', (data) => {
    const validData = data.filter(item => item.status !== 1);
    carouselItems.value = validData.slice().sort(() => Math.random() - 0.5).slice(0, 5);
    console.log(data)
    console.log(carouselItems)
  })
}

function fillBookList(bookList, data) {
  let dataIndex = 0

  for (let i = 0; i < bookList.length; i++) {
    for (let j = 0; j < bookList[i].length; j++) {
      if (dataIndex < data.length) {
        if (data[dataIndex].status) {
          dataIndex++
        }
        bookList[i][j] = data[dataIndex]
        dataIndex++
      } else {
        bookList[i][j] = null
      }
    }
  }

  console.log(bookList)
}

const rows = 2    // 行数
const columns = 5 // 列数
const books = ref(Array.from({length: rows}, () => Array(columns).fill(null)))

const maxColumns = computed(() => {
  return books.value.reduce((max, row) => Math.max(max, row.length), 0)
})

function getBooks(pageIndex, pageSize, searchText, typeId) {
  //console.log('/book/page?pageIndex=' + pageIndex + '&pageSize=' + pageSize + '&searchText=' + searchText)
  get('/book/page?pageIndex='
      + pageIndex + '&pageSize='
      + pageSize + '&searchText='
      + searchText + '&typeId=' + typeId, (data) => {
    fillBookList(books.value, data.rows)
    console.log(1111)
    console.log(books.value)
    totalItems.value = data.totalRowCount
  })
}

function getType() {
  get('/type/list', (data) => {
    store.type = data
  })
}

function searchBooks() {
  console.log(searchText)
  getBooks(pageIndex.value, pageSize.value, searchText.value, '')
}

const carouselItems = ref([])


const selectedType = ref('')

function pageByType() {
  getBooks(pageIndex.value, pageSize.value, '', selectedType.value)
}

const drawer = ref(false)

const bookItem = reactive({
  id: -1,
  name: '',
  cover: '',
  author: '',
  press: '',
  isbn: '',
  status: 0,
  number: 1,
  price: ''
})


const bookItemRef = ref(null)

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
  number: [
    {required: true, message: '数量大于等于0', trigger: 'blur'},
    {validator: validateNumber, trigger: 'blur'}
  ]
}

const showDrawer = async (row) => {
  drawer.value = true
  console.log(row)
  bookItem.value = row
  bookItem.value.number = 1
};


const isUnauthorized = unauthorized()

function addCart() {
  if (isUnauthorized) {
    router.push('/welcome');
  }
  bookItemRef.value.validate(isValid => {
    if (isValid) {
      post('/cart/add', bookItem.value, () => {
        ElMessage.success('加入购物车成功！')
        drawer.value = false
      })
    }
  })
}


</script>

<template>
  <div>
    <card :icon="Notebook" title="每日推荐图书">
      <el-carousel :interval="4000" type="card" height="200px">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <!-- 在这里自定义每个carousel item的内容 -->
          <img style="object-fit: scale-down;height: 100%;width: 100%"
               :src="`${axios.defaults.baseURL}/images${item.cover}`" class="image" alt=""/>
        </el-carousel-item>
      </el-carousel>
    </card>
  </div>
  <div class="search-bar">
    <el-select v-model="selectedType" clearable placeholder="类型" @change="pageByType">
      <el-option
          v-for="option in store.type"
          :key="option.id"
          :label="option.name"
          :value="option.id"
      />
    </el-select>
    <el-input v-model="searchText" placeholder="请输入书名/作者" clearable
              @clear="searchBooks"
              style="width: 200px; margin-right: 10px; margin-left: 650px"></el-input>
    <el-button type="primary" plain @click="searchBooks">搜索</el-button>
  </div>
  <div>
    <el-table :data="books" style="width: 100%">
      <el-table-column v-for="columnIndex in maxColumns" :key="columnIndex" :width="230"
      >
        <template #default="scope">
          <div v-if="scope.row[columnIndex-1]" style="align-items: center">
            <book-card :name="scope.row[columnIndex-1].name"
                       :author="scope.row[columnIndex-1].author"
                       :price="scope.row[columnIndex-1].price"
                       @click="showDrawer(scope.row[columnIndex-1])">
              <el-image :src="`${axios.defaults.baseURL}/images${scope.row[columnIndex-1].cover}`"
                        style="width: 100px; height: 150px; margin-bottom: 10px;"
                        fit="cover">
              </el-image>
            </book-card>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-drawer
      v-model="drawer"
      title="加购"
  >
    <el-form :model="bookItem.value" ref="bookItemRef" :rules="rules" label-width="80px">
      <el-form-item label="封面" prop="cover">
        <el-image :src="`${axios.defaults.baseURL}/images${bookItem.value.cover}`"
                  style="width: 100px; height: 150px; margin-bottom: 10px;"
                  fit="cover">
        </el-image>
      </el-form-item>
      <el-form-item label="书名" prop="name">
        {{ bookItem.value.name }}
      </el-form-item>
      <el-form-item label="作者" prop="author">
        {{ bookItem.value.author }}
      </el-form-item>
      <el-form-item label="出版社" prop="press">
        {{ bookItem.value.press }}
      </el-form-item>
      <el-form-item label="ISBN" prop="isbn">
        {{ bookItem.value.isbn }}
      </el-form-item>
      <el-form-item label="数量" prop="number">
        <el-input-number :min="0" v-model="bookItem.value.number" type="number"></el-input-number>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        {{ bookItem.value.price }}
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="drawer=false">取消</el-button>
        <el-button @click="addCart">确定</el-button>
      </div>
    </template>
  </el-drawer>

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
.home {
  display: flex;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-top: 5px;
}

.carousel-item-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.pagination-container {
  display: flex;
  justify-content: center;
}
</style>