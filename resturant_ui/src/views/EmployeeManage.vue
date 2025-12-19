<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="title">👥 员工管理</div>
      <el-button type="primary" @click="openCreate">
        <el-icon style="margin-right:5px"><Plus /></el-icon> 新增员工
      </el-button>
    </div>

    <div class="table-box">
      <el-table :data="list" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        
        <el-table-column prop="username" label="登录账号">
           <template #default="{row}">
             <span style="font-weight:bold">{{ row.username }}</span>
           </template>
        </el-table-column>

        <el-table-column prop="name" label="姓名" />

        <el-table-column prop="role" label="角色" align="center">
           <template #default="{row}">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">{{ row.role }}</el-tag>
           </template>
        </el-table-column>

        <el-table-column label="操作" align="right">
           <template #default="{row}">
              <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
           </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="新增员工" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="用于登录" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" placeholder="默认 123456" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio label="STAFF">普通员工</el-radio>
            <el-radio label="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const list = ref([])
const dialogVisible = ref(false)
const form = reactive({ username: '', password: '', name: '', role: 'STAFF' })

const loadData = async () => {
  // 注意：这里需要后端有获取所有用户的接口，通常复用 user/list
  // 如果没有，可以先模拟，或者自己加一个接口
  try {
     const res = await axios.get('http://localhost:8080/user/list') 
     // 过滤掉顾客，只看员工
     if(res.data.code === 200) {
       list.value = res.data.data.filter(u => u.role !== 'USER')
     }
  } catch(e) { console.log('需后端支持列表接口') }
}

const openCreate = () => {
  form.username = ''; form.password = '123456'; form.name = ''; form.role = 'STAFF'
  dialogVisible.value = true
}

const submitCreate = async () => {
  if(!form.username) return ElMessage.warning('请输入账号')
  await axios.post('http://localhost:8080/user/register', form)
  ElMessage.success('添加成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该员工吗？', '警告', { type: 'warning' }).then(async () => {
     await axios.delete(`http://localhost:8080/user/${row.id}`)
     ElMessage.success('已删除')
     loadData()
  })
}

onMounted(loadData)
</script>

<style scoped>
.page-container { background: #fff; border-radius: 8px; padding: 20px; min-height: 80vh; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.title { font-size: 18px; font-weight: bold; color: #333; }
.table-box { border: 1px solid #eee; border-radius: 4px; }
</style>