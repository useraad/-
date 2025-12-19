1. AI 智能点餐助手 (AI Ordering Assistant)
自然语言对话：内置 AI 助手，支持用户通过自然语言咨询菜品，提升交互趣味性。

一键加购技术：前端实时解析 AI 回复文本中的菜品关键词，动态比对数据库菜品库，并在回复气泡下方自动生成“点一份”快捷按钮，极大缩短点餐路径。
2. 数字化桌台管理 (Digital Table Monitoring)
实时状态图谱：12 宫格实时展示桌位状态（空闲、占用、当前用户占用）。

加餐逻辑优化：通过 TableID 与 UserID 的双重校验，确保“加餐”功能仅对当前就餐者开放，防止数据错乱。

后端 (Backend - demo/)
核心框架：Spring Boot 2.7

持久层：MyBatis-Plus

数据库：MySQL 8.0

功能实现：Restful API 设计、Lambda 表达式条件构造、金额精确运算 (BigDecimal)

前端 (Frontend - resturant_ui/)
核心框架：Vue 3 (Composition API)

UI 组件库：Element Plus

交互技术：Axios 拦截、Vue Router 导航守卫、响应式布局 (Flex/Grid)
<img width="2527" height="1259" alt="image" src="https://github.com/user-attachments/assets/1f938c90-3110-4385-8971-975bac8166a5" />
