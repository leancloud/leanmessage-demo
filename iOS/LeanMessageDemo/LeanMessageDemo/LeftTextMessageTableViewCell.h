//
//  DemoTextMessageTableViewCell.h
//  LeanMessageDemo
//
//  Created by LeanCloud on 8/19/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

#import "BaseTextMessageTableViewCell.h"

@interface LeftTextMessageTableViewCell : BaseTextMessageTableViewCell

@property (strong, nonatomic) IBOutlet UILabel *clientIdLabel;
@property (strong, nonatomic) IBOutlet UITextView *messageContentTextView;
@property (strong, nonatomic) IBOutlet UIImageView *messageContentBackgroudImage;

@end
