package com.leancloud.im.guide;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;

import java.util.List;

/**
 * Created by wli on 15/8/14.
 */
public class AVSquareMembersActivity extends AVBaseActivity {

  private MembersAdapter itemAdapter;
  private RecyclerView recyclerView;
  private SwipeRefreshLayout refreshLayout;
  private AVIMConversation conversation;
  private List<String> memberList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square_members);

    recyclerView = (RecyclerView) findViewById(R.id.activity_square_members_rv_list);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    itemAdapter = new MembersAdapter(this);

    itemAdapter.setOnItemClick(new MembersAdapter.AdapterItemClick() {
      @Override
      public void onItemClick(String str) {
        startActivity(AVSingleChatActivity.class);
      }
    });
    recyclerView.setAdapter(itemAdapter);
    getMembers();
  }

  private void getMembers() {
    conversation = AVImClientManager.getInstance().getClient().getConversation("551a2847e4b04d688d73dc54");
    memberList = conversation.getMembers();
    if (null != memberList) {
      itemAdapter.setMemberList(memberList);
      itemAdapter.notifyDataSetChanged();
    } else {
      conversation.fetchInfoInBackground(new AVIMConversationCallback() {
        @Override
        public void done(AVIMException e) {
          memberList = conversation.getMembers();
          itemAdapter.setMemberList(memberList);
          itemAdapter.notifyDataSetChanged();
        }
      });
    }
  }
}
