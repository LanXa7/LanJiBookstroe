<script setup>
import {Plus} from "@element-plus/icons-vue";

defineProps({
  icon: Object,
  name: String,
  author: String,
  price: Number
})
</script>

<template>
  <div class="card">
    <slot/>
    <el-divider border-style="solid"></el-divider>
    <div class="card-header" v-if="name">
      <div>
        {{ name }}
      </div>
      <div>
        {{ author }}
      </div>
      <div>
        ￥{{ price }}
      </div>
    </div>
  </div>
</template>

<style scoped>

.card {
  border-radius: 5px;
  border: solid 1px var(--el-border-color);
  background-color: var(--el-bg-color);
  box-sizing: border-box;
  min-height: 20px;
  padding: 10px;
}

.card-header {

  padding-bottom: 5px;
  margin-bottom: 10px;

  & > :first-child {
    font-size: 13px;
    font-weight: bold;
  }

  & > :nth-child(2){
    font-size: 11px;
    color: grey;
  }

  & > :last-child {
    font-size: 13px;
    color: #fb3b3b;
    font-weight: bold;
  }
}

.el-divider {
  margin: 0; /* 移除上下间距 */
}



</style>