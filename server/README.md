LeanMessage MathBot Server
----

MathBot 是一个自动回复机器人，能响应实时通讯服务的系统消息 hook，计算用户发送的数学表达式，然后通过 REST API 将结果发回给用户。关于系统消息，请参考[概念](https://leancloud.cn/docs/realtime_v2.html#系统对话_System_Conversation_)与[文档](https://leancloud.cn/docs/realtime_v2.html#系统对话)。

需要特别指出的是，得益于 LeanCloud 实时通讯服务，LeanMessage Demo 本身不需要部署后端服务。

### 部署 MathBot

1. 使用 REST API [创建一个系统对话](https://leancloud.cn/docs/realtime_rest_api.html#创建一个对话)，记下 Conversation ID。
2. 将本目录的代码[部署到 LeanEngine](https://leancloud.cn/docs/cloud_code_commandline.html#部署)。你也可以部署到其他的 node 运行环境上，但记得定义环境变量 `LC_APP_ID`、`LC_APP_KEY`、`LC_APP_MASTER_KEY`。
3. 在控制台的「消息」-「实时消息设置」-「消息回调设置」中填入 LeanEngine 提供服务的地址：http://yourdomain.avosapps.com/webhook

接下来就可以加入刚才创建的系统对话，给 MathBot 发消息测试了。
