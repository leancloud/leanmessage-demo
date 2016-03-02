//
//  TextMessageTableViewCell.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/25/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "TextMessageTableViewCell.h"

@implementation TextMessageTableViewCell
-(void)setTextMessage:(AVIMTextMessage *)textMessage{
    _textMessage = textMessage;
}
- (void)awakeFromNib {
    // Initialization code
}
-(instancetype)init{
    self = [super init];
    if (self) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"MessageTableCells" owner:nil options:nil];
        
        self =  [nib objectAtIndex:0];
    }
    return self;
}
+ (instancetype)cellWithTableView:(UITableView *)tableView isMe:(BOOL)isMe
{
    static NSString *CellIdentifier;
    static NSString *CellNib = @"MessageTableCells";
    if (isMe) {
        CellIdentifier = @"LeftTextMessageTableViewCell";
    } else {
        CellIdentifier = @"RightTextMessageTableViewCell";
    }
    
    TextMessageTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        /*
         * 从 xib 文件中加载 View
         */
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:CellNib owner:self options:nil];
        if(isMe) {
            cell = (TextMessageTableViewCell *)[nib objectAtIndex:1];
        } else {
            cell = (TextMessageTableViewCell *)[nib objectAtIndex:0];
        }
    }
    return cell;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    
    // Configure the view for the selected state
}

@end
