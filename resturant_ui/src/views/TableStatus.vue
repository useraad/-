<template>
  <div class="page-container">
    <div class="header">
      <h2>🍽️ 餐厅桌台监控中心</h2>
      <div class="legend">
        <span class="dot free"></span> 空闲
        <span class="dot busy"></span> 就餐中
      </div>
    </div>

    <div class="table-grid">
      <div 
        v-for="t in tables" 
        :key="t.tableId" 
        :class="['table-item', t.status]"
        @click="handleTableClick(t)"
      >
        <div class="table-header">
           <span class="table-id">{{ t.tableId }}号桌</span>
           <el-tag size="small" :type="t.status==='busy'?'danger':'success'" effect="dark">
             {{ t.status==='busy'?'就餐中':'空闲' }}
           </el-tag>
        </div>
        
        <div class="info" v-if="t.status === 'busy'">
           <div class="money-label">当前消费</div>
           <div class="money-val">¥{{ t.orderInfo ? t.orderInfo.totalAmount : '0.00' }}</div>
           <div class="time">{{ formatTime(t.orderInfo?.createTime) }} 开台</div>
        </div>

        <div class="info free-text" v-else>
           <span>虚位以待</span>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="currentTableId + '号桌 - 订单详情'" width="500px">
      <el-table :data="tableDetails" border stripe>
        <el-table-column prop="productName" label="菜品" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="price" label="单价" width="80" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

const tables = ref([])
const dialogVisible = ref(false)
const currentTableId = ref(0)
const tableDetails = ref([])
let timer = null

const loadStatus = async () => {
  try {
    const res = await axios.get('http://localhost:8080/order/table/status')
    if(res.data.code === 200) tables.value = res.data.data
  } catch(e) {}
}

const handleTableClick = async (t) => {
  if (t.status === 'free') return
  currentTableId.value = t.tableId
  const res = await axios.get(`http://localhost:8080/order/table/${t.tableId}`)
  if(res.data.code === 200) {
    tableDetails.value = res.data.data
    dialogVisible.value = true
  }
}

const formatTime = (t) => t ? t.substring(11, 16) : '--:--'

onMounted(() => {
  loadStatus()
  timer = setInterval(loadStatus, 5000) // 5秒自动刷新一次
})

onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.page-container { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.legend { display: flex; align-items: center; gap: 15px; background: #fff; padding: 10px 20px; border-radius: 20px; }
.dot { width: 12px; height: 12px; border-radius: 50%; display: inline-block; margin-right: 5px; }
.dot.free { background: #67c23a; }
.dot.busy { background: #f56c6c; }

.table-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; }
.table-item {
  height: 180px; background: #fff; border-radius: 12px; padding: 15px;
  display: flex; flex-direction: column; justify-content: space-between;
  cursor: pointer; border: 2px solid transparent; transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.table-item:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(0,0,0,0.1); }
.table-item.busy { background: #fff1f0; border-color: #ffccc7; }
.table-header { display: flex; justify-content: space-between; align-items: center; }
.table-id { font-size: 20px; font-weight: bold; color: #333; }

.info { text-align: center; margin-top: 10px; }
.money-label { font-size: 12px; color: #999; }
.money-val { font-size: 28px; font-weight: 800; color: #f56c6c; font-family: sans-serif; margin: 5px 0; }
.time { font-size: 12px; color: #666; }
.free-text { color: #85ce61; font-size: 16px; margin-top: 40px; opacity: 0.8; }
</style>