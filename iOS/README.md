## iOS 

### 如何运行

1. Clone 项目到本地
2. 在根目录下，执行以下命令行
``` 
pod install
```
然后等待，可能因为「某些网络原因」安装 SDK 的时间会稍微长一点。
3. 用 XCode 打开
```
/iOS/LeanMessageDemo/LeanMessageDemo.xcworkspace
```
项目文件，就可以直接运行调试了。

### 实现的功能
iOS 实现的功能可以参照 [Release Notes](https://github.com/leancloud/leanmessage-demo/releases)
#### 登陆页面
需要手动输入一个全局唯一的字符串作为当前用户的唯一标识，这个标识在 LeanMessage 中被定义为：ClientId，任意一个不超过50长度的字符串。

![login_preview][1]

#### 广场页面
广场实现的功能是一个大型的开放的聊天室，可以随意的发送消息，所有在聊天并且在线的用户都会收到。

![chatroom_preview][2]

#### 单聊页面
从广场聊天记录中点击任意一个 ClientId 就会进入到与该 ClientId 的单聊。

![singlechat_preview][3]



  [1]: http://ac-lhzo7z96.clouddn.com/1441166276702
  [2]: http://ac-lhzo7z96.clouddn.com/1441164151869
  [3]: http://ac-lhzo7z96.clouddn.com/1441166213233