package com.leancloud.im.guide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.viewholder.AVCommonViewHolder;
import com.leancloud.im.guide.viewholder.LeftTextHolder;
import com.leancloud.im.guide.viewholder.RightTextHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wli on 15/8/13.
 */
public class MultipleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final int ITEM_LEFT_TEXT = 0;
  private final int ITEM_RIGHT_TEXT = 1;

  private List<AVIMMessage> messageList = new ArrayList<AVIMMessage>();

  public MultipleItemAdapter(Context context) {
  }

  public void setMessageList(List<AVIMMessage> messages) {
    messageList.clear();
    if (null != messages) {
      messageList.addAll(messages);
    }
  }

  public void addMessageList(List<AVIMMessage> messages) {
    messageList.addAll(0, messages);
  }

  public void addMessage(AVIMMessage message) {
    messageList.addAll(Arrays.asList(message));
  }

  public AVIMMessage getFirstMessage() {
    if (null != messageList && messageList.size() > 0) {
      return messageList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == ITEM_LEFT_TEXT) {
      return new LeftTextHolder(parent.getContext(), parent);
    } else if (viewType == ITEM_RIGHT_TEXT) {
      return new RightTextHolder(parent.getContext(), parent);
    } else {
      //TODO
      return null;
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((AVCommonViewHolder)holder).bindData(messageList.get(position));
  }

  @Override
  public int getItemViewType(int position) {
    AVIMMessage message = messageList.get(position);
    if (message.getFrom().equals(AVImClientManager.getInstance().getClientId())) {
      return ITEM_RIGHT_TEXT;
    } else {
      return ITEM_LEFT_TEXT;
    }
  }

  @Override
  public int getItemCount() {
    return messageList.size();
  }
}