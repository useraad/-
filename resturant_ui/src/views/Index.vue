<template>
  <div class="index-container">
    
    <div v-if="!isTableSet" class="welcome-screen">
      <el-carousel height="100vh" direction="vertical" :interval="4000" arrow="never" indicator-position="none">
        <el-carousel-item v-for="img in adImages" :key="img">
           <div class="ad-bg" :style="{ backgroundImage: `url(${img})` }"><div class="ad-mask"></div></div>
        </el-carousel-item>
      </el-carousel>
      
      <div class="center-action">
        <div class="brand-big">
           <div class="logo-bounce">🔥</div>
           <h1>SmartPOS</h1>
           <p>智能餐厅 · 就在此刻</p>
        </div>
        
        <div class="start-card" @click="openTableSelector">
           <div class="icon-circle"><el-icon><KnifeFork /></el-icon></div>
           <div class="start-text">
              <h3>开始点餐</h3>
              <span>Start Ordering</span>
           </div>
           <el-icon class="arrow"><Right /></el-icon>
        </div>
      </div>
    </div>

    <div v-else class="work-layout">
      <ProductList
        :products="filteredProducts"
        :categories="categories"
        :active-category="activeCategory"
        v-model:searchKey="searchKey"
        @select-category="activeCategory = $event"
        @add="addToCart"
        @open-detail="openDetail"
      />
      <PosUnit
        :cart="cart"
        :is-table-set="isTableSet"
        :table-info="{ id: tableId, person: personCount }"
        :loading="loading"
        @update="updateCart"
        @clear="clearCart"
        @checkout="createOrder"
        @history="openHistory"
        @set-table="openTableSelector"
      />
    </div>

    <el-dialog v-model="tableDialogVisible" title="请选择桌台" width="480px" center destroy-on-close>
       <div class="table-grid">
          <div 
            v-for="t in tableStates" :key="t.id"
            :class="['t-item', t.status, { 'is-mine': isMyTable(t) }]"
            @click="handleTableSelect(t)"
          >
             <div class="t-id">{{ t.id }}</div>
             <div class="t-status">
                <span v-if="t.status === 'free'">空闲</span>
                <span v-else-if="isMyTable(t)">我的桌 (加餐)</span>
                <span v-else>占用</span>
             </div>
          </div>
       </div>
       <div v-if="selectedTableId && !isAddingMeal" style="margin-top:20px; text-align:center">
          <p style="color:#666; margin-bottom:5px">请输入就餐人数</p>
          <el-input-number v-model="tempPersonCount" :min="1" :max="10" />
       </div>
       <template #footer>
          <el-button type="primary" size="large" style="width:100%; font-weight:bold" :disabled="!selectedTableId" @click="confirmTable">
            {{ isAddingMeal ? '进入加餐' : '确认开台' }}
          </el-button>
       </template>
    </el-dialog>

    <el-dialog v-model="payVisible" title="收银台" width="340px" center>
       <div style="text-align:center; padding: 20px 0;">
          <h2 style="font-size:36px; margin:0 0 20px; color:#333;">¥{{ totalAmount }}</h2>
          <div style="display:flex; flex-direction:column; gap:10px;">
             <el-button type="success" size="large" @click="confirmPay('wx')">微信支付</el-button>
             <el-button type="warning" size="large" @click="confirmPay('balance')">余额支付 (余:{{ userBalance }})</el-button>
          </div>
       </div>
    </el-dialog>

    <el-drawer v-model="historyVisible" title="已点菜单" size="350px">
       <div class="history-box">
         <div v-for="(group, idx) in historyGroups" :key="idx" class="h-group">
            <div class="h-group-title">
               <span>{{ idx === 0 ? '第1轮' : `加餐 ${idx}` }}</span>
               <small>{{ formatTime(group[0].createTime) }}</small>
            </div>
            <div v-for="item in group" :key="item.id" class="h-item">
               <div class="h-name">{{ item.productName }} <el-tag v-if="idx > 0" size="small" type="danger" plain>新加</el-tag></div>
               <div class="h-qty">x{{ item.quantity }}</div>
            </div>
         </div>
       </div>
    </el-drawer>

    <div v-if="isTableSet" class="ai-ball" @click="chatVisible = true">🤖</div>
    <el-dialog v-model="chatVisible" title="智能助手" width="400px">
       <div class="chat-box">
          <div v-for="(m, i) in chatHistory" :key="i" :class="['bubble', m.role]">
             {{ m.content }}
             <div v-if="m.role === 'ai'" class="ai-suggest-box">
                <el-button 
                  v-for="dish in extractDishes(m.content)" 
                  :key="dish.id"
                  size="small" type="primary" plain round
                  @click="addDishFromAi(dish)"
                >
                  + 点一份 "{{ dish.name }}"
                </el-button>
             </div>
          </div>
       </div>
       <div style="display:flex; margin-top:10px; gap:8px">
          <el-input v-model="userQuery" placeholder="问问助手吃什么..." @keyup.enter="sendToAi"/>
          <el-button type="primary" @click="sendToAi">发送</el-button>
       </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import axios from "axios"
