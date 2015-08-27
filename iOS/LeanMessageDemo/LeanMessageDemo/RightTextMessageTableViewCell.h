//
//  RightTextMessageTableViewCell.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/25/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "TextMessageTableViewCell.h"

@interface RightTextMessageTableViewCell : TextMessageTableViewCell
@property (strong, nonatomic) IBOutlet UITextView *textMessageContentTextView;
@property (strong, nonatomic) IBOutlet UILabel *messageSenderClientId;

@end
