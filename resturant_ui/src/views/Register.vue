<template>
  <div class="login-container">
    <div class="bg-shape shape-1" />
    <div class="bg-shape shape-2" />
    <div class="bg-shape shape-3" />

    <div class="login-card">
      <div class="brand">
        <div class="logo">📝</div>
        <div class="brand-text">
          <div class="title">新用户注册</div>
          <div class="subtitle">Join Smart Restaurant</div>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        size="large"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="设置账号">
            <template #prefix><span class="input-icon">👤</span></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="设置密码"
            show-password
          >
            <template #prefix><span class="input-icon">🔒</span></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="name">
          <el-input v-model="form.name" placeholder="您的昵称 (选填)">
            <template #prefix><span class="input-icon">✨</span></template>
          </el-input>
        </el-form-item>

        <el-button 
          type="primary" 
          class="login-btn" 
          :loading="loading" 
          @click="handleRegister"
        >
          立即注册
        </el-button>

        <div class="footer-links">
          <span>已有账号？</span>
          <span class="link-text" @click="$router.push('/login')">去登录</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  name: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await axios.post('http://localhost:8080/user/register', form)
        if (res.data.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(res.data.msg)
        }
      } catch (e) {
        ElMessage.error('注册失败，请检查网络')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 直接复用 Login.vue 的样式，为了方便我把核心样式贴在这里 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-shape { position: absolute; border-radius: 50%; filter: blur(80px); z-index: 1; }
.shape-1 { top: -10%; left: -10%; width: 500px; height: 500px; background: rgba(64, 158, 255, 0.2); }
.shape-2 { bottom: -10%; right: -10%; width: 400px; height: 400px; background: rgba(103, 194, 58, 0.2); }
.shape-3 { top: 40%; left: 40%; width: 300px; height: 300px; background: rgba(230, 162, 60, 0.15); transform: translate(-50%, -50%); }

.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  z-index: 2;
  text-align: center;
}

.brand { margin-bottom: 30px; display: flex; align-items: center; justify-content: center; gap: 15px; }
.logo { font-size: 40px; }
.brand-text { text-align: left; }
.title { font-size: 22px; font-weight: bold; color: #333; }
.subtitle { font-size: 12px; color: #999; }

.login-btn { width: 100%; height: 44px; font-size: 16px; border-radius: 8px; margin-bottom: 15px; }

.footer-links { font-size: 14px; color: #666; }
.link-text { color: #409eff; cursor: pointer; margin-left: 5px; font-weight: bold; }
.link-text:hover { text-decoration: underline; }

.input-icon { width: 20px; text-align: center; display: inline-block; }
</style>