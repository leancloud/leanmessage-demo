import {TypedMessage, messageField, messageType} from 'leancloud-realtime';
import EventEmitter from 'eventemitter3';

export class TypingStatusMessage extends TypedMessage {
  constructor(status) {
    super();
    this.value = status;
  }
}
TypingStatusMessage.STATUS = {
  TYPING: 1,
  FINISHED: 0
};
TypingStatusMessage.sendOptions = {
  transient: true
};
messageField(['value'])(TypingStatusMessage);
messageType(-100)(TypingStatusMessage);

class TypingIndicator extends EventEmitter {
  constructor(client) {
    super();
    this._client = client;
    this._handleMessage = this._handleMessage.bind(this);
    this._reset();
  }
  _reset() {
    this.typingClients = [];
    this._typingClientsActiveTimers = {};
    this._lastSendStatus = {
      type: null,
      timestamp: 0
    };
  }
  setConversation(conversation) {
    if (this._conversation) {
      this._conversation.off('_typingstatusmessage', this._handleMessage);
      this._reset();
    }
    conversation.on('_typingstatusmessage', this._handleMessage);
    this._conversation = conversation;
  }
  _handleMessage(message) {
    const {
      from: client,
      value: status
    } = message;
    this._updateTypingClients(client, status);
  }
  _updateTypingClients(client, status) {
    const isTyping = this._typingClientsActiveTimers[client] !== undefined;
    const changeToTyping = status === TypingStatusMessage.STATUS.TYPING;

    if (this._typingClientsActiveTimers[client]) {
      clearTimeout(this._typingClientsActiveTimers[client]);
    }

    if (changeToTyping) {
      this._typingClientsActiveTimers[client] = setTimeout(() => {
        this._updateTypingClients(client, TypingStatusMessage.STATUS.FINISHED);
      }, 5000);
    } else {
      delete this._typingClientsActiveTimers[client];
    }

    if (isTyping ^ changeToTyping) {
      this.typingClients = Object.keys(this._typingClientsActiveTimers);
      this.emit('change');
    }
  }
  updateStatus(status) {
    return Promise.resolve().then(() => {
      const conversation = this._conversation;
      if (!conversation) {
        throw new Error('must set a Conversation before updating status.');
      }
      if (this._lastSendStatus) {
        if (status === TypingStatusMessage.STATUS.TYPING) {
          if (this._lastSendStatus.timestamp + 3000 > Date.now()) {
            return;
          }
          const message = new TypingStatusMessage(TypingStatusMessage.STATUS.TYPING);
          this._lastSendStatus = {
            type: TypingStatusMessage.STATUS.TYPING,
            timestamp: Date.now()
          };
          return conversation.send(message);
        } else if (status === TypingStatusMessage.STATUS.FINISHED) {
          if (this._lastSendStatus.type === TypingStatusMessage.STATUS.FINISHED) {
            return;
          }
          if (this._lastSendStatus.timestamp + 5000 < Date.now()) {
            return;
          }
          const message = new TypingStatusMessage(TypingStatusMessage.STATUS.FINISHED);
          this._lastSendStatus = {
            type: TypingStatusMessage.STATUS.FINISHED,
            timestamp: 0
          };
          return conversation.send(message);
        }
      }
    });
  }
}

export const TypingIndicatorPlugin = {
  name: 'test',
  messageClasses: [TypingStatusMessage],
  beforeMessageDispatch: (message, conversation) => {
    if (message.type === TypingStatusMessage.TYPE) {
      conversation.emit('_typingstatusmessage', message);
      return false;
    }
    return true;
  },
  onIMClientCreate: client => {
    client.createTypingIndicator = () => new TypingIndicator(client);
  }
};
