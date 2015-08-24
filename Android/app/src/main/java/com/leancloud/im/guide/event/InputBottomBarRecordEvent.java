package com.leancloud.im.guide.event;

/**
 * Created by wli on 15/7/29.
 * InputBottomBar 录音事件，录音完成时触发
 */
public class InputBottomBarRecordEvent extends InputBottomBarEvent {

  /**
   * 录音本地路径
   */
  public String audioPath;

  /**
   * 录音长度
   */
  public int audioDuration;

  public InputBottomBarRecordEvent(int action, String path, int duration) {
    super(action);
    audioDuration = duration;
    audioPath = path;
  }
}
