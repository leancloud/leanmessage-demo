package com.leancloud.im.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangxiaobo on 15/4/16.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {
  private static final String EXTRA_CONVERSATION_ID = "conversation_id";
  private static final String TAG = ChatActivity.class.getSimpleName();
  static final int PAGE_SIZE = 10;

  private AVIMConversation conversation;
  MessageAdapter adapter;

  private EditText messageEditText;
  private ListView listView;
  private ChatHandler handler;

  private AtomicBoolean isLoadingMessages = new AtomicBoolean(false);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    // init component
    listView = (ListView) findViewById(R.id.listview);
    messageEditText = (EditText) findViewById(R.id.message);
    adapter = new MessageAdapter(ChatActivity.this, Application.getClientIdFromPre());
    listView.setOnScrollListener(this);
    listView.setAdapter(adapter);

    // get argument
    final String conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
    Log.d(TAG, "会话 id: " + conversationId);

    // register callback
    handler = new ChatHandler(adapter);

    conversation = Application.getIMClient().getConversation(conversationId);

    findViewById(R.id.send).setOnClickListener(this);

    loadMessagesWhenInit();
  }

  @Override
  protected void onResume() {
    super.onResume();
    MessageHandler.setActivityMessageHandler(handler);
  }

  @Override
  protected void onPause() {
    super.onPause();
    MessageHandler.setActivityMessageHandler(null);
  }

  private List<AVIMTypedMessage> filterMessages(List<AVIMMessage> messages) {
    List<AVIMTypedMessage> typedMessages = new ArrayList<AVIMTypedMessage>();
    for (AVIMMessage message : messages) {
      if (message instanceof AVIMTypedMessage) {
        typedMessages.add((AVIMTypedMessage) message);
      }
    }
    return typedMessages;
  }

  private void loadMessagesWhenInit() {
    if (isLoadingMessages.get()) {
      return;
    }
    isLoadingMessages.set(true);
    conversation.queryMessages(PAGE_SIZE, new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> list, AVIMException e) {
        if (filterException(e)) {
          List<AVIMTypedMessage> typedMessages = filterMessages(list);
          adapter.setMessageList(typedMessages);
          adapter.notifyDataSetChanged();
          scrollToLast();
        }
        isLoadingMessages.set(false);
      }
    });
  }

  private void loadOldMessages() {
    if (isLoadingMessages.get() || adapter.getMessageList().size() < PAGE_SIZE) {
      return;
    } else {
      isLoadingMessages.set(true);
      AVIMTypedMessage firstMsg = adapter.getMessageList().get(0);
      long time = firstMsg.getTimestamp();
      conversation.queryMessages(null, time, PAGE_SIZE, new AVIMMessagesQueryCallback() {
        @Override
        public void done(List<AVIMMessage> list, AVIMException e) {
          if (filterException(e)) {
            List<AVIMTypedMessage> typedMessages = filterMessages(list);
            if (typedMessages.size() == 0) {
              toast("无更早的消息了");
            } else {
              List<AVIMTypedMessage> newMessages = new ArrayList<AVIMTypedMessage>();
              newMessages.addAll(typedMessages);
              newMessages.addAll(adapter.getMessageList());
              adapter.setMessageList(newMessages);
              adapter.notifyDataSetChanged();
              listView.setSelection(typedMessages.size() - 1);
            }
          }
          isLoadingMessages.set(false);
        }
      });
    }
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
      public void done(AVIMException e) {
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

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE) {
      if (view.getChildCount() > 0) {
        View first = view.getChildAt(0);
        if (first != null && view.getFirstVisiblePosition() == 0 && first.getTop() == 0) {
          loadOldMessages();
        }
      }
    }
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

  }

  public class ChatHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
    private MessageAdapter adapter;

    public ChatHandler(MessageAdapter adapter) {
      this.adapter = adapter;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
      if (!(message instanceof AVIMTextMessage)) {
        return;
      }
      if (conversation.getConversationId().equals(ChatActivity.this.conversation.getConversationId())) {
        adapter.addMessage((AVIMTextMessage) message);
        scrollToLast();
      }
    }
  }
}
