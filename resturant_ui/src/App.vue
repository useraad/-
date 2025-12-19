<template>
  <div class="app-wrapper">
    <div v-if="isFullScreen" class="fullscreen-wrapper">
      <router-view />
    </div>

    <el-container v-else style="height: 100vh;">
      <el-aside :width="isCollapse ? '64px' : '220px'" class="main-sidebar">
        <div class="sidebar-logo">
           <span class="logo-icon">🍽️</span>
           <h1 v-show="!isCollapse">智慧餐厅</h1>
        </div>

        <div class="user-profile" v-show="!isCollapse">
           <div class="avatar">👑</div>
           <div class="username">{{ user.name || '未登录' }}</div>
           <div class="role-badge">
             {{ user.role === 'ADMIN' ? '管理员' : (user.role === 'STAFF' ? '员工' : '顾客') }}
           </div>
        </div>

        <el-menu
          :default-active="$route.path"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="el-menu-vertical"
        >
          <el-menu-item index="/pos">
            <el-icon><Monitor /></el-icon>
            <template #title>前台点餐</template>
          </el-menu-item>

          <template v-if="user.role === 'ADMIN' || user.role === 'STAFF'">
            <el-menu-item index="/staff">
              <el-icon><List /></el-icon>
              <template #title>订单中心</template>
            </el-menu-item>
            
            <el-menu-item index="/kitchen/board">
              <el-icon><Food /></el-icon>
              <template #title>后厨看板</template>
            </el-menu-item>

            <el-menu-item index="/admin/tables">
               <el-icon><Grid /></el-icon>
               <template #title>桌台监控</template>
            </el-menu-item>

            <el-menu-item index="/admin/dashboard">
               <el-icon><DataLine /></el-icon>
               <template #title>经营报表</template>
            </el-menu-item>
          </template>

          <template v-if="user.role === 'ADMIN'">
            <el-menu-item index="/admin/employee">
               <el-icon><User /></el-icon>
               <template #title>员工管理</template>
            </el-menu-item>

             <el-menu-item index="/admin">
               <el-icon><Dish /></el-icon>
               <template #title>菜品管理</template>
            </el-menu-item>
          </template>

          <el-menu-item index="/profile">
            <el-icon><UserFilled /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
        </el-menu>

        <div class="sidebar-footer">
           <el-button type="danger" link @click="logout" v-show="!isCollapse">退出登录</el-button>
           <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
             <component :is="isCollapse ? 'Expand' : 'Fold'" />
           </el-icon>
        </div>
      </el-aside>

      <el-main class="main-content">
         <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
               <component :is="Component" />
            </transition>
         </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Monitor, List, Food, Grid, DataLine, User, Dish, UserFilled, Expand, Fold } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const user = ref({ role: 'GUEST', name: '未登录' })

// 封装：读取最新的用户信息
const loadUser = () => {
  const userStr = localStorage.getItem('user')
  user.value = userStr ? JSON.parse(userStr) : { role: 'GUEST', name: '未登录' }
}

// 初始化加载
onMounted(() => { loadUser() })

// 🔥 关键：监听路由变化，确保切换账号后菜单刷新
watch(() => route.path, () => { loadUser() })

const isFullScreen = computed(() => ['/login', '/register'].includes(route.path))

const logout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('currentTable')
  user.value = { role: 'GUEST', name: '未登录' }
  router.push('/login')
}
</script>

<style scoped>
/* 保持你原来的 CSS 不变 */
.app-wrapper { width: 100%; height: 100vh; display: flex; overflow: hidden; }
.fullscreen-wrapper { width: 100%; height: 100vh; flex: 1; overflow: auto; }
.main-sidebar { background-color: #304156; transition: width 0.3s; display: flex; flex-direction: column; box-shadow: 2px 0 6px rgba(0,0,0,0.1); z-index: 10; }
.sidebar-logo { height: 60px; display: flex; align-items: center; justify-content: center; background: #2b3649; color: #fff; overflow: hidden; }
.logo-icon { font-size: 24px; margin-right: 8px; }
.sidebar-logo h1 { font-size: 18px; font-weight: bold; margin: 0; white-space: nowrap; }
.user-profile { text-align: center; padding: 20px 0; border-bottom: 1px solid rgba(255,255,255,0.05); }
.avatar { font-size: 36px; margin-bottom: 5px; }
.username { font-weight: bold; font-size: 16px; color: #fff; margin-bottom: 4px; }
.role-badge { display: inline-block; font-size: 10px; background: rgba(255,255,255,0.1); padding: 2px 8px; border-radius: 10px; color: #bfcbd9; }
.el-menu-vertical { border-right: none; flex: 1; overflow-y: auto; }
.sidebar-footer { height: 50px; background: #263445; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; color: #fff; }
.collapse-btn { cursor: pointer; font-size: 20px; }
.main-content { background: #f0f2f5; padding: 0; overflow: hidden; position: relative; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>