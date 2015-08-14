//
//  MainViewController.h
//  LeanMessageDemo
//
//  Created by lzw on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

@class MainViewController;

@interface MainViewController :UIViewController

- (IBAction)sendMessage:(id)sender;
@property (strong, nonatomic) IBOutlet UITableView *messageTableView;
@property (strong, nonatomic) IBOutlet UITextField *messageInputTextField;
@end
