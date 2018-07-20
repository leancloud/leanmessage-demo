import {TypedMessage, messageField, messageType} from 'leancloud-realtime';

export default class StickerMessage extends TypedMessage {
  constructor(group, sticker) {
    super();
    this.group = group;
    this.sticker = sticker;
  }
  get summary() {
    return `[Sticker] ${this.sticker}`;
  }
}
messageField(['group', 'sticker'])(StickerMessage);
messageType(100)(StickerMessage);
