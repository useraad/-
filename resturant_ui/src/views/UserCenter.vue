<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="p-header">
        <div class="avatar">{{ user.name ? user.name.charAt(0) : 'U' }}</div>
        <div class="p-info">
           <h2>{{ user.name }}</h2>
           <el-tag :type="roleTagType">{{ user.role }}</el-tag>
           <p class="uid">ID: {{ user.username }}</p>
        </div>
      </div>

      <el-divider />

      <div v-if="user.role === 'USER'" class="wallet-section">
         <div class="balance-box">
            <div class="label">当前余额</div>
            <div class="val">¥{{ balance.toFixed(2) }}</div>
         </div>
         <div class="action-box">
            <el-button type="primary" size="large" round @click="dialogVisible = true">
               💰 立即充值
            </el-button>
         </div>
      </div>

      <div v-else class="admin-section">
         <el-descriptions title="工作信息" :column="1" border>
            <el-descriptions-item label="所属门店">总店 (Main Store)</el-descriptions-item>
            <el-descriptions-item label="入职时间">2023-01-01</el-descriptions-item>
            <el-descriptions-item label="系统权限">最高管理员权限</el-descriptions-item>
         </el-descriptions>
         <div style="margin-top: 20px; text-align: center">
            <el-button type="warning" plain>🔒 修改登录密码</el-button>
         </div>
      </div>

    </div>

    <el-dialog v-model="dialogVisible" title="余额充值" width="300px" center>
      <div class="recharge-grid">
         <div 
           v-for="amt in [50, 100, 200, 500]" :key="amt"
           :class="['amt-item', { active: amount === amt }]"
           @click="amount = amt"
         >
           ¥{{ amt }}
         </div>
      </div>
      <template #footer>
        <el-button type="primary" style="width: 100%" @click="handleRecharge">确认支付 ¥{{ amount }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const user = ref({})
const balance = ref(0)
const dialogVisible = ref(false)
const amount = ref(100)

const roleTagType = computed(() => {
  if (user.value.role === 'ADMIN') return 'danger'
  if (user.value.role === 'STAFF') return 'warning'
  return 'success'
})

const loadInfo = async () => {
  const u = JSON.parse(localStorage.getItem('user'))
  if (u) {
    // 获取最新信息
    const res = await axios.get(`http://localhost:8080/user/info/${u.id}`)
    if(res.data.code === 200) {
       user.value = res.data.data
       balance.value = res.data.data.balance || 0
    }
  }
}

const handleRecharge = async () => {
  await axios.post('http://localhost:8080/user/recharge', { userId: user.value.id, amount: amount.value })
  ElMessage.success('充值成功')
  dialogVisible.value = false
  loadInfo()
}

onMounted(loadInfo)
</script>

<style scoped>
.profile-container { display: flex; justify-content: center; padding-top: 50px; }
.profile-card { width: 400px; background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }

.p-header { display: flex; align-items: center; gap: 20px; margin-bottom: 20px; }
.avatar { width: 64px; height: 64px; background: #f0f2f5; color: #909399; font-size: 28px; font-weight: bold; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.p-info h2 { margin: 0 0 5px 0; font-size: 20px; }
.uid { font-size: 12px; color: #999; margin-top: 5px; font-family: monospace; }

/* 钱包样式 */
.wallet-section { text-align: center; padding: 20px 0; background: #fdfdfd; border-radius: 8px; }
.balance-box .label { font-size: 13px; color: #999; }
.balance-box .val { font-size: 36px; font-weight: bold; color: #333; margin: 10px 0 20px; }

/* 充值格子 */
.recharge-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin: 10px 0; }
.amt-item { border: 1px solid #eee; padding: 15px; text-align: center; border-radius: 8px; cursor: pointer; font-weight: bold; }
.amt-item.active { border-color: #409eff; color: #409eff; background: #ecf5ff; }
</style>