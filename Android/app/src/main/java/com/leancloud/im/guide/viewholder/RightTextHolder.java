package com.leancloud.im.guide.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.leancloud.im.guide.R;

/**
 * Created by wli on 15/8/13.
 * * 聊天时居右的文本 holder
 */
public class RightTextHolder extends RecyclerView.ViewHolder {

  public TextView mTextView;

  public RightTextHolder(View itemView) {
    super(itemView);
    mTextView = (TextView) itemView.findViewById(R.id.chat_right_text_tv_content);
  }
}
