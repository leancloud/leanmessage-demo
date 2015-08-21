//
//  RightTextMessageTableViewCell.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/21/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

#import "BaseTextMessageTableViewCell.h"

@interface RightTextMessageTableViewCell : BaseTextMessageTableViewCell
@property (strong, nonatomic) IBOutlet UILabel *clientIdLabel;
@property (strong, nonatomic) IBOutlet UITextView *messageContentTextView;
@property (strong, nonatomic) IBOutlet UIImageView *messageContentBackgroudImage;
@end
