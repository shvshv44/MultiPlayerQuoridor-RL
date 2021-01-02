import { Injectable } from '@angular/core';
import {WebSocketMessageType} from './enums/web-socket-message-type.enum';

@Injectable({
  providedIn: 'root'
})
export class MessageHandlerService {

  handlers = new Map<WebSocketMessageType, (message: any) => void>();

  constructor() { }

  assignHandler(type: WebSocketMessageType, handler: (message: any) => void): void {
    this.handlers.set(type, handler);
  }

  // tslint:disable-next-line:typedef
  handleMessage(message: any) {
    // tslint:disable-next-line:triple-equals
    if (message.type != null && this.handlers.has(message.type)) {
      console.log('handle message of type ' + message.type);
      // @ts-ignore
      (this.handlers.get(message.type))(message);
    }
  }
}