import { ElMessage } from "element-plus"
import { KnifeFork, Right } from '@element-plus/icons-vue'
import ProductList from '../components/ProductList.vue'
import PosUnit from '../components/PosUnit.vue'

// --- 数据定义 ---
const adImages = ['https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=2070','https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?q=80&w=1974']
const products = ref([]) 
const categories = ["全部", "肉类", "蔬菜", "酒水", "主食", "甜品", "其他"]
const activeCategory = ref("全部") 
const searchKey = ref("")
const cart = ref([]) 
const loading = ref(false)

const isTableSet = ref(false)
const tableId = ref(0)
const personCount = ref(2)
const tableDialogVisible = ref(false) 
const tableStates = ref([]) 
const selectedTableId = ref(null)
const tempPersonCount = ref(2)
const isAddingMeal = ref(false)

const payVisible = ref(false)
const currentOrderNo = ref('')
const userBalance = ref(0)
const detailVisible = ref(false)
const currentItem = ref(null)
const historyVisible = ref(false)
const tableHistory = ref([])

const chatVisible = ref(false)
const userQuery = ref("")
const chatHistory = ref([{role:'ai',content:'想吃什么就问我，我可以帮你直接下单！'}])
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')

// --- 计算属性 ---
const filteredProducts = computed(() => {
  let list = products.value
  if (activeCategory.value !== "全部") {
    list = list.filter(p => p.category === activeCategory.value || (!p.category && activeCategory.value === '其他'))
  }
  if (searchKey.value) {
    list = list.filter(p => p.name.includes(searchKey.value))
  }
  return list
})

const totalAmount = computed(() => cart.value.reduce((s,i) => s + i.price * i.quantity, 0).toFixed(2))

const historyGroups = computed(() => {
  if (!tableHistory.value.length) return []
  const groups = []
  let currentGroup = [tableHistory.value[0]]
  for (let i = 1; i < tableHistory.value.length; i++) {
    const prev = new Date(tableHistory.value[i-1].createTime).getTime()
    const curr = new Date(tableHistory.value[i].createTime).getTime()
    if (curr - prev > 60000) { groups.push(currentGroup); currentGroup = [] }
    currentGroup.push(tableHistory.value[i])
  }
  groups.push(currentGroup); return groups
})

// --- 🔥 AI 核心功能函数 ---
const extractDishes = (text) => {
  if (!text || !products.value.length) return [];
  // 扫描当前所有菜品名，看哪些出现在了 AI 回复里
  return products.value.filter(p => text.includes(p.name));
};

const addDishFromAi = (dish) => {
  addToCart(dish);
  ElMessage({ message: `已为您添加 [${dish.name}]`, type: 'success', offset: 80 });
}

// --- 业务逻辑方法 ---
const fetchProducts = async () => { 
  try {
    const res = await axios.get("http://localhost:8080/product/list")
    if (res.data.code === 200) products.value = res.data.data 
  } catch(e){}
}

const openTableSelector = async () => {
  const res = await axios.get("http://localhost:8080/order/table/status")
  if(res.data.code === 200) tableStates.value = res.data.data
  selectedTableId.value = null; tableDialogVisible.value = true
}

const isMyTable = (t) => t.status === 'busy' && t.orderInfo && String(t.orderInfo.userId) === String(currentUser.id)

const handleTableSelect = (t) => {
  if (t.status === 'busy') {
     if (isMyTable(t)) { selectedTableId.value = t.tableId; isAddingMeal.value = true }
     else { ElMessage.warning(`该桌有人`); selectedTableId.value = null }
  } else { selectedTableId.value = t.tableId; isAddingMeal.value = false }
}

const confirmTable = () => {
  tableId.value = selectedTableId.value; personCount.value = tempPersonCount.value; isTableSet.value = true; tableDialogVisible.value = false
  localStorage.setItem('currentTable', JSON.stringify({ id: tableId.value, person: personCount.value }))
  cart.value = []
}

