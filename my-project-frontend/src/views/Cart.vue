<script setup>
import {onMounted, reactive, ref} from "vue";
import {get, post} from "@/net";
import axios from "axios";
import {ElMessage, ElTable} from "element-plus";
import {useStore} from "@/store";

onMounted(() => {
  getCart();
});

const store = useStore()
const addresses = store.user.addresses
const books = ref([]);

const bookChangeNumberForm = reactive({
  id: -1,
  number: 0,
})

function getCart() {
  get("/cart/get-cart", (data) => {
    books.value = data;
  });
}

const updateNumber = async (row) => {
  bookChangeNumberForm.number = row.number;
  bookChangeNumberForm.id = row.id;
  post("/cart/update-cart", bookChangeNumberForm, () => {
    ElMessage.success("修改数量成功！");
    getCart();
    window.location.reload();
  });
};

const deleteId = ref(0);

const deleteDialogDeleteVisible = ref(false);

const showDeleteDialog = async (id) => {
  deleteDialogDeleteVisible.value = true;
  deleteId.value = id;
};

const deleteBook = async () => {
  console.log(deleteId);
  get("/cart/delete?bookId=" + deleteId.value, () => {
    deleteDialogDeleteVisible.value = false;
    ElMessage.success("删除图书成功！");
    getCart();
  });
};

const bookForm = reactive({
  accountId: store.user.id,
  bookId: -1,
  totalNumber: 0,
  totalPrice: 0.0,
  addressId: ''
});

const choseAddress = async (row) => {
  dialogFormVisible.value = true
  console.log(row)
  bookForm.bookId = row.id
  bookForm.totalNumber = row.number
  bookForm.totalPrice = row.price * row.number
  console.log(bookForm)
}


const buyBook = async () => {
  if (bookForm.addressId === '') {
    ElMessage.error("请选择地址")
  } else {
    post('/cart/buy', bookForm, () => {
      ElMessage.success("下单成功！请前往我的订单查看!")
      getCart()
    })
  }
}


// 一键清除勾选的功能
const handleClearSelection = () => {
  const tableRef = table.value;
  if (tableRef) {
    tableRef.clearSelection();
    ElMessage.success("已清除所有勾选！");
  }
}

let selectedBooks = []

// 处理一键购买的功能
const handlePurchaseSelectedBooks = () => {
  selectedBooks = table.value.getSelectionRows();
  console.log(selectedBooks)
  if (selectedBooks.length === 0) {
    ElMessage.warning("请选择要购买的图书！");
    return;
  }
  dialogFormVisibleMany.value = true

}

const deleteAllVisible = ref(null)

const handleClearSelectedBooks = () => {
  selectedBooks = table.value.getSelectionRows();
  console.log(selectedBooks)
  if (selectedBooks.length === 0) {
    ElMessage.warning("请选择要清空的图书！");
    return;
  }
  deleteAllVisible.value = true
}


const deleteCart = async () => {
  const bookIds = selectedBooks.map(book => book.id);
  console.log(bookIds)
  post('/cart/deleteAll', bookIds, () => {
    ElMessage.success("清空成功")
    getCart()
    deleteAllVisible.value = false
  })
}


const buyBooks = async () => {
  if (bookForm.addressId === '') {
    ElMessage.error("请选择地址")
  } else {
    // 构建需要传递的 bookList 数组，每个元素都是 bookForm 对象
    const bookList = selectedBooks.map((book) => ({
      accountId: bookForm.accountId,
      bookId: bookForm.bookId = book.id,
      totalNumber: bookForm.totalNumber = book.number,
      totalPrice: bookForm.totalPrice = book.price * book.number,
      addressId: bookForm.addressId
    }))
    console.log(bookList)
    post('/cart/buys', bookList, () => {
      ElMessage.success("下单成功！请前往我的订单查看!")
      getCart()
      dialogFormVisibleMany.value = false
    })
  }
}

const dialogFormVisible = ref(false)
const dialogFormVisibleMany = ref(false)
const table = ref(null);

</script>

<template>
  <div>
    <el-table ref="table" :data="books" style="width: 100%" empty-text="还没有图书加入购物车">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column prop="id" label="ID" align="center"/>
      <el-table-column prop="cover" label="封面" align="center">
        <template #default="{ row }">
          <el-image
              :src="`${axios.defaults.baseURL}/images${row.cover}`"
              style="width: 50px; height: 75px; margin-bottom: 10px;"
              fit="cover"
          ></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="书名" align="center"/>
      <el-table-column prop="author" label="作者" align="center"/>
      <el-table-column prop="isbn" label="isbn" align="center"/>
      <el-table-column prop="number" label="数量" align="center">
        <template #default="{ row }">
          <el-input-number
              v-model="row.number"
              controls-position="right"
              size="small"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="单价" align="center"/>
      <el-table-column label="操作" width="180px" align="center">
        <template #default="scope">
          <el-row>
            <el-col :span="8">
              <el-button
                  type="primary"
                  plain
                  size="small"
                  @click="updateNumber(scope.row)"
              >
                修改
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="warning" plain size="small" @click="choseAddress(scope.row)">
                购买
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button
                  type="danger"
                  plain
                  size="small"
                  @click="showDeleteDialog(scope.row.id)"
              >
                删除
              </el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
    </el-table>

    <!-- 按钮容器 -->
    <div style="text-align: right; margin-top: 20px;">
      <el-button type="warning" plain @click="handleClearSelection">清除勾选</el-button>
      <el-button type="success" plain @click="handlePurchaseSelectedBooks">一键购买</el-button>
      <el-button type="danger" plain @click="handleClearSelectedBooks">一键清空</el-button>
    </div>
  </div>

  <el-dialog v-model="deleteDialogDeleteVisible" title="Tips" width="500">
    <span>你确定要删除这本图书吗?</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="deleteDialogDeleteVisible = false">取消</el-button>
        <el-button type="danger" plain @click="deleteBook">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="deleteAllVisible" title="Tips" width="500">
    <span>你确定要清空购物车吗?</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="deleteAllVisible = false">取消</el-button>
        <el-button type="danger" plain @click="deleteCart">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="dialogFormVisible" title="选择收货地址" align-center>
    <el-select v-model="bookForm.addressId" placeholder="收货信息">
      <el-option
          v-for="option in store.user.addresses"
          :key="option.id"
          :label="`${option.name} ${option.phone } ${option.address}`"
          :value="option.id"
      />
    </el-select>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button @click="buyBook" type="primary">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="dialogFormVisibleMany" title="选择收货地址" align-center>
    <el-select v-model="bookForm.addressId" placeholder="收货信息">
      <el-option
          v-for="option in store.user.addresses"
          :key="option.id"
          :label="`${option.name} ${option.phone } ${option.address}`"
          :value="option.id"
      />
    </el-select>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisibleMany = false">取消</el-button>
        <el-button @click="buyBooks" type="primary">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

