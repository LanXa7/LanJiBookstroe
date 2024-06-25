<script setup>

import {onMounted, reactive, ref} from "vue";
import {useStore} from "@/store";
import {Plus} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";

const type = ref([])
const store = useStore()

onMounted(() => {
  getType()
})

function getType() {
  get('/type/list', (data) => {
    store.type = data
    type.value = data
  })
}

const deleteDialogDeleteVisible = ref(false)

const showDeleteDialog = async (id) => {
  deleteDialogDeleteVisible.value = true
  deleteId.value = id
}

const addDialogFormVisible = ref(false)

const showAddDialog = () => {
  addDialogFormVisible.value = true
}

const typeFormRef = ref(null)
const deleteId = ref(0)

const typeForm = reactive({
  name: ''
})


const deleteType = async () => {
  get('/type/delete?id=' + deleteId.value, () => {
    ElMessage.success("删除类型成功")
    getType()
    window.location.reload()
  })
}

const rules = {
  name: [
    {required: true, message: '请输入类型', trigger: 'blur'}
  ]
}

const addType = async () => {
  typeFormRef.value.validate(isValid => {
    if (isValid) {
      post('/type/add', typeForm, () => {
        ElMessage.success("添加类型成功")
        getType()
        window.location.reload()
      })
    }
  })

}

</script>

<template>
  <div>
    <el-table :data="type">
      <el-table-column prop="id" label="id"></el-table-column>
      <el-table-column prop="name" label="类型"></el-table-column>
      <el-table-column label="操作" width="180px" align="center">
        <template #default="scope">
          <el-row>
            <el-col>
              <el-button type="danger" plain size="small" @click="showDeleteDialog(scope.row.id)">
                删除
              </el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog
      v-model="deleteDialogDeleteVisible"
      title="Tips"
      width="500"
  >
    <span>你确定要删除这个类型吗?</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="deleteDialogDeleteVisible = false">取消</el-button>
        <el-button type="danger" plain @click="deleteType">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <div class="bottom-right-button">
    <el-button type="primary" :icon="Plus" circle @click="showAddDialog"/>
  </div>

  <el-dialog v-model="addDialogFormVisible" title="添加类型" width="500px" align-center>
    <el-form :model="typeForm" ref="typeFormRef" :rules="rules" class="inline-form">
      <el-form-item label="类型" prop="name">
        <el-input v-model="typeForm.name"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="addDialogFormVisible = false">取消</el-button>
        <el-button @click="addType" type="primary">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.bottom-right-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  transform: scale(1.5); /* 放大 20% */
}
</style>