//
//  MessageToolBarView.m
//  LeanMessageDemo
//
//  Created by LeanCloud on 8/20/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "MessageToolBarView.h"

@interface MessageToolBarView() {
    CGSize _intrinsicContentSize;
}
@end

@implementation MessageToolBarView
-(void)setCurrentConversation:(AVIMConversation *)currentConversation{
    _currentConversation=currentConversation;
}
- (CGSize)intrinsicContentSize {
    return _intrinsicContentSize;
}
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        /*
         * 从 xib 文件中加载 View
         */
        
        [[NSBundle mainBundle] loadNibNamed:@"MessageToolBarView" owner:self options:nil];
        
        self.bounds = self.view.bounds;
        _intrinsicContentSize = self.bounds.size;
        [self addSubview:self.view];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.view
                                                              attribute:NSLayoutAttributeBottom
                                                              relatedBy:NSLayoutRelationEqual
                                                                 toItem:self
                                                              attribute:NSLayoutAttributeBottom
                                                             multiplier:1.0
                                                               constant:0.0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.view
                                                                  attribute:NSLayoutAttributeTop
                                                                  relatedBy:NSLayoutRelationEqual
                                                                     toItem:self
                                                                  attribute:NSLayoutAttributeTop
                                                                 multiplier:1.0
                                                                   constant:0.0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.view
                                                              attribute:NSLayoutAttributeLeading
                                                              relatedBy:NSLayoutRelationEqual
                                                                 toItem:self
                                                              attribute:NSLayoutAttributeLeading
                                                             multiplier:1.0
                                                               constant:0.0]];
        [self addConstraint:[NSLayoutConstraint constraintWithItem:self.view
                                                              attribute:NSLayoutAttributeTrailing
                                                              relatedBy:NSLayoutRelationEqual
                                                                 toItem:self
                                                              attribute:NSLayoutAttributeTrailing
                                                             multiplier:1.0
                                                               constant:0.0]];

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
/**
 *  点击「发送」按钮，将消息发送到当前的聊天室
 */
- (IBAction)sendMessageClicked:(id)sender {
    AVIMTextMessage *textMessage = [AVIMTextMessage messageWithText:self.messageInputTextField.text attributes:nil];
    [self.currentConversation sendMessage:textMessage callback:^(BOOL succeeded, NSError *error) {
        if (error) {
            // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
            // 此时聊天服务不可用。
            UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"聊天不可用！" message:[error description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [view show];
        }
        else{
            //[self.messages addObject:textMessage];
            //[self.messageTableView reloadData];
            UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"发送成功！" message:[error description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            self.messageInputTextField.text = @"";
            [view show];
        }
    }];
}
@end
