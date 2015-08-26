package com.leancloud.im.guide.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


/**
 * Created by wli on 15/8/25.
 */

public abstract class AVCommonViewHolder<T> extends RecyclerView.ViewHolder {

  public AVCommonViewHolder(Context context, ViewGroup root, int layoutRes) {
    super(LayoutInflater.from(context).inflate(layoutRes, root, false));
    findView();
  }

//  public AVCommonViewHolder(Context context, int layoutRes, T t) {
//    super(LayoutInflater.from(context).inflate(layoutRes, null));
//    findView();
//    bindData(t);
//  }

  public Context getContext() {
    return itemView.getContext();
  }

  public abstract void findView();

  public abstract void bindData(T t);

  public void setData(T t) {
    bindData(t);
  }
}