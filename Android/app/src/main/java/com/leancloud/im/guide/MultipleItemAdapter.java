package com.leancloud.im.guide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.viewholder.LeftTextHolder;
import com.leancloud.im.guide.viewholder.RightTextHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wli on 15/8/13.
 */
public class MultipleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final int ITEM_LEFT_TEXT = 0;
  private final int ITEM_RIGHT_TEXT = 1;

  private final LayoutInflater mLayoutInflater;
  private final Context mContext;
  private List<AVIMMessage> messageList = new ArrayList<AVIMMessage>();

  public MultipleItemAdapter(Context context) {
    mContext = context;
    mLayoutInflater = LayoutInflater.from(context);
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
      return new LeftTextHolder(mLayoutInflater.inflate(R.layout.chat_left_text_view, parent, false));
    } else if (viewType == ITEM_RIGHT_TEXT) {
      return new RightTextHolder(mLayoutInflater.inflate(R.layout.chat_right_text_view, parent, false));
    } else {
      //TODO
      return null;
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    AVIMMessage message = messageList.get(position);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm 1969-12-31 16:00");
    String time = dateFormat.format(message.getTimestamp());

    String content = "暂不支持此消息类型";
    if (message instanceof AVIMTextMessage) {
      content = ((AVIMTextMessage)message).getText() + "   " + message.getMessageStatus();
    }

    if (holder instanceof LeftTextHolder) {
      ((LeftTextHolder) holder).contentView.setText(content);
      ((LeftTextHolder) holder).timeView.setText(message.getTimestamp() + "");
      ((LeftTextHolder) holder).nameView.setText(message.getFrom());
    } else if (holder instanceof RightTextHolder) {
      ((RightTextHolder) holder).contentView.setText(content);
      ((RightTextHolder) holder).timeView.setText(message.getTimestamp() + "");
      ((RightTextHolder) holder).nameView.setText(message.getFrom());
    }
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