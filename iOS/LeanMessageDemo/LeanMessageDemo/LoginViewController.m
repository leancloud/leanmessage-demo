//
//  ViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "LoginViewController.h"
#import "LeanMessageManager.h"

@interface LoginViewController ()

@property (weak, nonatomic) IBOutlet UITextField *selfIdTextField;

@end

@implementation LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.title = @"LeanMessageDemo";
    //	self.selfIdTextField.text = @"a";
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *selfId = [userDefaults objectForKey:kLoginSelfIdKey];
    if (selfId) {
        [self openSessionAndGoMainViewController:selfId];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onLoginButtonClicked:(id)sender {
    NSString *selfId = self.selfIdTextField.text;
    if (selfId.length > 0) {
        [self openSessionAndGoMainViewController:selfId];
    }
}

- (void)openSessionAndGoMainViewController:(NSString *)selfId{
    WEAKSELF
    [[LeanMessageManager manager] openSessionWithClientID : selfId completion : ^(BOOL succeeded, NSError *error) {
        if (!error) {
            [[NSUserDefaults standardUserDefaults] setObject:selfId forKey:kLoginSelfIdKey];
            [[NSUserDefaults standardUserDefaults] synchronize];
            [weakSelf performSegueWithIdentifier:@"toMain" sender:self];
        }
    }];
}

@end
