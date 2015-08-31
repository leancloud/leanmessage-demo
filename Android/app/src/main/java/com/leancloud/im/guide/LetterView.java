package com.leancloud.im.guide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.leancloud.im.guide.event.MemberLetterEvent;
import java.util.List;
import de.greenrobot.event.EventBus;


/**
 * Created by wli on 15/8/24.
 * 联系人列表，字母导航 View
 */
public class LetterView extends LinearLayout {

  public LetterView(Context context) {
    super(context);
    setOrientation(VERTICAL);
  }

  public LetterView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOrientation(VERTICAL);
  }

  public void setLetters(List<Character> letters) {
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
}
