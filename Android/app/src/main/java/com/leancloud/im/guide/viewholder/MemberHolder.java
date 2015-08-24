package com.leancloud.im.guide.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leancloud.im.guide.activity.AVSingleChatActivity;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;

/**
 * Created by wli on 15/8/14.
 */
public class MemberHolder extends RecyclerView.ViewHolder {

  public TextView mTextView;

  public MemberHolder(Context context, ViewGroup root, boolean attachToRoot) {
    super(LayoutInflater.from(context).inflate(R.layout.activity_member_item, root, attachToRoot));
    mTextView = (TextView) itemView.findViewById(R.id.member_item_name);
  }

  public void bindData(final String str) {
    mTextView.setText(str);
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Activity host = (Activity) itemView.getContext();
        Intent intent = new Intent(host, AVSingleChatActivity.class);
        intent.putExtra(Constants.MEMBER_ID, str);
        host.startActivity(intent);
      }
    });
  }
}