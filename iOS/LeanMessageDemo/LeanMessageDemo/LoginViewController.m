//
//  ViewController.m
//  SimpleChat
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
	self.title = @"登录";
//	self.selfIdTextField.text = @"a";
}

- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
	// Dispose of any resources that can be recreated.
}

- (IBAction)onLoginButtonClicked:(id)sender {
	NSString *selfId = self.selfIdTextField.text;
	if (selfId.length > 0) {
		WEAKSELF
		[[LeanMessageManager manager] openSessionWithClientID : selfId completion : ^(BOOL succeeded, NSError *error) {
		    if (!error) {
		        [weakSelf performSegueWithIdentifier:@"toMain" sender:self];
			}
		}];
	}
}

@end
