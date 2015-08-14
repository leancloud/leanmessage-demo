package com.leancloud.im.guide;

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
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;

import java.util.List;

/**
 * Created by wli on 15/8/14.
 */
public class AVSingleChatActivity extends AVBaseActivity {
  private static final String SQUARE_CONVERSATION_ID = "551a2847e4b04d688d73dc54";
  private AVIMConversation squareConversation;
  private MultipleItemAdapter itemAdapter;
  private RecyclerView recyclerView;
  private SwipeRefreshLayout refreshLayout;
  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square);

    toolbar = (Toolbar) findViewById(R.id.toolbar);

    recyclerView = (RecyclerView) findViewById(R.id.activity_square_rv_chat);
    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_square_rv_srl_pullrefresh);
    setSupportActionBar(toolbar);

    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (R.id.menu_square_members == item.getItemId()) {
          startActivity(AVSquareMembersActivity.class);
          return true;
        }
        return false;
      }
    });

    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        AVIMMessage message = itemAdapter.getFirstMessage();
        squareConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
          @Override
          public void done(List<AVIMMessage> list, AVIMException e) {
            refreshLayout.setRefreshing(false);
            itemAdapter.addMessageList(list);
            itemAdapter.notifyDataSetChanged();
          }
        });

      }
    });

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    itemAdapter = new MultipleItemAdapter(this);

    getSquare(SQUARE_CONVERSATION_ID);
    fetchConversationInfo();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_square, menu);
    return true;
  }

  private void getSquare(String conversationId) {
    if (TextUtils.isEmpty(conversationId)) {
      throw new IllegalArgumentException("conversationId can not be null");
    }

    AVIMClient client = AVImClientManager.getInstance().getClient();
    squareConversation = client.getConversation(conversationId);
  }

  //TODO 失败后 retry
  private void fetchConversationInfo() {
    squareConversation.fetchInfoInBackground(new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        if (filterException(e)) {
          if (!squareConversation.getMembers().contains(AVImClientManager.getInstance().getClientId())) {
            joinSquare();
          } else {
            fetchMessages();
          }
        }
      }
    });
  }

  private void joinSquare() {
    squareConversation.join(new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        if (filterException(e)) {
          fetchMessages();
        }
      }
    });
  }

  /**
   * 拉取消息，必须加入 conversation 后才能拉取消息
   */
  private void fetchMessages() {
    squareConversation.queryMessages(new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> list, AVIMException e) {
        if (filterException(e)) {
          itemAdapter.setMessageList(list);
          recyclerView.setAdapter(itemAdapter);
          itemAdapter.notifyDataSetChanged();
        }
      }
    });
  }
}
