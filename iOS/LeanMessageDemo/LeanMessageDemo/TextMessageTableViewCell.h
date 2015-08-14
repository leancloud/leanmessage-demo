//
//  TextMessageTableViewCell.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/13/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TextMessageTableViewCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UITextView *textMessageContentTextView;
@property (strong, nonatomic) IBOutlet UILabel *messageSenderClientIdLabel;

@end
