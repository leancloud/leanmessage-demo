# LeanMessage Demo 使用指南
为了直观地展现 LeanCloud 实时消息服务的 SDK 使用方法，官方诚意推出新版的 LeanMessage Demo 给开发者使用和学习。

当前仓库里包含了 3 个版本 SDK 的演示，包含

* iOS |[iOS 官方文档](https://leancloud.cn/docs/realtime_guide-ios.html)
* Android |[Android 官方文档](https://leancloud.cn/docs/realtime_guide-android.html)
* Web（JavaScript）| [JavaScript 官方文档](https://leancloud.cn/docs/js_realtime.html)|[Web Demo 线上地址](http://leancloud.github.io/leanmessage-demo)

三个版本是「互通」的，可以直接打开任意 2 个版本的 Demo 进行实时聊天。

以下将是各个版本的 Demo 如何使用，以及 SDK API 调用方法的相关介绍。建议对照代码进行阅读，效果更好。


> 在深度调研 LeanMessage 之前，强烈建议您提前阅读[实时通信服务开发指南](https://leancloud.cn/docs/realtime_v2.html)，该文档详细的阐述了我们针对实时通信设计的几个重要抽象概念以及交互流程，其中包含了对话，ClientId，权限认证，系统对话（类似系统群发）等诸多解释，阅读之后再继续调研 Demo 效果更佳。