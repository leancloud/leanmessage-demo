package com.leancloud.im.guide.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.LetterView;
import com.leancloud.im.guide.adapter.MembersAdapter;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.event.MemberLetterEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by wli on 15/8/14.
 * 在线成员列表
 * 当前版本因为暂态回话不能查询成员而导致此页面的入口被注释掉
 */
public class AVSquareMembersActivity extends AVEventBaseActivity {

  @Bind(R.id.toolbar)
  protected Toolbar toolbar;

  @Bind(R.id.activity_square_members_srl_list)
  protected SwipeRefreshLayout refreshLayout;

  @Bind(R.id.activity_square_members_letterview)
  protected LetterView letterView;

  @Bind(R.id.activity_square_members_rv_list)
  protected RecyclerView recyclerView;

  private SearchView searchView;

  private MembersAdapter itemAdapter;
  private AVIMConversation conversation;
  LinearLayoutManager layoutManager;
  private List<String> memberList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square_members);

    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    setTitle(R.string.square_member_title);

    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    itemAdapter = new MembersAdapter();
    recyclerView.setAdapter(itemAdapter);

    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        conversation.fetchInfoInBackground(new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            getMembers();
            refreshLayout.setRefreshing(false);
          }
        });
      }
    });
    getMembers();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_member_menu, menu);

    searchView = (SearchView) menu.findItem(R.id.activity_member_menu_search).getActionView();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        itemAdapter.setMemberList(filterMembers(newText));
        itemAdapter.notifyDataSetChanged();
        return false;
      }
    });
    return true;
  }

  /**
   * 在 memberList 里匹配搜索结果
   */
  private List<String> filterMembers(String content) {
    List<String> members = new ArrayList<String>();
    for (String name : memberList) {
      if (name.contains(content)) {
        members.add(name);
      }
    }
    return members;
  }

  /**
   * 从 AVIMConversation 获取 member，如果本地没有则做拉取请求，然后更新 UI
   */
  private void getMembers() {
    conversation = AVImClientManager.getInstance().getClient().getConversation(Constants.SQUARE_CONVERSATION_ID);
    memberList = conversation.getMembers();
    if (null != memberList && memberList.size() > 0) {
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

  /**
   * 处理 LetterView 发送过来的 MemberLetterEvent
   * 会通过 MembersAdapter 获取应该要跳转到的位置，然后跳转
   */
  public void onEvent(MemberLetterEvent event) {
    Character targetChar = Character.toLowerCase(event.letter);
    if (itemAdapter.getIndexMap().containsKey(targetChar)) {
      int index = itemAdapter.getIndexMap().get(targetChar);
      if (index > 0 && index < itemAdapter.getItemCount()) {
        layoutManager.scrollToPositionWithOffset(index, 0);
      }
    }
  }
}
