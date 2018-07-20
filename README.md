LeanMessage WebApp
====

使用 LeanCloud 实时通讯搭建的聊天 WebApp。[Live Demo](https://leanmessage.leanapp.cn/)

> 在找 iOS/Android Demo？试试我们全新设计的 ChatKit 吧：[iOS](https://github.com/leancloud/ChatKit-OC)、[Android](https://github.com/leancloud/LeanCloudChatKit-Android)。

### 功能

- [x] 登录
- [x] 聊天室、单聊、群聊
- [x] 系统对话实现的聊天机器人
- [x] 成员管理
  - [x] 邀请、移除成员
  - [x] 提升、撤销管理员
- [x] 群名称修改
- [x] 消息送达回执与已读回执
- [x] 未读消息
- [x] 「正在输入」状态
- [x] 消息撤回
- [x] 图片消息
- [x] 提及（@）对话成员
- [x] 表情
  - [x] 小表情（Emoji）
  - [x] 大表情（Sticker）

### 文档

[实时通讯 JavaScript SDK 使用文档](https://leancloud.cn/docs/realtime_guide-js.html)

### 本地开发、调试


1. `npm install`
2. `npm run serve`
3. `open http://localhost:3000/`

### 部署


1. 运行 `npm run build` 生成 dist 目录
2. 将 dist 目录作为静态资源部署

实时通讯服务是通过校验 origin 来保证安全的，项目中默认的 AppID 是 LeanCloud 提供的测试 App 的 ID，仅允许 localhost:3000 来源的访问，当你需要在另外的域名上部署 LeanMessage WebApp 时，需要将 src/index.js 中的 appId 改为你的 LeanCloud 项目的 App ID，并且确认设置了你的域名为该项目的「安全域名」，更多关于安全方面的信息请参考 [Web 安全域名](https://leancloud.cn/docs/data_security.html#Web_安全域名)。

### 代码结构与功能

```
src/
├── app
│   ├── components      // 组件
│   │   ├── message                 // 消息 directive
│   │   ├── reverse-infinite-list   // 向上滚动无限加载列表 directive
│   │   └── user                    // 用户相关 service
│   ├── conversation    // 会话 view，app 主界面
│   │   └── conversationMessage     // 消息 view，会话 view 的子 view
│   └── login           // 登录 view
├── index.html          // 页面入口，ui-view 容器
├── index.js            // js 入口，注册依赖模块，启动 app
├── index.run.js        // 启动 app 的代码
├── index.scss
└── routes.js           // 配置路由
```
![leanmessage-views](https://cloud.githubusercontent.com/assets/175227/9702711/598c3390-549d-11e5-86c7-32595fbb9013.png)

#### index.js
js 入口，申明依赖模块，配置依赖模块，注册 components 与 views，启动 app。
依赖的 LeanCloud 模块有:
- [leancloud-realtime](https://leancloud.cn/docs/realtime_guide-js.html)

依赖的 angular 模块有：
- [ui.router](https://github.com/angular-ui/ui-router)：基于状态的 router
- [ngMaterial](https://material.angularjs.org/)：提供 material design UI 组件

#### routes.js
配置 ui.router

#### index.run.js
启动 app 的脚本，通过监听 ui.router 的 `stateChangeStart` 事件在视图切换时检查用户登录状态。

### How to

#### 实现用户系统
为了方便与其他系统对接，LeanMessage 服务没有内建用户系统，只有作为唯一标识的 [clientId](https://leancloud.cn/docs/realtime_v2.html#clientId)，你需要在你的用户系统中维护用户与 clientId 的关系。你可以使用 LeanStorage 提供的[「用户系统」](https://leancloud.cn/docs/js_guide.html#用户)。
本 demo 中没有关联用户系统，可以用任何的 id 登录服务。
