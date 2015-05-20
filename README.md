# LeanMessage Demo

此项目是为了大家能快速熟悉 LeanCloud 的实时通信功能 LeanMessage ，精简了 UI，突出了核心功能。同时也支持了多个平台，

* [LeanMessageDemo-android](./Android)
* [LeanMessageDemo-ios](./iOS)
* [LeanMessageDemo-web](https://github.com/leancloud/js-realtime-sdk)，[在线地址](http://leancloud.github.io/js-realtime-sdk/demo/demo2/)

![leanmessagedemo](https://cloud.githubusercontent.com/assets/5022872/7699368/f71e201e-fe49-11e4-8c82-5d53dfc95b24.jpg)

## iOS App 运行

```
   cd iOS/LeanMessageDemo
   pod install
   open LeanMessageDemo.xcworkspace
```

## Android App 运行

直接用 Android Studio open `./Android` 。

## Web Page 运行
```
  git submodule init
  git submodule update
  cd JavaScript/demo/demo2
  avoscloud
```

`avoscloud` 是[云代码命令行工具](https://leancloud.cn/docs/cloud_code_commandline.html)。若 `git submodule` 使用有困惑，可直接前往 [相关项目](https://github.com/leancloud/js-realtime-sdk) 。

## 文档

[实时通信服务开发指南](https://leancloud.cn/docs/realtime_v2.html)

