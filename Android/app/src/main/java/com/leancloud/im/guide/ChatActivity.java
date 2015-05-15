package com.leancloud.im.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.*;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaobo on 15/4/16.
 */
public class ChatActivity extends ActionBarActivity implements View.OnClickListener {
  private static final String EXTRA_CONVERSATION_ID = "conversation_id";
  private static final String TAG = ChatActivity.class.getSimpleName();

  private AVIMConversation conversation;
  MessageAdapter adapter;

  private EditText messageEditText;
  private ListView listView;
  private ChatHandler handler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    // init component
    listView = (ListView) findViewById(R.id.listview);
    messageEditText = (EditText) findViewById(R.id.message);
    adapter = new MessageAdapter(ChatActivity.this, Application.getClientIdFromPre());
    listView.setAdapter(adapter);

    // get argument
    final String conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
    Log.d(TAG, "会话 id: " + conversationId);

    // register callback
    handler = new ChatHandler(adapter);
    AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, handler);

    conversation = Application.getIMClient().getConversation(conversationId);

    findViewById(R.id.send).setOnClickListener(this);

    loadMessagesWhenInit();
  }

  void loadMessagesWhenInit() {
    conversation.queryMessages(100, new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> list, AVException e) {
        if (filterException(e)) {
          List<AVIMTypedMessage> typedMessages = new ArrayList<AVIMTypedMessage>();
          for (AVIMMessage message : list) {
            if (message instanceof AVIMTypedMessage) {
              typedMessages.add((AVIMTypedMessage) message);
            }
          }
          adapter.setMessageList(typedMessages);
          adapter.notifyDataSetChanged();
          scrollToLast();
        }
      }
    });
  }

  private boolean filterException(Exception e) {
    if (e != null) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
      return false;
    }
    return true;
  }

  private void scrollToLast() {
    listView.smoothScrollToPosition(listView.getCount() - 1);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    AVIMMessageManager.unregisterMessageHandler(AVIMTypedMessage.class, handler);
  }

  public static void startActivity(Context context, String conversationId) {
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
    context.startActivity(intent);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.send:
        sendText();
        break;
    }
  }

  public void sendText() {
    final AVIMTextMessage message = new AVIMTextMessage();
    message.setText(messageEditText.getText().toString());
    conversation.sendMessage(message, new AVIMConversationCallback() {
      @Override
      public void done(AVException e) {
        if (null != e) {
          e.printStackTrace();
        } else {
          adapter.addMessage(message);
          finishSend();
        }
      }
    });
  }

  public void finishSend() {
    messageEditText.setText(null);
    scrollToLast();
  }

  public class ChatHandler extends AVIMTypedMessageHandler<AVIMTextMessage> {
    private MessageAdapter adapter;

    public ChatHandler(MessageAdapter adapter) {
      this.adapter = adapter;
    }

    @Override
    public void onMessage(AVIMTextMessage message, AVIMConversation conversation,
                          AVIMClient client) {
      if (client.getClientId().equals(Application.getClientIdFromPre())) {
        if (conversation.getConversationId().equals(ChatActivity.this.conversation.getConversationId())) {
          adapter.addMessage(message);
        }
      } else {
        client.close(null);
      }
    }
  }
}
