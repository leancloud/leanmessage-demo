package com.leancloud.im.guide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.leancloud.im.guide.viewholder.MemberHolder;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by wli on 15/8/14.
 */
public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final Context mContext;
  private List<String> memberList = new ArrayList<String>();

  public MembersAdapter(Context context) {
    mContext = context;
  }

  public void setMemberList(List<String> list) {
    memberList.clear();
    memberList.addAll(list);
    Collections.sort(memberList, new SortChineseName());
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new MemberHolder(mContext, parent, false);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
      ((MemberHolder) holder).bindData(memberList.get(position));
  }

  @Override
  public int getItemViewType(int position) {
    return 1;
  }

  @Override
  public int getItemCount() {
    return memberList.size();
  }

  public class SortChineseName implements Comparator<String> {
    Collator cmp = Collator.getInstance(Locale.SIMPLIFIED_CHINESE);

    @Override
    public int compare(String str1, String str2) {

      if (null == str1) {
        return -1;
      }
      if (null == str2) {
        return 1;
      }
      if (cmp.compare(str1, str2)>0){
        return 1;
      }else if (cmp.compare(str1, str2)<0){
        return -1;
      }
      return 0;
    }
  }
}