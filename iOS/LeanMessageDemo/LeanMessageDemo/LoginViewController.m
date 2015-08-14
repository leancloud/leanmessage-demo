//
//  LoginViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "LoginViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface LoginViewController ()

@end

@implementation LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    //[self.loginButton setBackgroundColor:[UIColor colorWithRed:44 green:151 blue:235 alpha:1]];
    self.fruitIconImage.clipsToBounds = YES;
    self.fruitIconImage.layer.cornerRadius=5;
   
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onLoginButtonClicked:(id)sender {
    // 创建一个 AVIMClient 实例
    AVIMClient *imClient = [AVIMClient defaultClient];
    
    [imClient openWithClientId:self.clientIdTextFiled.text callback:^(BOOL succeeded, NSError *error){
        if (error) {
            // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
            // 此时聊天服务不可用。
            UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"聊天不可用！" message:[error description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [view show];
        } else {
            // 成功登录，可以进入聊天主界面了。
          [self performSegueWithIdentifier:@"toChatroom" sender:self];
        }
    }];
    
}

- (IBAction)inputingClientId:(id)sender {
    
}
@end
