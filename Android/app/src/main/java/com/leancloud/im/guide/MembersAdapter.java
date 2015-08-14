package com.leancloud.im.guide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leancloud.im.guide.viewholder.MemberHolder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wli on 15/8/14.
 */
public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final LayoutInflater mLayoutInflater;
  private final Context mContext;
  private List<String> memberList = new LinkedList<String>();
  private AdapterItemClick itemClick;

  public MembersAdapter(Context context) {
    mContext = context;
    mLayoutInflater = LayoutInflater.from(context);
  }

  public void setMemberList(List<String> memberList) {
    this.memberList = memberList;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new MemberHolder(mLayoutInflater.inflate(R.layout.chat_left_text_view, parent, false));
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
      ((MemberHolder) holder).mTextView.setText(memberList.get(position));
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != itemClick) {
          itemClick.onItemClick(memberList.get(position));
        }
      }
    });
  }

  @Override
  public int getItemViewType(int position) {
    return 1;
  }

  @Override
  public int getItemCount() {
    return memberList.size();
  }

  public void setOnItemClick(AdapterItemClick itemClick) {
    this.itemClick = itemClick;
  }

  public static interface AdapterItemClick {
    public void onItemClick(String str);
  }
}