package com.leancloud.im.guide;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leancloud.im.guide.event.InputBottomBarEvent;
import com.leancloud.im.guide.event.InputBottomBarTextEvent;

import de.greenrobot.event.EventBus;


/**
 * Created by wli on 15/7/24.
 * 专门负责输入的底部操作栏，与 activity 解耦
 * 当点击相关按钮时发送 InputBottomBarEvent，需要的 View 可以自己去订阅相关消息
 */
public class AVInputBottomBar extends LinearLayout {

  /**
   * 最小间隔时间为 1 秒，避免多次点击
   */
  private final int MIN_INTERVAL_SEND_MESSAGE = 1000;

  /**
   * 发送文本的Button
   */
  private ImageButton sendTextBtn;

  private EditText contentView;

  public AVInputBottomBar(Context context) {
    super(context);
    initView(context);
  }

  public AVInputBottomBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  private void initView(final Context context) {
    View.inflate(context, R.layout.input_bottom_bar, this);

    sendTextBtn = (ImageButton) findViewById(R.id.input_bottom_bar_btn_send);
    contentView = (EditText) findViewById(R.id.input_bottom_bar_et_content);

    setEditTextChangeListener();

    sendTextBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String content = contentView.getText().toString();
        if (TextUtils.isEmpty(content)) {
          Toast.makeText(getContext(), R.string.message_is_null, Toast.LENGTH_SHORT).show();
          return;
        }

        contentView.setText("");
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            sendTextBtn.setEnabled(true);
          }
        }, MIN_INTERVAL_SEND_MESSAGE);

        EventBus.getDefault().post(
          new InputBottomBarTextEvent(InputBottomBarEvent.INPUTBOTTOMBAR_SEND_TEXT_ACTION, contentView.getText().toString()));
      }
    });
  }

  private void setEditTextChangeListener() {
    contentView.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
      }

      @Override
      public void afterTextChanged(Editable editable) {}
    });
  }
}
