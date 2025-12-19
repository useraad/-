<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="title">🥦 菜品管理</div>
      <el-button type="primary" @click="openCreate">
        <el-icon style="margin-right:5px"><Plus /></el-icon> 新增菜品
      </el-button>
    </div>

    <el-table :data="list" stripe>
      <el-table-column label="图片" width="100">
        <template #default="{row}">
          <el-image :src="row.image" style="width:50px; height:50px; border-radius:4px" fit="cover"/>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="菜名" />
      <el-table-column prop="price" label="价格">
         <template #default="{row}">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="category" label="分类">
        <template #default="{row}"><el-tag>{{ row.category }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" />
      <el-table-column label="操作" align="right">
         <template #default="{row}">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">下架</el-button>
         </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜品' : '新增菜品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜名"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0"/></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0"/></el-form-item>
        <el-form-item label="分类">
           <el-select v-model="form.category">
              <el-option v-for="c in ['肉类','蔬菜','主食','酒水','甜品']" :key="c" :label="c" :value="c"/>
           </el-select>
        </el-form-item>
        <el-form-item label="图片URL"><el-input v-model="form.image" placeholder="http://..."/></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" v-model="form.description"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProduct">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const list = ref([])
const dialogVisible = ref(false)
const form = reactive({ id: null, name: '', price: 0, category: '', stock: 100, image: '', description: '' })

const loadData = async () => {
  const res = await axios.get('http://localhost:8080/product/list')
  if(res.data.code === 200) list.value = res.data.data
}

const openCreate = () => {
  Object.assign(form, { id: null, name: '', price: 0, category: '', stock: 100, image: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitProduct = async () => {
  const url = form.id ? 'http://localhost:8080/product/update' : 'http://localhost:8080/product/add'
  const method = form.id ? 'put' : 'post'
  await axios[method](url, form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定下架该菜品吗？').then(async () => {
     await axios.delete(`http://localhost:8080/product/${row.id}`)
     ElMessage.success('已删除')
     loadData()
  })
}

onMounted(loadData)
</script>
<style scoped>
.page-container { padding: 20px; background: #fff; border-radius: 8px; }
.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; }
</style>