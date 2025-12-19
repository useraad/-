<template>
  <div class="product-area">
    <div class="area-header">
      <div class="brand">🔥 <span class="b-text">SmartPOS</span></div>
      <div class="search-box">
        <el-icon class="s-icon"><Search /></el-icon>
        <input 
          :value="searchKey" 
          @input="$emit('update:searchKey', $event.target.value)" 
          placeholder="搜索菜品..." 
        />
      </div>
    </div>

    <div class="content-row">
      <div :class="['cat-sidebar', { collapsed: isCollapsed }]">
        <div 
          v-for="cat in categories" :key="cat"
          :class="['cat-item', { active: activeCategory === cat }]"
          @click="$emit('select-category', cat)"
        >
           <span class="icon">●</span>
           <span class="text" v-show="!isCollapsed">{{ cat }}</span>
        </div>
        <div class="toggle-btn" @click="isCollapsed = !isCollapsed">
           <el-icon><component :is="isCollapsed ? 'Expand' : 'Fold'" /></el-icon>
        </div>
      </div>

      <div class="prod-scroll">
        <div class="prod-grid">
          <div v-for="item in products" :key="item.id" class="p-card" @click="$emit('open-detail', item)">
            <div class="p-img">
              <img v-if="item.image" :src="item.image" loading="lazy"/>
              <div v-else class="ph">{{ item.name.charAt(0) }}</div>
              <div v-if="item.stock <= 0" class="mask">SOLD OUT</div>
            </div>
            <div class="p-info">
              <div class="name">{{ item.name }}</div>
              <div class="row">
                <span class="price">¥{{ item.price }}</span>
                <button v-if="item.stock > 0" class="add" @click.stop="$emit('add', item)">
                  <el-icon><Plus /></el-icon>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search, Plus, Fold, Expand } from '@element-plus/icons-vue'

defineProps(['products', 'categories', 'activeCategory', 'searchKey'])
defineEmits(['update:searchKey', 'select-category', 'add', 'open-detail'])

const isCollapsed = ref(false)
</script>

<style scoped>
.product-area { flex: 1; display: flex; flex-direction: column; overflow: hidden; background: #f4f5f7; }
.area-header { height: 60px; display: flex; align-items: center; padding: 0 20px; background: #fff; border-bottom: 1px solid #eee; justify-content: space-between; }
.brand { font-size: 18px; font-weight: 800; display: flex; align-items: center; gap: 8px; }
.search-box { background: #f4f5f7; border-radius: 20px; padding: 8px 15px; display: flex; align-items: center; gap: 8px; width: 220px; transition: 0.3s; }
.search-box:focus-within { background: #fff; box-shadow: 0 0 0 2px #eee; }
.search-box input { border: none; background: transparent; outline: none; width: 100%; font-size: 13px; }
.s-icon { color: #999; }

.content-row { flex: 1; display: flex; overflow: hidden; }

/* 分类栏 */
.cat-sidebar { width: 140px; background: #fff; border-right: 1px solid #eee; display: flex; flex-direction: column; transition: 0.3s cubic-bezier(0.25, 0.8, 0.25, 1); padding: 10px 0; }
.cat-sidebar.collapsed { width: 48px; }
.cat-item { padding: 12px 18px; cursor: pointer; color: #666; font-size: 14px; font-weight: 500; display: flex; align-items: center; gap: 10px; white-space: nowrap; overflow: hidden; height: 44px; }
.cat-item:hover { background: #f9f9f9; color: #000; }
.cat-item.active { background: #f0fdf4; color: #166534; border-right: 3px solid #166534; }
.cat-item .icon { font-size: 6px; color: #ddd; }
.cat-item.active .icon { color: #166534; }
.toggle-btn { margin-top: auto; padding: 15px; text-align: center; color: #999; cursor: pointer; border-top: 1px solid #eee; }
.toggle-btn:hover { color: #333; }

/* 商品网格 */
.prod-scroll { flex: 1; overflow-y: auto; padding: 20px; }
.prod-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 15px; }
.p-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: 0.2s; border: 1px solid #f0f0f0; }
.p-card:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.05); border-color: #e0e0e0; }
.p-img { height: 130px; background: #f8f8f8; position: relative; display: flex; align-items: center; justify-content: center; }
.p-img img { width: 100%; height: 100%; object-fit: cover; }
.ph { font-size: 40px; color: #ddd; font-weight: bold; }
.mask { position: absolute; inset: 0; background: rgba(255,255,255,0.9); display: flex; align-items: center; justify-content: center; font-weight: 800; color: #ccc; letter-spacing: 2px; }
.p-info { padding: 12px; }
.name { font-size: 15px; font-weight: 600; color: #333; margin-bottom: 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.row { display: flex; justify-content: space-between; align-items: center; }
.price { color: #ef4444; font-weight: 800; font-size: 16px; }
.add { width: 26px; height: 26px; background: #111; color: #fff; border: none; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.add:hover { transform: scale(1.1); }
</style>