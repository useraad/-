springboot+vue3的前后端分离项目，创新点在协同过滤算法和ai助手的引入可做毕设或者结课项目，部署简单，小白也可

1. AI 智能点餐助手 (AI Ordering Assistant)
自然语言对话：内置 AI 助手，支持用户通过自然语言咨询菜品，提升交互趣味性。可自行调整ai助手的 说话方式也可亲和，

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
<img width="2520" height="1295" alt="image" src="https://github.com/user-attachments/assets/bfb5a504-369f-454c-a7e4-a7bef96caa79" />
<img width="2482" height="1241" alt="image" src="https://github.com/user-attachments/assets/bbc4f672-81a8-4716-a328-d98df88ebd02" />
<img width="2509" height="1297" alt="image" src="https://github.com/user-attachments/assets/93d01da5-56b1-42c4-b846-7c2199ae51a7" />
<img width="2479" height="1325" alt="image" src="https://github.com/user-attachments/assets/9772b1a5-0752-4f2c-b54c-e90a107511ff" />
<img width="2485" height="1299" alt="image" src="https://github.com/user-attachments/assets/52ea5abf-3bd4-4608-b645-adf507e8ba31" />
<img width="2462" height="1220" alt="image" src="https://github.com/user-attachments/assets/a862310a-d5f2-4c16-a458-74afab1b828d" />
<img width="2485" height="1265" alt="image" src="https://github.com/user-attachments/assets/bb4eab16-b593-4626-a497-d42556146f4e" />
<img width="2502" height="1249" alt="image" src="https://github.com/user-attachments/assets/c66388aa-a02e-42c1-976e-5e71d6d5c8ff" />
<img width="2457" height="1250" alt="image" src="https://github.com/user-attachments/assets/d349ce77-293c-4590-b2a4-770b1be945fc" />
<img width="2515" height="1262" alt="image" src="https://github.com/user-attachments/assets/1c57af05-8076-4d0a-8af5-c4d3f6bdaa15" />
<img width="2560" height="1377" alt="image" src="https://github.com/user-attachments/assets/b86e4976-d72d-4ad1-8042-6783ee56026d" />
<img width="2559" height="1345" alt="image" src="https://github.com/user-attachments/assets/6dedd2ae-bffa-48e0-9864-904668021afc" />
<img width="2396" height="1256" alt="image" src="https://github.com/user-attachments/assets/69f54834-3277-4e91-9ee4-df4c39ba84d9" />
欢迎下载，感谢支持
![db69b453f5e5d510e7c45bdd290d240](https://github.com/user-attachments/assets/e568b645-3c19-4452-9a35-00d3f32331b7)

