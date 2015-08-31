package com.leancloud.im.guide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.leancloud.im.guide.event.MemberLetterEvent;

import java.util.ArrayList;
import java.util.List;
import de.greenrobot.event.EventBus;


/**
 * Created by wli on 15/8/24.
 * 联系人列表，快速滑动字母导航 View
 * 此处仅在滑动或点击时发送 MemberLetterEvent，接收放自己处理相关逻辑
 * 注意：因为长按事件等触发，有可能重复发送
 */
public class LetterView extends LinearLayout {

  public LetterView(Context context) {
    super(context);
    setOrientation(VERTICAL);
    updateLetters();
  }

  public LetterView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOrientation(VERTICAL);
    updateLetters();
  }

  private void updateLetters() {
    setLetters(getSortLetters());
  }

  /**
   * 设置快速滑动的字母集合
   */
  public void setLetters(List<Character> letters) {
    removeAllViews();
    for(Character content : letters) {
      TextView view = new TextView(getContext());
      view.setText(content.toString());
      addView(view);
    }

    setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());
        for (int i = 0; i < getChildCount(); i++) {
          TextView child = (TextView) getChildAt(i);
          if (y > child.getTop() && y < child.getBottom()) {
            MemberLetterEvent letterEvent = new MemberLetterEvent();
            letterEvent.letter = child.getText().toString().charAt(0);
            EventBus.getDefault().post(letterEvent);
          }
        }
        return true;
      }
    });
  }

  /**
   * 默认的只包含 A-Z 的字母
   */
  private List<Character> getSortLetters() {
    List<Character> letterList = new ArrayList<Character>();
    for (char c = 'A'; c <= 'Z'; c++) {
      letterList.add(c);
    }
    return letterList;
  }
}
