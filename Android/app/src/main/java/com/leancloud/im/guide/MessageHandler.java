package com.leancloud.im.guide;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

/**
 * Created by zhangxiaobo on 15/4/20.
 */
class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
  private static AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler;
  private Context context;
  private String TAG = MessageHandler.this.getClass().getSimpleName();

  public MessageHandler(Context context) {
    this.context = context;
  }

  @Override
  public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
    Log.d(TAG, "消息已到达对方" + message.getContent());
  }

  @Override
  public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
    if (activityMessageHandler != null) {
      // 正在聊天时，分发消息，刷新界面
      activityMessageHandler.onMessage(message, conversation, client);
    } else {
      // 没有打开聊天界面，这里简单地 Toast 一下。实际中可以刷新最近消息页面，增加小红点
      if (message instanceof AVIMTextMessage) {
        AVIMTextMessage textMessage = (AVIMTextMessage) message;
        Toast.makeText(context, "新消息 " +message.getFrom()+" : " + textMessage.getText(), Toast.LENGTH_SHORT).show();
      }
    }
  }

  public static AVIMTypedMessageHandler<AVIMTypedMessage> getActivityMessageHandler() {
    return activityMessageHandler;
  }

  public static void setActivityMessageHandler(AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler) {
    MessageHandler.activityMessageHandler = activityMessageHandler;
  }
}
