package com.leancloud.im.guide.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.AVInputBottomBar;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.adapter.MultipleItemAdapter;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.event.ImTypeMessageEvent;
import com.leancloud.im.guide.event.ImTypeMessageResendEvent;
import com.leancloud.im.guide.event.InputBottomBarTextEvent;
import com.leancloud.im.guide.fragment.ChatFragment;
import com.leancloud.im.guide.viewholder.LeftChatItemClickEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wli on 15/8/13.
 * 流程：1、根据 clientId 获得 AVIMClient 实例
 * 2、根据 conversationId 获得 AVIMConversation 实例
 * 3、必须要加入 conversation 后才能拉取消息
 */
public class AVSquareActivity extends AVEventBaseActivity {

  private AVIMConversation squareConversation;
  private ChatFragment chatFragment;
  private Toolbar toolbar;

  private static long lastBackTime = 0;
  private final int BACK_INTERVAL = 1000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square);

    String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
    String title = getIntent().getStringExtra(Constants.ACTIVITY_TITLE);

    chatFragment = (ChatFragment)getFragmentManager().findFragmentById(R.id.fragment_chat);
    toolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);

    setTitle(title);

//    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//      @Override
//      public boolean onMenuItemClick(MenuItem item) {
//        if (R.id.menu_square_members == item.getItemId()) {
//          startActivity(AVSquareMembersActivity.class);
//          return true;
//        }
//        return false;
//      }
//    });
    getSquare(conversationId);
    queryInSquare(conversationId);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_square, menu);
    return true;
  }

  @Override
  public void onBackPressed() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastBackTime < BACK_INTERVAL) {
      super.onBackPressed();
    } else {
      showToast("双击 back 退出");
    }
    lastBackTime = currentTime;
  }

  private void getSquare(String conversationId) {
    if (TextUtils.isEmpty(conversationId)) {
      throw new IllegalArgumentException("conversationId can not be null");
    }

    AVIMClient client = AVImClientManager.getInstance().getClient();
    squareConversation = client.getConversation(conversationId);
  }

  private void joinSquare() {
    squareConversation.join(new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        if (filterException(e)) {
          chatFragment.setConversation(squareConversation);
        }
      }
    });
  }

  private void queryInSquare(String conversationId) {
    final AVIMClient client = AVImClientManager.getInstance().getClient();
    AVIMConversationQuery conversationQuery = client.getQuery();
    conversationQuery.whereEqualTo("objectId", conversationId);
    conversationQuery.containsMembers(Arrays.asList(AVImClientManager.getInstance().getClientId()));
    conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> list, AVIMException e) {
        if (null != list && list.size() > 0) {
          chatFragment.setConversation(list.get(0));
        } else {
          joinSquare();
        }
      }
    });
  }

  public void onEvent(LeftChatItemClickEvent event) {
    Intent intent = new Intent(this, AVSingleChatActivity.class);
    intent.putExtra(Constants.MEMBER_ID, event.userId);
    startActivity(intent);
  }
}
