package com.leancloud.im.guide.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.AVInputBottomBar;
import com.leancloud.im.guide.NotificationUtils;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.adapter.MultipleItemAdapter;
import com.leancloud.im.guide.event.ImTypeMessageEvent;
import com.leancloud.im.guide.event.ImTypeMessageResendEvent;
import com.leancloud.im.guide.event.InputBottomBarTextEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/8/27.
 * 将聊天相关的封装到此 Fragment 里边，只需要通过 setConversation 传入 Conversation 即可
 */
public class ChatFragment extends Fragment {
  protected AVIMConversation imConversation;

  protected MultipleItemAdapter itemAdapter;
  protected RecyclerView recyclerView;
  protected LinearLayoutManager layoutManager;
  protected SwipeRefreshLayout refreshLayout;
  protected AVInputBottomBar inputBottomBar;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, container, false);

    recyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_rv_chat);
    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_chat_srl_pullrefresh);
    refreshLayout.setEnabled(false);
    inputBottomBar = (AVInputBottomBar) view.findViewById(R.id.fragment_chat_inputbottombar);
    layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);
    itemAdapter = new MultipleItemAdapter();
    recyclerView.setAdapter(itemAdapter);

    EventBus.getDefault().register(this);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        AVIMMessage message = itemAdapter.getFirstMessage();
        if (null != imConversation) {
          imConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
              refreshLayout.setRefreshing(false);
              if (filterException(e)) {
                if (null != list && list.size() > 0) {
                  itemAdapter.addMessageList(list);
                  itemAdapter.notifyDataSetChanged();

                  layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                }
              }
            }
          });
        } else {
          refreshLayout.setRefreshing(false);
        }
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    if (null != imConversation) {
      NotificationUtils.addTag(imConversation.getConversationId());
    }
  }

  @Override
  public void onPause() {
    super.onResume();
    NotificationUtils.removeTag(imConversation.getConversationId());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
  }

  public void setConversation(AVIMConversation conversation) {
    if (null != conversation) {
      imConversation = conversation;
      refreshLayout.setEnabled(true);
      inputBottomBar.setTag(imConversation.getConversationId());
      fetchMessages();
      NotificationUtils.addTag(conversation.getConversationId());
    }
  }



  /**
   * 拉取消息，必须加入 conversation 后才能拉取消息
   */
  private void fetchMessages() {
    if (null != imConversation) {
      imConversation.queryMessages(new AVIMMessagesQueryCallback() {
        @Override
        public void done(List<AVIMMessage> list, AVIMException e) {
          if (filterException(e)) {
            itemAdapter.setMessageList(list);
            recyclerView.setAdapter(itemAdapter);
            itemAdapter.notifyDataSetChanged();
            scrollToBottom();
          }
        }
      });
    }
  }

  /**
   * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
   * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
   */
  public void onEvent(InputBottomBarTextEvent textEvent) {
    if (null != imConversation && null != textEvent) {
      if (!TextUtils.isEmpty(textEvent.sendContent) && imConversation.getConversationId().equals(textEvent.tag)) {
        AVIMTextMessage message = new AVIMTextMessage();
        message.setText(textEvent.sendContent);
        itemAdapter.addMessage(message);
        itemAdapter.notifyDataSetChanged();
        scrollToBottom();
        imConversation.sendMessage(message, new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            itemAdapter.notifyDataSetChanged();
          }
        });
      }
    }
  }

  /**
   * 处理推送过来的消息
   * 同理，避免无效消息，此处加了 conversation id 判断
   */
  public void onEvent(ImTypeMessageEvent event) {
    if (null != imConversation && null != event &&
      imConversation.getConversationId().equals(event.conversation.getConversationId())) {
      itemAdapter.addMessage(event.message);
      itemAdapter.notifyDataSetChanged();
      scrollToBottom();
    }
  }

  /**
   * 重新发送已经发送失败的消息
   */
  public void onEvent(ImTypeMessageResendEvent event) {
    if (null != imConversation && null != event) {
      if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == event.message.getMessageStatus()
        && imConversation.getConversationId().equals(event.message.getConversationId())) {
        imConversation.sendMessage(event.message, new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            itemAdapter.notifyDataSetChanged();
          }
        });
        itemAdapter.notifyDataSetChanged();
      }
    }
  }

  private void scrollToBottom() {
    layoutManager.scrollToPositionWithOffset(itemAdapter.getItemCount() - 1, 0);
  }

  protected boolean filterException(Exception e) {
    if (e != null) {
      e.printStackTrace();
      toast(e.getMessage());
      return false;
    } else {
      return true;
    }
  }

  protected void toast(String str) {
    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
  }
}
