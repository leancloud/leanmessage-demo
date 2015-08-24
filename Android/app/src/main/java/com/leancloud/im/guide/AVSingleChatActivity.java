package com.leancloud.im.guide;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.event.ImTypeMessageEvent;
import com.leancloud.im.guide.event.InputBottomBarTextEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wli on 15/8/14.
 */
public class AVSingleChatActivity extends AVEventBaseActivity {
  private AVIMConversation singleConversation;
  private MultipleItemAdapter itemAdapter;
  private RecyclerView recyclerView;
  private SwipeRefreshLayout refreshLayout;
  private Toolbar toolbar;

  private String memberId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square);

    memberId = getIntent().getStringExtra(Constants.MEMBER_ID);

    toolbar = (Toolbar) findViewById(R.id.toolbar);

    recyclerView = (RecyclerView) findViewById(R.id.activity_square_rv_chat);
    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_square_rv_srl_pullrefresh);
    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

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

    setTitle(memberId);

    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        AVIMMessage message = itemAdapter.getFirstMessage();
        if (null == message) {
          singleConversation.queryMessages(new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
              refreshLayout.setRefreshing(false);
              itemAdapter.addMessageList(list);
              itemAdapter.notifyDataSetChanged();
            }
          });
        } else {
          singleConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
              refreshLayout.setRefreshing(false);
              itemAdapter.addMessageList(list);
              itemAdapter.notifyDataSetChanged();
            }
          });
        }
      }
    });

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    itemAdapter = new MultipleItemAdapter(this);
    recyclerView.setAdapter(itemAdapter);

    getSquare();
  }

  private void getSquare() {
    final AVIMClient client = AVImClientManager.getInstance().getClient();
    AVIMConversationQuery conversationQuery = client.getQuery();
    conversationQuery.withMembers(Arrays.asList(memberId), true);
    conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> list, AVIMException e) {
        if (null != list && list.size() > 0) {
          singleConversation = list.get(0);
          fetchMessages();
        } else {
          client.createConversation(Arrays.asList(memberId), null, null, true, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVIMException e) {
              singleConversation = avimConversation;
              fetchMessages();
            }
          });
        }
      }
    });
  }

  /**
   * 拉取消息，必须加入 conversation 后才能拉取消息
   */
  private void fetchMessages() {
    singleConversation.queryMessages(new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> list, AVIMException e) {
        if (filterException(e)) {
          itemAdapter.setMessageList(list);
          itemAdapter.notifyDataSetChanged();
        }
      }
    });
  }

  public void onEvent(InputBottomBarTextEvent textEvent) {
    if (!TextUtils.isEmpty(textEvent.sendContent)) {
      AVIMTextMessage message = new AVIMTextMessage();
      message.setText(textEvent.sendContent);
      itemAdapter.addMessage(message);
      itemAdapter.notifyDataSetChanged();
      scrollToBottom();
      singleConversation.sendMessage(message, new AVIMConversationCallback() {
        @Override
        public void done(AVIMException e) {
          itemAdapter.notifyDataSetChanged();
        }
      });
    }
  }

  public void onEvent(ImTypeMessageEvent event) {
    if (null != singleConversation && null != event &&
      singleConversation.getConversationId().equals(event.conversation.getConversationId())) {
      itemAdapter.addMessage(event.message);
      itemAdapter.notifyDataSetChanged();
      scrollToBottom();
    }
  }

  private void scrollToBottom() {
    recyclerView.scrollToPosition(itemAdapter.getItemCount() - 1);
  }
}
