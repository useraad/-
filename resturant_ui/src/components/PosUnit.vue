<template>
  <div class="pos-unit">
    <div class="pos-header">
      <div class="title">当前订单</div>
      <div v-if="isTableSet" class="table-badge" @click="$emit('set-table')">
        <span>{{ tableInfo.id }}号桌</span>
        <span class="p-num">{{ tableInfo.person }}人</span>
        <el-icon><EditPen /></el-icon>
      </div>
      <div v-else class="table-alert" @click="$emit('set-table')">
        🛑 点击开台
      </div>
    </div>

    <div class="cart-body">
      <div v-if="!isTableSet" class="empty-tip">
        <el-icon size="30"><Lock /></el-icon>
        <p>请先开台</p>
      </div>
      <div v-else-if="cart.length === 0" class="empty-tip">
        <el-icon size="30"><ShoppingCart /></el-icon>
        <p>购物车是空的</p>
      </div>
      <div v-else class="cart-list">
        <div v-for="item in cart" :key="item.id" class="c-item">
          <div class="c-info">
            <div class="n">{{ item.name }}</div>
            <div class="p">
              <span style="font-size:12px; color:#999">¥{{ item.price }} x {{ item.quantity }} = </span>
              <span style="color:#ef4444; font-weight:bold">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          <div class="c-ctrl">
            <button @click="$emit('update', item, -1)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="$emit('update', item, 1)">+</button>
          </div>
        </div>
      </div>
    </div>

    <div class="pos-footer">
      <div class="bill-row">
        <span>总计</span>
        <span class="total">¥{{ totalAmount }}</span>
      </div>
      
      <div class="action-grid">
        <button class="btn-clear" @click="$emit('clear')">清空</button>
        <button 
          class="btn-pay" 
          :disabled="!isTableSet || cart.length === 0"
          @click="$emit('checkout')"
        >
          <span v-if="loading">...</span>
          <span v-else>下单 / 支付</span>
        </button>
      </div>
      
      <div class="history-link" @click="$emit('history')">
        查看历史订单
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { EditPen, Lock, ShoppingCart } from '@element-plus/icons-vue'

const props = defineProps(['cart', 'isTableSet', 'tableInfo', 'loading'])
const emit = defineEmits(['update', 'clear', 'checkout', 'history', 'set-table'])

const totalAmount = computed(() => 
  props.cart.reduce((sum, i) => sum + (Number(i.price) * i.quantity), 0).toFixed(2)
)
</script>

<style scoped>
.pos-unit {
  width: 340px; background: #fff; border-left: 1px solid #eee;
  display: flex; flex-direction: column; z-index: 10;
  box-shadow: -5px 0 20px rgba(0,0,0,0.02);
}
.pos-header {
  padding: 15px 20px; border-bottom: 1px solid #f9f9f9;
  display: flex; justify-content: space-between; align-items: center;
}
.title { font-weight: 700; font-size: 15px; color: #333; }
.table-badge { font-size: 13px; font-weight: 600; background: #f0fdf4; color: #166534; padding: 4px 12px; border-radius: 20px; cursor: pointer; display: flex; align-items: center; gap: 5px; }
.p-num { opacity: 0.7; font-weight: normal; margin-right: 3px; }
.table-alert { color: #ef4444; font-weight: bold; cursor: pointer; font-size: 13px; animation: pulse 1s infinite; }
@keyframes pulse { 50% { opacity: 0.5; } }

.cart-body { flex: 1; overflow-y: auto; padding: 15px; }
.empty-tip { height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #ccc; gap: 10px; font-size: 12px; }
.c-item { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px dashed #f5f5f5; }
.c-info .n { font-size: 14px; font-weight: 500; color: #333; margin-bottom: 2px; }
.c-info .p { font-size: 12px; }
.c-ctrl { display: flex; align-items: center; background: #f9f9f9; padding: 2px; border-radius: 4px; }
.c-ctrl button { width: 22px; height: 22px; border: 1px solid #eee; background: #fff; border-radius: 4px; cursor: pointer; font-weight: bold; }
.c-ctrl button:active { background: #eee; }
.c-ctrl span { width: 24px; text-align: center; font-size: 12px; font-weight: bold; }

.pos-footer { padding: 20px; background: #fff; border-top: 1px solid #f5f5f5; }
.bill-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.total { font-size: 22px; font-weight: 800; color: #111; }
.action-grid { display: flex; gap: 10px; }
.btn-pay { flex: 2; height: 44px; background: #111; color: #fff; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 14px; }
.btn-pay:disabled { background: #eee; color: #999; cursor: not-allowed; }
.btn-clear { flex: 1; background: #fdf2f2; color: #ef4444; border: none; border-radius: 8px; cursor: pointer; font-weight: 500; font-size: 13px; }
.history-link { text-align: center; margin-top: 12px; font-size: 12px; color: #999; cursor: pointer; text-decoration: underline; }
</style>