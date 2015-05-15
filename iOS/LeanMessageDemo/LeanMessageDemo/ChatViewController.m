//
//  ChatViewController.m
//  SimpleChat
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "ChatViewController.h"

@interface ChatViewController () <UITableViewDataSource, UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITableView *messageTableView;
@property (weak, nonatomic) IBOutlet UITextField *inputTextField;
@property (nonatomic, strong) NSMutableArray *messages;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *keyboardHeight;

@end

@implementation ChatViewController

#pragma mark - life cycle

- (void)viewDidLoad {
	[super viewDidLoad];
	_messages = [NSMutableArray array];

	self.title = @"Chat";
    
	[self initTableView];
    
    [self setupReceiveMessageBlock];
    
	[self loadMessagesWhenInit];
}

- (void)initTableView {
	self.messageTableView.dataSource = self;
	self.messageTableView.delegate = self;
}

- (void)viewDidAppear:(BOOL)animated {
	[super viewDidAppear:animated];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillChangeFrameNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)viewDidDisappear:(BOOL)animated {
	[super viewDidDisappear:animated];
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
}

- (void)dealloc {
	[[LeanMessageManager manager] setupDidReceiveTypedMessageCompletion:nil];
}

#pragma mark - tableview

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return self.messages.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	NSString *identifier = @"cell";
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
	if (cell == nil) {
		cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
	}
	AVIMTypedMessage *message = self.messages[indexPath.row];
	NSString *text;
	UIColor *fontColor;
	if (message.mediaType == kAVIMMessageMediaTypeText) {
		AVIMTextMessage *textMessage = (AVIMTextMessage *)message;
		text = textMessage.text;
	}
	else {
		text = @"其它格式的消息";
	}
	if ([message.clientId isEqualToString:[LeanMessageManager manager].selfClientID]) {
		fontColor = [UIColor blackColor];
	}
	else {
		fontColor = [UIColor orangeColor];
	}
	cell.textLabel.textColor = fontColor;
	cell.textLabel.text = [NSString stringWithFormat:@"%@ : %@", message.clientId, text];
	return cell;
}

#pragma mark - message

- (void)loadMessagesWhenInit {
    [self.conversation queryMessagesBeforeId:nil timestamp:[[NSDate distantFuture] timeIntervalSince1970] * 1000 limit:100 callback: ^(NSArray *objects, NSError *error) {
        if (error == nil) {
            NSMutableArray *messages = [NSMutableArray array];
            for (AVIMMessage *message in objects) {
                if ([message isKindOfClass:[AVIMTypedMessage class]]) {
                    [messages addObject:message];
                }
            }
            self.messages = messages;
            [self.messageTableView reloadData];
            [self scrollToLast];
        }
    }];
}

- (IBAction)onSendButtonClicked:(id)sender {
    NSString *text = self.inputTextField.text;
    if (text.length > 0) {
        AVIMTextMessage *textMessage = [[AVIMTextMessage alloc] init];
        textMessage.text = text;
        WEAKSELF
        [self.conversation sendMessage : textMessage callback : ^(BOOL succeeded, NSError *error) {
            if ([weakSelf filterError:error]) {
                [weakSelf addMessage:textMessage];
                weakSelf.inputTextField.text = nil;
            }
        }];
    }
}

-(void)setupReceiveMessageBlock{
    [[LeanMessageManager manager] setupDidReceiveTypedMessageCompletion: ^(AVIMConversation *conversation, AVIMTypedMessage *message) {
        if ([conversation.conversationId isEqualToString:self.conversation.conversationId]) {
            [self addMessage:message];
        }
    }];
}

- (void)addMessage:(AVIMTypedMessage *)message {
	[self.messages addObject:message];
	[self.messageTableView reloadData];
	[self scrollToLast];
}

#pragma mark - util

- (BOOL)filterError:(NSError *)error {
	if (error) {
		UIAlertView *alertView = [[UIAlertView alloc]
		                          initWithTitle:nil message:error.description delegate:nil
		                       cancelButtonTitle   :@"确定" otherButtonTitles:nil];
		[alertView show];
		return NO;
	}
	return YES;
}

#pragma mark - keyboard

- (void)keyboardWillShow:(NSNotification *)notification {
	NSDictionary *info = notification.userInfo;
	NSValue *kbFrame = [info objectForKey:UIKeyboardFrameEndUserInfoKey];
	NSTimeInterval animationDuration = [[info objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
	CGRect keyboardFrame = [kbFrame CGRectValue];
	CGFloat height = keyboardFrame.size.height;
	self.keyboardHeight.constant = height;
	[UIView animateWithDuration:animationDuration animations: ^{
	    [self.view layoutIfNeeded];
	}];
}

- (void)keyboardWillHide:(NSNotification *)notification {
	NSTimeInterval animationDuration = [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
	self.keyboardHeight.constant = 0;
	[UIView animateWithDuration:animationDuration animations: ^{
	    [self.view layoutIfNeeded];
	}];
}

#pragma mark - scroll
- (void)scrollToLast {
    if(self.messages.count>0){
     	[self.messageTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:self.messages.count - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:NO];
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
	if ([self.inputTextField isFirstResponder]) {
		[self.inputTextField resignFirstResponder];
	}
}

@end
