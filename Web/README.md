LeanMessage WebApp
====

本地开发、调试
----

1. 进入 Web 目录
2. `npm install && bower install`
3. `gulp serve`
4. `open http://localhost:3000/`

部署
----

1. 运行 `gulp build` 生成 dist 目录
2. 将 dist 目录作为静态资源部署

实时通讯服务是通过校验 origin 来保证安全的，项目中默认的 AppID 是 LeanCloud 提供的测试 App 的 ID，仅允许 localhost:3000 来源的访问，当你需要在另外的域名上部署 LeanMessage WebApp 时，需要将 src/app/components/user/user.service.js 中的 appId 改为你的 LeanCloud 项目的 App ID，并且确认设置了你的域名为该项目的「安全域名」，更多关于安全方面的信息请参考 [Web 安全域名](https://leancloud.cn/docs/data_security.html#Web_安全域名)。

代码结构与功能
----
```
src
├── app
│   ├── components      // 组件
│   │   ├── conversation-cache    // 会话本地缓存 service
│   │   ├── message               // 消息 directive
│   │   ├── reverse-infinite-list // 向上滚动无限加载列表 directive
│   │   └── user                  // 用户相关 service
│   ├── conversation    // 会话 view，app 主界面
│   │   └── conversation-message  // 消息 view，会话 view 的子 view
│   ├── index.config.js // 配置依赖模块
│   ├── index.module.js // js 入口，注册依赖模块，启动 app
│   ├── index.route.js  // 配置路由
│   ├── index.run.js    // 启动 app 的代码
│   ├── index.scss
│   └── login           // 登录 view
├── assets
│   ├── fonts
│   └── images
├── favicon.ico
└── index.html  // 页面入口，ui-view 容器
```
![leanmessage-views](https://cloud.githubusercontent.com/assets/175227/9702711/598c3390-549d-11e5-86c7-32595fbb9013.png)

### index.\*.js
#### index.module.js
js 入口，申明依赖模块，配置依赖模块，注册 components 与 views，启动 app。
依赖的 angular 模块有：
- [leancloud-realtime](https://github.com/leeyeh/angular-leancloud-realtime)：leancloud realtime SDK 的 angular 包装
- [ui.router](https://github.com/angular-ui/ui-router)：基于状态的 router
- [ngMaterial](https://material.angularjs.org/)：提供 material design UI 组件

#### index.route.js
配置 ui.router

#### index.config.js
配置其他依赖模块

#### index.run.js
启动 app 的脚本，通过监听 ui.router 的 `stateChangeStart` 事件在视图切换时检查用户登录状态。

###Views
#### Login
通过 UserService 的 login 方法，最终调用了 SDK 的  [`AV.realtime()`](https://leancloud.cn/docs/js_realtime.html#AV_realtime)，与实时通讯 server 建立长连接。然后跳转到 conversation 视图。

#### Coversation
聊天主界面，包括了当前用户的对话列表以及子视图 `ConversationMessage`。
这个视图主要做了这些事情：

- 获取当前用户的所有会话，按照群聊单聊分类。
- 当新用户没有加入任何会话时，自动加入一个预设的群聊会话（defaultConversation）。
- 点击会话列表时让 `ConversationMessage` 视图转到对应的会话状态。
- 响应 rt 的 `message` 事件，维护会话的未读消息数。
- 响应 rt 的 `message` 事件、rt 的 `join` 事件，响应 `ConversationMessage` scope 的 `conv.messagesent` 事件，维护消息列表，将其按照最后消息时间排序。
- 点击 Logout 按钮时调用 UserService 的 login 方法进行注销。然后跳转到 Login 视图。

#### CoversationMessage
聊天消息视图，`Coversation` 的子视图，由工具栏、消息列表、输入框以及在线用户列表侧边栏组成。其中暂态对话没有「在线用户列表」
」概念。
主要做了这些事情：
- 根据当前的路由信息，获取或创建一个会话。
- 获取该会话的历史消息记录，用户滚到顶部时加载更多历史消息。
- 响应该会话的 `message` 事件，在列表中显示消息内容。
- （可用的话）获取该会话的在线用户列表，提供本地的查询功能。
- 通知 `Conversation` 视图用以清除该会话的未读消息计数。
- 发送消息，通知 `Conversation` 视图用以调整会话列表排序。

### Components
#### Message
Message directive 封装了消息的样式，需要传入四个属性：
- `message`：根据不同的消息类型展示不同的消息
- `previousMessage`：根据上一条消息与当前消息的时间戳决定是否显示消息时间
- `isMine`：标记是用户自己发送的消息
- `onNameClick`：点击消息发送方 ID 的回调

#### ReverseInfiniteList
封装了向上滚动到容器顶部时触发回调的 directive。

#### User
提供用户登录、注销、获取用户登录状态、用户信息的 service。

How to
----
### 实现用户系统
为了方便与其他系统对接，LeanMessage 服务没有内建用户系统，只有作为唯一标识的 [clientId](https://leancloud.cn/docs/realtime_v2.html#clientId)，你需要在你的用户系统中维护用户与 clientId 的关系。你可以使用 LeanStorage 提供的[「用户系统」](https://leancloud.cn/docs/js_guide.html#用户)。
本 demo 中没有关联用户系统，可以用任何的 id 登录服务，UserService 实现了基于 localStorage 的客户端持久化方案。

### 区分单聊与群聊
LeanMessage 服务本身不区分单聊与群聊，demo 中在会话属性（`attr`）中增加了一个标识符 `customConversationType` 来标识这是一个单聊会话。
