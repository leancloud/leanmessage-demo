//
//  DemoTextMessageTableViewCell.m
//  LeanMessageDemo
//
//  Created by LeanCloud on 8/19/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "LeftTextMessageTableViewCell.h"

@interface LeftTextMessageTableViewCell() {
    CGSize _intrinsicContentSize;
}
@end

@implementation LeftTextMessageTableViewCell
-(void)setTextMessage:(AVIMTextMessage *)textMessage{
    [super setTextMessage:textMessage ];
    
    // 显示该条消息的发送者的 ClientId，并且为 Label 控件设置可点击的事件。
    self.clientIdLabel.text=self.textMessage.clientId;
    // 显示文本消息的内容setAttributedText
    [self.messageContentTextView setAttributedText:[[NSMutableAttributedString alloc] initWithString:textMessage.text]];
    [self textViewDidChange:self.messageContentTextView];
}
- (void)textViewDidChange:(UITextView *)textView
{
    textView.frame=CGRectMake(0, 120, 100, 100);
}
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        /*
         * 从 xib 文件中加载 View
         */
        
        [[NSBundle mainBundle] loadNibNamed:@"LeftTextMessageTableViewCell" owner:self options:nil];
        
        self.bounds = self.view.bounds;
        _intrinsicContentSize = self.bounds.size;
        self.messageContentTextView.backgroundColor=[UIColor redColor];
        [self addSubview:self.view];
        
        self.clientIdLabel.userInteractionEnabled = YES;
        UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clientIdLabelTapped:)];
        [self.clientIdLabel addGestureRecognizer:tapGesture];
    }
    return self;
}
- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if(self) {
        
        [[NSBundle mainBundle] loadNibNamed:@"MessageToolBarView" owner:self options:nil];
        
        [self addSubview:self.view];
        
        _intrinsicContentSize = self.bounds.size;
    }
    return self;
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
