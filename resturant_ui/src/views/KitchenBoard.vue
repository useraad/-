<template>
  <div class="kds-wrapper">
    <div class="kds-header">
      <div class="title">🍳 智能后厨系统 (KDS)</div>
      <div class="clock">{{ currentTime }}</div>
    </div>

    <div class="kds-board">
      <div class="column pending-col">
        <div class="col-head pending-head">
          <span>🔥 待制作 ({{ pendingList.length }})</span>
        </div>
        <div class="card-list">
          <transition-group name="list">
            <div v-for="order in pendingList" :key="order.id" class="order-ticket">
               <div class="ticket-head">
                  <span class="t-no">#{{ order.tableId }}号桌</span>
                  <span class="t-time">{{ formatTime(order.createTime) }}</span>
               </div>
               <div class="ticket-body">
                  <div class="ticket-id">单号: {{ order.orderNo.slice(-6) }}</div>
                  <div class="ticket-items">
                     <div class="item-placeholder">共消费 ¥{{ order.totalAmount }}</div>
                     <div class="item-note">请查看详情</div>
                  </div>
               </div>
               <div class="ticket-foot">
                  <el-button type="primary" class="done-btn" @click="finishOrder(order.id)">
                     出餐完成
                  </el-button>
                  <el-button link size="small" @click="showDetail(order.tableId)">明细</el-button>
               </div>
            </div>
          </transition-group>
          <div v-if="pendingList.length === 0" class="empty-col">暂无待制作订单</div>
        </div>
      </div>

      <div class="column done-col">
        <div class="col-head done-head">
          <span>✅ 已出餐 (最近10单)</span>
        </div>
        <div class="card-list">
           <transition-group name="list">
             <div v-for="order in doneList" :key="order.id" class="order-ticket is-done">
                <div class="ticket-head">
                   <span class="t-no">#{{ order.tableId }}</span>
                   <span class="status-tag">已出餐</span>
                </div>
                <div class="ticket-body">
                   <div class="ticket-id">{{ order.orderNo.slice(-6) }}</div>
                </div>
             </div>
           </transition-group>
        </div>
      </div>
    </div>

    <el-dialog v-model="detailVisible" title="订单明细" width="400px">
      <el-table :data="tableDetail" border stripe>
        <el-table-column prop="productName" label="菜品" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const list = ref([])
const currentTime = ref('')
const detailVisible = ref(false)
const tableDetail = ref([])
let timer = null

const pendingList = computed(() => list.value.filter(o => o.status === 1))
const doneList = computed(() => list.value.filter(o => o.status === 2).slice(0, 10))

const loadData = async () => {
  const res = await axios.get('http://localhost:8080/order/list')
  if(res.data.code === 200) list.value = res.data.data.reverse()
}

const finishOrder = async (id) => {
  await axios.put(`http://localhost:8080/order/status/${id}/2`)
  ElMessage.success('出餐成功')
  loadData()
}

const showDetail = async (tid) => {
  const res = await axios.get(`http://localhost:8080/order/table/${tid}`)
  tableDetail.value = res.data.data
  detailVisible.value = true
}

const formatTime = (t) => t ? t.substring(11, 16) : ''

onMounted(() => {
  loadData()
  setInterval(loadData, 5000)
  timer = setInterval(() => {
     currentTime.value = new Date().toLocaleTimeString()
  }, 1000)
})

onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.kds-wrapper { height: calc(100vh - 40px); display: flex; flex-direction: column; background: #f0f2f5; }
.kds-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background: #fff; border-bottom: 1px solid #ddd; }
.title { font-size: 20px; font-weight: 800; color: #333; }
.clock { font-family: monospace; font-size: 18px; font-weight: bold; color: #666; }

.kds-board { flex: 1; display: flex; padding: 20px; gap: 20px; overflow: hidden; }

.column { flex: 1; background: #eef0f4; border-radius: 12px; display: flex; flex-direction: column; overflow: hidden; }
.col-head { padding: 15px; font-weight: bold; font-size: 16px; text-align: center; }
.pending-head { background: #ffe4ba; color: #d46b08; }
.done-head { background: #d9f7be; color: #389e0d; }

.card-list { flex: 1; overflow-y: auto; padding: 15px; }

/* 票据卡片样式 */
.order-ticket { background: #fff; border-radius: 8px; padding: 15px; margin-bottom: 15px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); border-left: 5px solid #fa8c16; transition: 0.3s; }
.order-ticket.is-done { border-left-color: #52c41a; opacity: 0.7; }
.order-ticket:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0,0,0,0.1); }

.ticket-head { display: flex; justify-content: space-between; border-bottom: 1px dashed #eee; padding-bottom: 8px; margin-bottom: 10px; }
.t-no { font-size: 18px; font-weight: 800; color: #333; }
.t-time { color: #999; font-size: 14px; }

.ticket-body { margin-bottom: 15px; }
.ticket-id { font-size: 13px; color: #666; font-family: monospace; }
.item-placeholder { font-weight: bold; margin-top: 5px; color: #333; }
.item-note { font-size: 12px; color: #999; }

.ticket-foot { display: flex; gap: 10px; align-items: center; }
.done-btn { flex: 1; font-weight: bold; }

.empty-col { text-align: center; color: #999; margin-top: 50px; }

/* 动画 */
.list-enter-active, .list-leave-active { transition: all 0.5s ease; }
.list-enter-from, .list-leave-to { opacity: 0; transform: translateX(30px); }
</style>