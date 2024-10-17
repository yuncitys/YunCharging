

<div align="center">

##   YunCharge开源充电桩收费管理平台

</div>

<div align="center" >
    <a href="http://wenhui.huizhidata.com">
        <img src="https://img.shields.io/badge/Licence-GPL3.0-green.svg?style=flat" />
    </a>
    <a href="http://wenhui.huizhidata.com">
        <img src="https://img.shields.io/badge/Edition-5.2-blue.svg" />
    </a>
     <a href="https://github.com/roinli/huige-ChargeOS-cloud/repository/archive/master.zip">
        <img src="https://img.shields.io/badge/Download-240m-red.svg" />
    </a>
</div>

#### 

<div align="center">

[官网](https://yuncitys.com) |
[在线体验](https://charge.yuncitys.com/) |
[帮助文档](https://yuncitys.com/yun_charging.html) |
</div>


### 关于我们

YunCharge是一套用于二轮电单车和四轮充电桩充电领域的运营和计费系统，已线上稳定运行30000+个充电桩设备，现全部源码100%开源出来！通过本项目可以学习单车桩、汽车桩的充电协议、充电收费规则等企业级功能的业务处理；后端技术采用主流的SpringCloud Alibaba微服务全家桶，前端采用Vue+ElementUI技术栈。无需复杂学习即可快速上手，功能强大UI界面清新，通过简要部署就能够助力企业组织快速赋能业务。

### 功能特征

🔥支持电单车（单车2路10路12路设备）🔥

🔥新能源汽车接入（云快充1.5、1.6+欧标协议OCPP1.5、OCPP 2.0）🔥

充电系统以微信、公众号为C端主要入口，为充电用户提供查桩找桩、设备信息查询、在线支付、充电状态查询、账户信息等服务，具备在线充值、支付、实时到账功能，给充电用户带来更加安全、便捷、贴心的充电体验。


### 在线文档

[充电项目系统文档在线地址](https://yuncitys.com/yun_charge_paper/)


基于vue-element-admin框架
主要由[vue2.0](https://cn.vuejs.org/)和[Elmentui2.0](https://element.faas.ele.me/#/zh-CN/component/installation)框架开发的


### 运行效果
![image](https://github.com/user-attachments/assets/7f36acd4-e770-43a1-8752-f168d587e804)

![image](https://github.com/user-attachments/assets/7827f38c-a72d-4579-a81f-37abb0e4dc67)

![image](https://github.com/user-attachments/assets/8b08af56-0172-44a8-9705-90d281c67374)

![image](https://github.com/user-attachments/assets/fc7bb3d1-f058-4995-b919-fe06f0577d13)

![image](https://github.com/user-attachments/assets/70b2031a-67ac-4763-82b7-8af98b9b178c)

![image](https://github.com/user-attachments/assets/a142c7b5-189f-41f1-9d5d-80e8fffb3ec2)

![image](https://github.com/user-attachments/assets/e4090379-0815-4a89-8643-550cf8a2bff9)


### 小程序
![image](https://github.com/user-attachments/assets/4f6701c0-380f-4c55-ba49-5cbdddd81677)



### 开发注意项

1.已对本项目进行移动端适配

2.开发环境下的接口请求域名在vue.config.js文件中，可自行切换本地或线上地址调试

3.生成环境的接口请求域名由后台在宝塔内配置，只需打包即可
原理：在.env.production文件中写入'/api',后台在宝塔配置后会自动将'/api'转为线上请求域名

4.本项目使用前端路由权限，所有跳转路由都得在前端配置

```

- Login / Logout

- Permission Authentication
  - Page permission
  - Directive permission
  - Permission configuration page
  - Two-step login

- Multi-environment build
  - Develop (dev)
  - sit
  - Stage Test (stage)
  - Production (prod)

- Global Features
  - I18n
  - Multiple dynamic themes
  - Dynamic sidebar (supports multi-level routing)
  - Dynamic breadcrumb
  - Tags-view (Tab page Support right-click operation)
  - Svg Sprite
  - Mock data
  - Screenfull
  - Responsive Sidebar

- Editor
  - Rich Text Editor
  - Markdown Editor
  - JSON Editor

- Excel
  - Export Excel
  - Upload Excel
  - Visualization Excel
  - Export zip

- Table
  - Dynamic Table
  - Drag And Drop Table
  - Inline Edit Table

- Error Page
  - 401
  - 404

- Components
  - Avatar Upload
  - Back To Top
  - Drag Dialog
  - Drag Select
  - Drag Kanban
  - Drag List
  - SplitPane
  - Dropzone
  - Sticky
  - CountTo

- Advanced Example
- Error Log
- Dashboard
- Guide Page
- ECharts
- Clipboard
- Markdown to html
```

### 开发

```
# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 启动服务
npm run dev
```


### 打包

```bash
# 构建生产环境
npm run build:prod
```

### YunCitys#2 交流群
QQ群：955562455 
