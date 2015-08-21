//
//  RightTextMessageTableViewCell.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/21/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "RightTextMessageTableViewCell.h"

@implementation RightTextMessageTableViewCell
-(void)setTextMessage:(AVIMTextMessage *)textMessage{
    [super setTextMessage:textMessage ];
    
    // 显示该条消息的发送者的 ClientId，并且为 Label 控件设置可点击的事件。
    self.clientIdLabel.text=self.textMessage.clientId;
    // 显示文本消息的内容
    self.messageContentTextView.text=textMessage.text;
}
- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
    // 设置聊天内容背景图
    UIImage *bgmImage = [UIImage imageNamed:@"bg_2"];
    bgmImage = [bgmImage resizableImageWithCapInsets:UIEdgeInsetsMake(0, 0, 0, 20)];
    [self.messageContentBackgroudImage setImage:bgmImage];
    
    self.clientIdLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clientIdLabelTapped:)];
    [self.clientIdLabel addGestureRecognizer:tapGesture];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