const createOrder = async () => {
  loading.value = true
  try {
    const res = await axios.post("http://localhost:8080/order/create", { 
      tableId: tableId.value, userId: currentUser.id, 
      items: cart.value.map(i => ({ id: i.id, quantity: i.quantity, price: i.price })) 
    })
    if(res.data.code === 200) {
       currentOrderNo.value = res.data.data
       const b = await axios.get(`http://localhost:8080/user/info/${currentUser.id}`)
       userBalance.value = b.data.data.balance || 0
       payVisible.value = true
    }
  } catch(e) {} finally { loading.value = false }
}

const confirmPay = async (type) => {
   if (type === 'balance') {
      const res = await axios.post('http://localhost:8080/order/pay/balance', { orderNo: currentOrderNo.value, userId: currentUser.id })
      if(res.data.code !== 200) return ElMessage.error(res.data.msg)
   } else { await axios.put(`http://localhost:8080/order/pay/${currentOrderNo.value}`) }
   ElMessage.success("支付成功"); payVisible.value = false; cart.value = []; fetchProducts()
}

const addToCart = (item) => {
  if (item.stock <= 0) return; 
  const exist = cart.value.find(x => x.id === item.id)
  exist ? exist.quantity++ : cart.value.push({...item, quantity: 1})
}

const updateCart = (item, change) => {
  if (change > 0) addToCart(item)
  else { item.quantity--; if (item.quantity <= 0) cart.value = cart.value.filter(x => x.id !== item.id) }
}

const clearCart = () => { fetchProducts(); cart.value = [] }
const openDetail = (item) => { currentItem.value = item; detailVisible.value = true }
const openHistory = async () => { 
  historyVisible.value = true
  const res = await axios.get(`http://localhost:8080/order/table/${tableId.value}`)
  if(res.data.code === 200) tableHistory.value = res.data.data 
}

const sendToAi = async () => { 
  if(!userQuery.value) return
  chatHistory.value.push({role:'user',content:userQuery.value})
  const q = userQuery.value; userQuery.value='' 
  try{ 
    const res = await axios.get("http://localhost:8080/ai/chat",{params:{query:q}})
    chatHistory.value.push({role:'ai',content:res.data.data}) 
  }catch(e){ chatHistory.value.push({role:'ai',content:'掉线了...'}) }
}

const formatTime = (t) => t ? t.replace('T', ' ').substring(11, 16) : ''

onMounted(() => {
  fetchProducts()
  const cached = localStorage.getItem('currentTable')
  if (cached) {
    const { id, person } = JSON.parse(cached); tableId.value = id; personCount.value = person; isTableSet.value = true
  }
})
</script>

<style scoped>
.index-container { height: 100vh; overflow: hidden; background: #fff; font-family: -apple-system, sans-serif; }
.welcome-screen { position: relative; height: 100%; width: 100%; overflow: hidden; }
.ad-bg { width: 100%; height: 100vh; background-size: cover; background-position: center; position: relative; animation: zoom 20s infinite alternate; }
.ad-mask { position: absolute; inset: 0; background: rgba(0,0,0,0.3); } 
@keyframes zoom { from { transform: scale(1); } to { transform: scale(1.1); } }
.center-action { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 10; text-align: center; color: #fff; }
.brand-big h1 { font-size: 60px; font-weight: 900; margin: 0; text-shadow: 0 4px 20px rgba(0,0,0,0.5); }
.start-card { background: rgba(255, 255, 255, 0.95); padding: 20px 40px; border-radius: 100px; display: flex; align-items: center; gap: 20px; cursor: pointer; color: #333; margin-top: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); }
.work-layout { display: flex; height: 100%; }

/* AI 样式 */
.ai-ball { position: fixed; bottom: 30px; left: 30px; width: 50px; height: 50px; background: #000; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 24px; z-index: 100; transition: 0.2s; }
.chat-box { height: 320px; overflow-y: auto; padding: 10px; background: #f9fafb; border-radius: 8px; }
.bubble { padding: 8px 12px; border-radius: 8px; margin-bottom: 8px; font-size: 14px; max-width: 80%; }
.bubble.ai { background: #fff; border: 1px solid #eee; align-self: flex-start; }
.bubble.user { background: #000; color: #fff; margin-left: auto; }

.ai-suggest-box { margin-top: 10px; display: flex; flex-wrap: wrap; gap: 8px; border-top: 1px dashed #eee; padding-top: 8px; }

/* 桌台样式 */
.table-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; padding: 10px; }
.t-item { aspect-ratio: 1; background: #f5f7fa; border-radius: 8px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; border: 2px solid transparent; }
.t-item.busy { background: #fff0f0; color: #cf1322; }
.t-item.is-mine { background: #f6ffed !important; border-color: #b7eb8f !important; color: #389e0d !important; }
</style>