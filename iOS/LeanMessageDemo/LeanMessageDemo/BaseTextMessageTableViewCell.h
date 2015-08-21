//
//  BaseTextMessageTableViewCell.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/21/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

@interface BaseTextMessageTableViewCell : UITableViewCell

@property (strong,nonatomic) AVIMTextMessage *textMessage;

-(void)clientIdLabelTapped:(UIGestureRecognizer *)sender;

@end
