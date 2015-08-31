package com.leancloud.im.guide;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.activity.AVSingleChatActivity;
import com.leancloud.im.guide.activity.AVSquareActivity;
import com.leancloud.im.guide.event.ImTypeMessageEvent;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by zhangxiaobo on 15/4/20.
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

  private Context context;
  private String TAG = MessageHandler.this.getClass().getSimpleName();

  //TODO 此处代码仍有问题，这种实现方式不太合理
  public static String currentTopConversation = "";

  public MessageHandler(Context context) {
    this.context = context;
  }

  @Override
  public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
    Log.d(TAG, "消息已到达对方" + message.getContent());
  }

  @Override
  public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {

    String clientID = "";
    try {
      clientID = AVImClientManager.getInstance().getClientId();
    } catch (IllegalStateException e) {
      client.close(null);
      return;
    }

    if (client.getClientId().equals(clientID)) {

      // 自己发的消息不提示
      if (message.getFrom().equals(clientID)) {
        return;
      }
      ImTypeMessageEvent event = new ImTypeMessageEvent();
      event.message = message;
      event.conversation = conversation;
      EventBus.getDefault().post(event);

      String notificationContent = "";
      if (event.message instanceof AVIMTextMessage) {
        notificationContent = ((AVIMTextMessage)event.message).getText();
      } else {
        notificationContent = context.getString(R.string.unspport_message_type);
      }

      //TODO 这一坨代码还是太乱了
      if (!currentTopConversation.equals(conversation.getConversationId())) {
        if (Constants.SQUARE_CONVERSATION_ID.equals(conversation.getConversationId())) {
          Intent intent = new Intent();
          ComponentName cn = new ComponentName(context, AVSquareActivity.class);
          intent.setComponent(cn);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          NotificationUtils.showNotification(context, "", notificationContent, null, intent);
        } else {
          Intent intent = new Intent();
          ComponentName cn = new ComponentName(context, AVSingleChatActivity.class);
          intent.setComponent(cn);
          intent.putExtra(Constants.MEMBER_ID, message.getFrom());
          NotificationUtils.showNotification(context, "", notificationContent, null, intent);
        }
      }
    } else {
      client.close(null);
    }
  }
}
