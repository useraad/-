<template>
  <div class="dashboard-container">
    <h2>📊 经营数据看板</h2>
    
    <el-row :gutter="20" class="card-row">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card revenue">
           <div class="card-title">今日营收</div>
           <div class="card-num">¥{{ stats.todayRevenue }}</div>
           <div class="card-tip">实时到账金额</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card orders">
           <div class="card-title">今日订单</div>
           <div class="card-num">{{ stats.todayOrderCount }} 单</div>
           <div class="card-tip">后厨已接单数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card users">
           <div class="card-title">员工总数</div>
           <div class="card-num">3 人</div>
           <div class="card-tip">在岗服务人员</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card products">
           <div class="card-title">菜品总数</div>
           <div class="card-num">72 道</div>
           <div class="card-tip">在售菜品库存</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
           <template #header><span style="font-weight:bold">🔥 热销菜品 Top 5</span></template>
           <div id="pieChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
           <template #header><span style="font-weight:bold">📈 近7天销售趋势</span></template>
           <div id="lineChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

// 定义响应式数据
const stats = reactive({
  todayRevenue: 0,
  todayOrderCount: 0,
  topProducts: [],
  trendDates: [],   // 真实日期数组
  trendAmounts: []  // 真实金额数组
})

// 初始化图表
const initCharts = () => {
  // --- 1. 饼图 (热销) ---
  const pieChartDom = document.getElementById('pieChart')
  // 防止 DOM 还没渲染导致报错
  if (pieChartDom) {
    const pieChart = echarts.init(pieChartDom)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%' },
      series: [
        {
          name: '销量',
          type: 'pie',
          radius: ['40%', '70%'], // 环形
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
          data: stats.topProducts // 后端返回的 [{name: 'xx', value: 10}, ...]
        }
      ]
    })
  }

  // --- 2. 折线图 (趋势) ---
  const lineChartDom = document.getElementById('lineChart')
  if (lineChartDom) {
    const lineChart = echarts.init(lineChartDom)
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { 
        type: 'category', 
        boundaryGap: false,
        data: stats.trendDates // 使用后端返回的日期轴
      },
      yAxis: { type: 'value' },
      series: [{
        name: '营业额',
        data: stats.trendAmounts, // 使用后端返回的金额数据
        type: 'line',
        smooth: true,
        itemStyle: { color: '#409EFF' },
        areaStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.5)' },
            { offset: 1, color: 'rgba(64,158,255,0.01)' }
          ])
        }
      }]
    })
    
    // 监听窗口缩放，自适应图表大小
    window.addEventListener('resize', () => {
      echarts.getInstanceByDom(pieChartDom)?.resize()
      echarts.getInstanceByDom(lineChartDom)?.resize()
    })
  }
}

// 加载数据
const loadData = async () => {
  try {
    const res = await axios.get('http://localhost:8080/stats/dashboard')
    if(res.data.code === 200) {
      const d = res.data.data
      
      // 赋值给响应式对象
      stats.todayRevenue = d.todayRevenue
      stats.todayOrderCount = d.todayOrderCount
      stats.topProducts = d.topProducts
      stats.trendDates = d.trendDates
      stats.trendAmounts = d.trendAmounts
      
      // 数据准备好后，再渲染图表
      initCharts()
    }
  } catch (error) {
    console.error("获取看板数据失败", error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard-container { padding: 20px; }
.card-row { margin-bottom: 20px; }
.data-card { color: #fff; cursor: pointer; transition: transform 0.2s; border: none; }
.data-card:hover { transform: translateY(-5px); }

/* 卡片渐变配色 */
.revenue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.orders { background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%); }
.users { background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%); }
.products { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); }

.card-title { font-size: 14px; opacity: 0.8; }
.card-num { font-size: 28px; font-weight: bold; margin: 10px 0; }
.card-tip { font-size: 12px; opacity: 0.6; }
</style>