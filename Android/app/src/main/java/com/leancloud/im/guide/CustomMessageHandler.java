package com.leancloud.im.guide;

import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;

/**
 * Created by zhangxiaobo on 15/4/20.
 */
class CustomMessageHandler extends AVIMMessageHandler {

  private static final String TAG = CustomMessageHandler.class.getSimpleName();

  @Override
  public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
    // 新消息到来了。在这里增加你自己的处理代码。
    String msgContent = message.getContent();
    Log.d(TAG, " 收到一条新消息：" + msgContent);
  }

}
