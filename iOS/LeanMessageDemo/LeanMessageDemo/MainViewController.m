//
//  MainViewController.m
//  SimpleChat
//
//  Created by lzw on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "MainViewController.h"
#import "LeanMessageManager.h"
#import "ChatViewController.h"

#define kConversationId @"551a2847e4b04d688d73dc54"

@interface MainViewController ()

@property (weak, nonatomic) IBOutlet UITextField *otherIdTextField;

@end

@implementation MainViewController

- (void)viewDidLoad {
	[super viewDidLoad];
	self.title = @"主页";
//	self.otherIdTextField.text = @"b";
}

- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
}

- (IBAction)onChatButtonClicked:(id)sender {
	NSString *otherId = self.otherIdTextField.text;
	if (otherId.length > 0) {
		[[LeanMessageManager manager] createConversationsWithClientIDs:@[otherId] conversationType:ConversationTypeOneToOne completion: ^(AVIMConversation *conversation, NSError *error) {
		    if (error) {
		        NSLog(@"error=%@", error);
			}
		    else {
		        [self performSegueWithIdentifier:@"toChat" sender:conversation];
			}
		}];
	}
}

- (IBAction)onStartGroupConversationButtonClicked:(id)sender {
	[[LeanMessageManager manager] fetchConversationById:kConversationId block: ^(AVIMConversation *conversation, NSError *error) {
	    if (!error) {
	        [self performSegueWithIdentifier:@"toChat" sender:conversation];
		}
	}];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
	ChatViewController *chatViewController = (ChatViewController *)segue.destinationViewController;
	chatViewController.conversation = sender;
}

@end
