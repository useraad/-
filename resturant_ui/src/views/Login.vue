<template>
  <div class="login-container">
    <div class="bg-shape shape-1" />
    <div class="bg-shape shape-2" />
    <div class="bg-shape shape-3" />

    <div class="login-card">
      <div class="brand">
        <div class="logo" aria-hidden="true">🍔</div>
        <div class="brand-text">
          <div class="title">智慧餐厅系统</div>
          <div class="subtitle">Smart Restaurant Management</div>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" autocomplete="username">
            <template #prefix><span class="input-icon">👤</span></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
            show-password
          >
            <template #prefix><span class="input-icon">🔒</span></template>
          </el-input>
        </el-form-item>

        <div class="options">
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
          <button class="link" type="button" @click="handleForgotPassword">忘记密码？</button>
        </div>
        <div class="footer-links" style="text-align: center; margin-top: 15px; font-size: 14px; color: #666;">
        <span>还没有账号？</span>
        <span style="color: #409eff; cursor: pointer; font-weight: bold;" @click="$router.push('/register')">
          立即注册
        </span>
        </div>

        <el-button
          type="primary"
          class="login-btn"
          :loading="loading"
          :disabled="loading"
          @click="handleLogin"
        >
          登录
        </el-button>

        <div class="tips">提示：可按 Enter 快速登录</div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  remember: true,
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleForgotPassword = () => {
  ElMessage.info('请联系管理员重置密码')
}

// 修改 Login.vue 里的 handleLogin 方法
const handleLogin = async () => {
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/user/login', form)
    if (res.data.code === 200) {
      const userData = res.data.data
      localStorage.setItem('user', JSON.stringify(userData))
      
      ElMessage.success(`欢迎，${userData.name}`)

      // 🔥 角色权限跳转逻辑
      if (userData.role === 'ADMIN') {
        router.push('/admin/dashboard').then(() => { window.location.reload() })
      } else if (userData.role === 'STAFF') {
        router.push('/staff').then(() => { window.location.reload() })
      } else {
        router.push('/pos').then(() => { window.location.reload() })
      }
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  padding: 24px;
  background:
    radial-gradient(1200px 600px at 20% 10%, rgba(255, 255, 255, 0.16), transparent 50%),
    radial-gradient(1000px 600px at 80% 90%, rgba(255, 255, 255, 0.12), transparent 55%),
    linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bg-shape {
  position: absolute;
  border-radius: 999px;
  filter: blur(30px);
  opacity: 0.6;
}
.shape-1 {
  width: 360px;
  height: 360px;
  left: -120px;
  top: -120px;
  background: rgba(255, 255, 255, 0.35);
}
.shape-2 {
  width: 420px;
  height: 420px;
  right: -180px;
  top: 10%;
  background: rgba(54, 240, 220, 0.22);
}
.shape-3 {
  width: 520px;
  height: 520px;
  left: 15%;
  bottom: -240px;
  background: rgba(255, 190, 90, 0.18);
}

.login-card {
  width: min(420px, 100%);
  padding: 34px 34px 28px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 18px;
  box-shadow: 0 18px 60px rgba(0, 0, 0, 0.25);
  text-align: center;
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 22px;
}
.logo {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.18), rgba(118, 75, 162, 0.18));
  box-shadow: inset 0 0 0 1px rgba(102, 126, 234, 0.18);
  font-size: 22px;
}
.brand-text {
  text-align: left;
}
.title {
  font-size: 22px;
  font-weight: 800;
  color: #1f2328;
  letter-spacing: 0.5px;
  line-height: 1.2;
}
.subtitle {
  font-size: 12px;
  color: rgba(31, 35, 40, 0.64);
  margin-top: 2px;
  letter-spacing: 0.6px;
}

.input-icon {
  display: inline-flex;
  width: 18px;
  justify-content: center;
  opacity: 0.85;
}

.options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 2px 14px;
}
.link {
  border: none;
  background: transparent;
  color: rgba(31, 35, 40, 0.7);
  cursor: pointer;
  padding: 0;
  font-size: 13px;
}
.link:hover {
  color: rgba(31, 35, 40, 0.9);
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  font-weight: 700;
  border: none;
  background: linear-gradient(to right, #667eea, #764ba2);
  box-shadow: 0 10px 22px rgba(102, 126, 234, 0.28);
}
.login-btn:hover {
  opacity: 0.95;
}

.tips {
  margin-top: 14px;
  font-size: 12px;
  color: rgba(31, 35, 40, 0.55);
}

/* Element Plus 微调（scoped 下用 :deep） */
:deep(.el-form-item) {
  margin-bottom: 14px;
}
:deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 2px 12px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow:
    0 1px 0 rgba(31, 35, 40, 0.06),
    0 10px 24px rgba(31, 35, 40, 0.08);
}
:deep(.el-input__wrapper.is-focus) {
  box-shadow:
    0 0 0 3px rgba(102, 126, 234, 0.22),
    0 12px 28px rgba(31, 35, 40, 0.12);
}
</style>
