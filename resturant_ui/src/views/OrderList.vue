<template>
  <div class="page-container">
    
    <div class="stat-row">
      <div class="stat-card">
        <div class="s-icon blue"><el-icon><List /></el-icon></div>
        <div class="s-info">
           <div class="label">今日订单</div>
           <div class="num">{{ list.length }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="s-icon orange"><el-icon><Timer /></el-icon></div>
        <div class="s-info">
           <div class="label">待制作</div>
           <div class="num">{{ pendingCount }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="s-icon green"><el-icon><Money /></el-icon></div>
        <div class="s-info">
           <div class="label">今日流水</div>
           <div class="num">¥{{ totalRevenue }}</div>
        </div>
      </div>
    </div>

    <div class="table-panel">
      <div class="toolbar">
         <div class="left">
           <el-input v-model="searchKw" placeholder="搜索订单号..." prefix-icon="Search" style="width: 200px" />
           <el-select v-model="statusFilter" placeholder="状态" style="width: 120px; margin-left: 10px">
              <el-option label="全部" value="" />
              <el-option label="制作中" :value="1" />
              <el-option label="已完成" :value="2" />
           </el-select>
         </div>
         <div class="right">
            <el-button type="primary" :icon="Refresh" @click="loadOrders">刷新</el-button>
         </div>
      </div>

      <el-table :data="filteredList" stripe style="width: 100%" height="calc(100vh - 280px)">
        <el-table-column prop="orderNo" label="订单编号" width="180">
           <template #default="{row}">
             <span class="mono-font">{{ row.orderNo.slice(-10) }}</span>
           </template>
        </el-table-column>
        
        <el-table-column prop="tableId" label="桌号" width="100" align="center">
           <template #default="{row}">
             <span class="table-badge">{{ row.tableId }}号</span>
           </template>
        </el-table-column>

        <el-table-column prop="totalAmount" label="金额" width="120">
           <template #default="{row}">
              <span class="price-text">¥{{ row.totalAmount }}</span>
           </template>
        </el-table-column>

        <el-table-column prop="createTime" label="下单时间" width="180">
            <template #default="{row}">{{ formatTime(row.createTime) }}</template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120" align="center">
           <template #default="{row}">
              <el-tag v-if="row.status === 2" type="success" effect="dark">已完成</el-tag>
              <el-tag v-else type="warning" effect="dark">制作中</el-tag>
           </template>
        </el-table-column>

        <el-table-column label="操作" align="right">
           <template #default="{row}">
              <el-button link type="primary" size="small" @click="showDetail(row)">查看详情</el-button>
              <el-button 
                v-if="row.status !== 2" 
                link type="success" 
                size="small" 
                @click="finishOrder(row.id)"
              >
                确认出餐
              </el-button>
           </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="订单详情" width="500px">
       <div v-if="currentRow">
          <div class="detail-header">
             <h3>#{{ currentRow.tableId }}号桌</h3>
             <span class="time">{{ formatTime(currentRow.createTime) }}</span>
          </div>
          <el-table :data="detailList" border size="small">
             <el-table-column prop="productName" label="菜品" />
             <el-table-column prop="quantity" label="数量" width="80" align="center" />
             <el-table-column label="小计" width="100" align="right">
                <template #default="{row}">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
             </el-table-column>
          </el-table>
          <div class="detail-footer">
             合计：<span class="big-price">¥{{ currentRow.totalAmount }}</span>
          </div>
       </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { List, Timer, Money, Refresh, Search } from '@element-plus/icons-vue'

const list = ref([])
const searchKw = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const currentRow = ref(null)
const detailList = ref([])

// 统计数据
const pendingCount = computed(() => list.value.filter(i => i.status !== 2).length)
const totalRevenue = computed(() => list.value.reduce((s, i) => s + i.totalAmount, 0).toFixed(2))

// 过滤列表
const filteredList = computed(() => {
  return list.value.filter(item => {
    const matchKw = item.orderNo.includes(searchKw.value)
    const matchStatus = statusFilter.value === '' || item.status === statusFilter.value
    return matchKw && matchStatus
  })
})

const loadOrders = async () => {
  const res = await axios.get('http://localhost:8080/order/list')
  if(res.data.code === 200) list.value = res.data.data.reverse()
}

const showDetail = async (row) => {
  currentRow.value = row
  const res = await axios.get(`http://localhost:8080/order/table/${row.tableId}`)
  detailList.value = res.data.data
  dialogVisible.value = true
}

const finishOrder = async (id) => {
   await axios.put(`http://localhost:8080/order/status/${id}/2`)
   ElMessage.success('操作成功')
   loadOrders()
}

const formatTime = (t) => t ? t.replace('T', ' ').substring(0, 19) : ''

onMounted(() => {
   loadOrders()
   setInterval(loadOrders, 10000) // 10秒轮询
})
</script>

<style scoped>
.page-container { height: 100%; display: flex; flex-direction: column; gap: 20px; }

/* 统计卡片 */
.stat-row { display: flex; gap: 20px; }
.stat-card {
  flex: 1; background: #fff; padding: 20px; border-radius: 12px;
  display: flex; align-items: center; gap: 15px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02); border: 1px solid #f0f0f0;
}
.s-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
.s-icon.blue { background: #ecf5ff; color: #409eff; }
.s-icon.orange { background: #fdf6ec; color: #e6a23c; }
.s-icon.green { background: #f0f9eb; color: #67c23a; }
.s-info .label { font-size: 13px; color: #909399; margin-bottom: 5px; }
.s-info .num { font-size: 24px; font-weight: bold; color: #303133; font-family: 'DIN Alternate', sans-serif; }

/* 表格面板 */
.table-panel { flex: 1; background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.02); display: flex; flex-direction: column; }
.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; }

/* 表格内样式 */
.mono-font { font-family: monospace; color: #606266; }
.table-badge { background: #f4f4f5; padding: 2px 8px; border-radius: 4px; font-weight: bold; color: #303133; }
.price-text { font-weight: bold; color: #f56c6c; }

/* 详情弹窗 */
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 10px; }
.detail-footer { text-align: right; margin-top: 15px; font-size: 16px; font-weight: bold; }
.big-price { color: #f56c6c; font-size: 20px; margin-left: 5px; }
</style>