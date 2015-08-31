package com.leancloud.im.guide.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.adapter.MembersAdapter;
import com.leancloud.im.guide.event.ImTypeMessageResendEvent;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/8/13.
 * * 聊天时居右的文本 holder
 */
public class RightTextHolder extends AVCommonViewHolder {

  @Bind(R.id.chat_right_text_tv_time)
  protected TextView timeView;

  @Bind(R.id.chat_right_text_tv_content)
  protected TextView contentView;

  @Bind(R.id.chat_right_text_tv_name)
  protected TextView nameView;

  @Bind(R.id.chat_right_text_layout_status)
  protected FrameLayout statusView;

  @Bind(R.id.chat_right_text_progressbar)
  protected ProgressBar loadingBar;

  @Bind(R.id.chat_right_text_tv_error)
  protected ImageView errorView;

  private AVIMMessage message;

  public RightTextHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.chat_right_text_view);
  }

  @OnClick(R.id.chat_right_text_tv_error)
  public void onErrorClick(View view) {
    ImTypeMessageResendEvent event = new ImTypeMessageResendEvent();
    event.message = message;
    EventBus.getDefault().post(event);
  }

  @Override
  public void bindData(Object o) {
    message = (AVIMMessage)o;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String time = dateFormat.format(message.getTimestamp());

    String content = getContext().getString(R.string.unspport_message_type);;
    if (message instanceof AVIMTextMessage) {
      content = ((AVIMTextMessage)message).getText();
    }

    contentView.setText(content);
    timeView.setText(time);
    nameView.setText(message.getFrom());

    if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == message.getMessageStatus()) {
      errorView.setVisibility(View.VISIBLE);
      loadingBar.setVisibility(View.GONE);
      statusView.setVisibility(View.VISIBLE);
    } else if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSending == message.getMessageStatus()) {
      errorView.setVisibility(View.GONE);
      loadingBar.setVisibility(View.VISIBLE);
      statusView.setVisibility(View.VISIBLE);
    } else {
      statusView.setVisibility(View.GONE);
    }
  }

  public void showTimeView(boolean isShow) {
    timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}
