//
//  SingleChatViewController.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/24/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>
#import "BaseChatViewController.h"

@interface SingleChatViewController : BaseChatViewController
@property (nonatomic,strong) NSString *targetClientId;
@end
