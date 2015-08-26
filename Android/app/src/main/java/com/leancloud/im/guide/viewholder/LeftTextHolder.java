package com.leancloud.im.guide.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.R;

import java.text.SimpleDateFormat;

/**
 * Created by wli on 15/8/13.
 * 聊天时居左的文本 holder
 */

public class LeftTextHolder extends AVCommonViewHolder {

  private TextView timeView;
  private TextView contentView;
  private TextView nameView;

  public LeftTextHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.chat_left_text_view);
  }

  @Override
  public void findView() {
    timeView = (TextView) itemView.findViewById(R.id.chat_left_text_tv_time);
    nameView = (TextView) itemView.findViewById(R.id.chat_left_text_tv_name);
    contentView = (TextView) itemView.findViewById(R.id.chat_left_text_tv_content);
  }

  @Override
  public void bindData(Object o) {
    AVIMMessage message = (AVIMMessage)o;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String time = dateFormat.format(message.getTimestamp());

    String content =  getContext().getString(R.string.unspport_message_type);
    if (message instanceof AVIMTextMessage) {
      content = ((AVIMTextMessage)message).getText();
    }

    contentView.setText(content);
    timeView.setText(time);
    nameView.setText(message.getFrom());
  }
}