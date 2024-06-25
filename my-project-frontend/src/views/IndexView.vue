<script setup>
import {get, login, logout, unauthorized} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {ref} from "vue";
import {Back, Lock, Message, Moon, Operation, Sunny} from "@element-plus/icons-vue";
import {useDark} from "@vueuse/core";

const store = useStore()
const loading = ref(false)
const dark = ref(useDark())
const isUnauthorized = unauthorized()

if (!isUnauthorized) {
  get('/api/user/info', (data) => {
    console.log(data)
    store.user = data
    loading.value = false
  })
}


function userLogout() {
  store.resetUserData();
  logout(() => {
    router.push("/home")
    location.reload()
  })

}

function gotoSetting() {
  router.push("/user-setting")
}

function userLogin() {
  router.push("/welcome")
}
</script>

<template>
  <div class="main-container" v-loading="loading" element-loading-text="正在进入，请稍后...">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-container-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <el-switch style="margin: 0 20px"
                   v-model="dark" active-color="#424242"
                   :active-action-icon="Moon"
                   :inactive-action-icon="Sunny"/>
        <div style="flex: 1" class="user-info">
          <div class="profile">
            <div>{{ store.user.username }}</div>
            <div>{{ store.user.email }}</div>
          </div>
        </div>
        <el-dropdown>
          <el-avatar :src="store.avatarUrl"/>
          <template #dropdown>
            <el-dropdown-item @click="gotoSetting">
              <el-icon>
                <Operation/>
              </el-icon>
              个人设置
            </el-dropdown-item>
            <el-dropdown-item @click="userLogout" v-if="!isUnauthorized" divided>
              <el-icon>
                <Back/>
              </el-icon>
              退出登录
            </el-dropdown-item>
            <el-dropdown-item @click="userLogin" v-if="isUnauthorized" divided>
              <el-icon>
                <Lock/>
              </el-icon>
              书店登录
            </el-dropdown-item>
          </template>
        </el-dropdown>
      </el-header>
      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100vh - 55px) ">
            <el-menu
                style="min-height: calc(100vh - 55px)"
                :default-active="$route.path"
                router
            >
              <el-menu-item index="/">
                <template #title>
                  <el-icon>
                    <i class="fa-solid fa-house" style="color:#409eff"></i>
                  </el-icon>
                  <span><b>首页</b></span>
                </template>
              </el-menu-item>
              <el-sub-menu v-if="store.user.role==='admin'">
                <template #title>
                  <el-icon>
                    <i class="fa-solid fa-bookmark" style="color:#409eff"></i>
                  </el-icon>
                  <span><b>书店管理</b></span>
                </template>
                <el-menu-item index="book-manager">
                  <template #title>
                    <el-icon>
                      <i class="fa-solid fa-info" style="color:#409eff"></i>
                    </el-icon>
                    图书管理
                  </template>
                </el-menu-item>
                <el-menu-item index="type-manager">
                  <template #title>
                    <el-icon>
                      <i class="fa-solid fa-info" style="color:#409eff"></i>
                    </el-icon>
                    种类管理
                  </template>
                </el-menu-item>
<!--                <el-menu-item index="data-manager">-->
<!--                  <template #title>-->
<!--                    <el-icon>-->
<!--                      <i class="fa-solid fa-info" style="color:#409eff"></i>-->
<!--                    </el-icon>-->
<!--                    销量数据-->
<!--                  </template>-->
<!--                </el-menu-item>-->
              </el-sub-menu>
              <el-menu-item v-if="store.user.role==='admin'" index="/order-manager">
                <template #title>
                  <el-icon>
                    <i class="fa-solid fa-clipboard" style="color:#409eff"></i>
                  </el-icon>
                  <span><b>订单管理</b></span>
                </template>
              </el-menu-item>
              <el-menu-item index="/shopping-cart">
                <template #title>
                  <el-icon>
                    <i class="fa-solid fa-cart-shopping" style="color:#409eff"></i>
                  </el-icon>
                  <span><b>购物车</b></span>
                </template>
              </el-menu-item>
              <el-sub-menu>
                <template #title>
                  <el-icon>
                    <i class="fa-solid fa-user" style="color:#409eff"></i>
                  </el-icon>
                  <span><b>我的</b></span>
                </template>
                <el-menu-item index="/user-setting">
                  <template #title>
                    <el-icon>
                      <i class="fa-solid fa-info" style="color:#409eff"></i>
                    </el-icon>
                    个人信息
                  </template>
                </el-menu-item>
                <el-menu-item index="/address-setting">
                  <template #title>
                    <el-icon>
                      <i class="fa-solid fa-location-dot" style="color:#409eff"></i>
                    </el-icon>
                    收货地址
                  </template>
                </el-menu-item>
                <el-menu-item index="/my-order">
                  <template #title>
                    <el-icon>
                      <i class="fa-solid fa-clipboard" style="color:#409eff"></i>
                    </el-icon>
                    我的订单
                  </template>
                </el-menu-item>
                <el-menu-item index="/account-setting">
                  <template #title>
                    <el-icon>
                      <i class="fa-solid fa-lock" style="color:#409eff"></i>
                    </el-icon>
                    账号设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main class="main-container-page">
          <el-scrollbar style="height: calc(100vh - 55px) ">
            <router-view v-slot="{Component}">
              <component :is="Component" style="height: 100%"/>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<style lang="less" scoped>

.main-container {
  height: 100vh;
  width: 100vw;
}

.main-container-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .logo {
    height: 32px;
  }

  .user-info {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .profile {
      text-align: right;
      margin-right: 20px;

      :first-child {
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }

      :last-child {
        font-size: 10px;
        color: grey;
      }
    }
  }
}

.main-container-page {
  padding: 0;
  background-color: #f7f8fa;
}

.dark .main-container-page {
  background-color: #212225;
}

</style>
