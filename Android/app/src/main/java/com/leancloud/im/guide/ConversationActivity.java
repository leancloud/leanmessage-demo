package com.leancloud.im.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import java.util.*;

/**
 * Created by zhangxiaobo on 15/4/16.
 */
public class ConversationActivity extends ActionBarActivity implements View.OnClickListener {
  // 这是使用中国节点时使用的 对话 id。如果不使用美国节点，请 uncomment 这一行。
  public static final String CONVERSATION_ID = "551a2847e4b04d688d73dc54";
  private static final String TAG = ConversationActivity.class.getSimpleName();
  // 这是使用美国节点时使用的 对话 id。如果不使用美国节点，请 comment 这一行。
//  public static final String CONVERSATION_ID =   "55489bd9e4b065597b2061d6";

  private TextView clientIdTextView;
  private EditText otherIdEditText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conversation);

    // init
    clientIdTextView = (TextView) findViewById(R.id.client_id);
    otherIdEditText = (EditText) findViewById(R.id.otherIdEditText);

    clientIdTextView.setText(getString(R.string.welcome) + " "+Application.getClientIdFromPre());

    findViewById(R.id.join_conversation).setOnClickListener(this);

    findViewById(R.id.logout).setOnClickListener(this);
    findViewById(R.id.chat_with_other).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.logout:
        Application.setClientIdToPre("");
        finish();
        break;
      case R.id.join_conversation:
        ChatActivity.startActivity(ConversationActivity.this,
            CONVERSATION_ID);
        break;
      case R.id.chat_with_other:
        String otherId = otherIdEditText.getText().toString();
        if (!TextUtils.isEmpty(otherId)) {
          fetchConversationWithClientIds(Arrays.asList(otherId), ConversationType.OneToOne, new
              AVIMConversationCreatedCallback
                  () {
                @Override
                public void done(AVIMConversation conversation, AVException e) {
                  if (e != null) {
                    Toast.makeText(ConversationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                  } else {
                    ChatActivity.startActivity(ConversationActivity.this, conversation.getConversationId());
                  }
                }
              });
        }
        break;
    }
  }

  private void fetchConversationWithClientIds(List<String> clientIds, final ConversationType type, final
  AVIMConversationCreatedCallback
      callback) {
    final AVIMClient imClient = Application.getIMClient();
    final List<String> queryClientIds = new ArrayList<>();
    queryClientIds.addAll(clientIds);
    if (!clientIds.contains(imClient.getClientId())) {
      queryClientIds.add(imClient.getClientId());
    }
    AVIMConversationQuery query = imClient.getQuery();
    query.whereEqualTo(Conversation.ATTRIBUTE_MORE + ".type", type.getValue());
    query.whereContainsAll(Conversation.COLUMN_MEMBERS, queryClientIds);
    query.findInBackground(new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> list, AVException e) {
        if (e != null) {
          callback.done(null, e);
        } else {
          if (list == null || list.size() == 0) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(ConversationType.KEY_ATTRIBUTE_TYPE, type.getValue());
            imClient.createConversation(queryClientIds, attributes, callback);
          } else {
            callback.done(list.get(0), null);
          }
        }
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Application.getIMClient().close(new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVException e) {
        if (e == null) {
          Log.d(TAG, "退出连接");
        } else {
          Toast.makeText(ConversationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
  }
}
