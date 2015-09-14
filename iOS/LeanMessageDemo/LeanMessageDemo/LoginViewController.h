//
//  LoginViewController.h
//  LeanMessageDemo
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

static NSString *kLoginSelfIdKey = @"selfId";

@interface LoginViewController : UIViewController
@property (strong, nonatomic) IBOutlet UITextField *clientIdTextFiled;
@property (strong, nonatomic) IBOutlet UIButton *loginButton;
- (IBAction)inputingClientId:(id)sender;
@property (strong, nonatomic) IBOutlet UIImageView *fruitIconImage;

@end
