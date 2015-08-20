//
//  DemoTextMessageTableViewCell.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/19/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

@interface DemoTextMessageTableViewCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UILabel *clientIdLabel;
@property (strong, nonatomic) IBOutlet UITextView *messageContentTextView;
@property (strong,nonatomic) AVIMTextMessage *textMessage;

@property (strong, nonatomic) IBOutlet UIImageView *messageContentBackgroudImage;
@end
