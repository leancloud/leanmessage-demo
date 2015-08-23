package com.leancloud.im.guide;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.event.ImTypeMessageEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by zhangxiaobo on 15/4/20.
 */
class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

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
    if (client.getClientId().equals(AVImClientManager.getInstance().getClientId())) {
      ImTypeMessageEvent event = new ImTypeMessageEvent();
      event.message = message;
      event.conversation = conversation;
      EventBus.getDefault().post(event);
    } else {
      client.close(null);
    }
  }
}
