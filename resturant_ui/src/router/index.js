import { createRouter, createWebHistory } from 'vue-router'

// 引入组件
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Index from '../views/Index.vue' // 点餐页
import OrderList from '../views/OrderList.vue'
import KitchenBoard from '../views/KitchenBoard.vue'
import TableStatus from '../views/TableStatus.vue'
import Dashboard from '../views/Dashboard.vue'
import EmployeeManage from '../views/EmployeeManage.vue'
import ProductManage from '../views/ProductManage.vue'
import UserCenter from '../views/UserCenter.vue'

const routes = [
  { path: '/', redirect: '/pos' },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  
  // 公共页面
  { path: '/pos', name: 'Pos', component: Index },
  { path: '/profile', name: 'Profile', component: UserCenter },

  // 管理/员工页面
  { path: '/staff', component: OrderList },
  { path: '/kitchen/board', component: KitchenBoard },
  { path: '/admin/tables', component: TableStatus },
  { path: '/admin/dashboard', component: Dashboard },
  { path: '/admin/employee', component: EmployeeManage },
  { path: '/admin', component: ProductManage } // 菜品管理
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// === 路由守卫：最关键的部分 ===
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null

  // 1. 如果去登录或注册页，直接放行
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  // 2. 如果没登录，强制踢回登录
  if (!user) {
    next('/login')
    return
  }

  // 3. 权限控制：如果顾客(USER)想去后台(/admin, /staff, /kitchen)
  if (user.role === 'USER') {
    const forbidden = ['/admin', '/staff', '/kitchen']
    if (forbidden.some(path => to.path.startsWith(path))) {
      // 踢回点餐页
      next('/pos')
      return
    }
  }

  next()
})

export default router