//
//  DemoTextMessageTableViewCell.m
//  LeanMessageDemo
//
//  Created by LeanCloud on 8/19/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "LeftTextMessageTableViewCell.h"

@implementation LeftTextMessageTableViewCell

-(void)setTextMessage:(AVIMTextMessage *)textMessage{
    [super setTextMessage:textMessage ];

    // 显示该条消息的发送者的 ClientId，并且为 Label 控件设置可点击的事件。
    self.clientIdLabel.text=self.textMessage.clientId;
    // 显示文本消息的内容
    self.messageContentTextView.text=textMessage.text;
    NSString* text = textMessage.text;
    CGSize size = [text sizeWithAttributes:@{NSFontAttributeName: [UIFont systemFontOfSize:14.0f]}];
    
    CGSize adjustedSize = CGSizeMake(ceilf(size.width), ceilf(size.height));
    [self layoutIfNeeded];
}

- (void)awakeFromNib {
    [super awakeFromNib];
    
    // 设置聊天内容背景图
    UIImage *bgmImage = [UIImage imageNamed:@"bg_1"];
    bgmImage = [bgmImage resizableImageWithCapInsets:UIEdgeInsetsMake(10, 10, 10, 10)];
    [self.imageView setImage:bgmImage];
    
    self.clientIdLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clientIdLabelTapped:)];
    [self.clientIdLabel addGestureRecognizer:tapGesture];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
