//
//  TextMessageTableViewCell.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/24/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

#import "TextMessageTableViewCell.h"

@interface LeftTextMessageTableViewCell : TextMessageTableViewCell

@property (strong, nonatomic) IBOutlet UILabel *messageSenderClientId;
@property (strong, nonatomic) IBOutlet UITextView *textMessageContentTextView;

@end
