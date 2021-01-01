import {Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {MessageHandlerService} from '../../message-handler.service';
import {ConfigService} from '../config/config.service';


@Injectable({
  providedIn: 'root'
})
export class WebSocketApiService {
  webSocketEndPoint;
  topic = '/topic/gameStatus';
  stompClient: any;
  gameId = '-1';
  isOpen = false;

  constructor(public msgHandler: MessageHandlerService,
              private configService: ConfigService) {
    this.webSocketEndPoint = this.configService.getConfig().serverUrl;
  }

// tslint:disable-next-line:typedef
  async _connectToGame(gameId: string) {
    console.log('Initialize WebSocket Connection');
    const ws = new SockJS(this.webSocketEndPoint);
    this.stompClient = Stomp.over(ws);

    this.gameId = gameId;
    // tslint:disable-next-line:variable-name
    const _this = this;

    // tslint:disable-next-line:only-arrow-functions typedef
    _this.stompClient.connect({}, function() {
      // tslint:disable-next-line:only-arrow-functions typedef
      _this.stompClient.subscribe(_this.topic + '/' + gameId, function(sdkEvent: any) {
        _this.onMessageReceived(sdkEvent);
      });
      // _this.stompClient.reconnect_delay = 2000;
    }, this.errorCallBack);

    await this.sleep(1000); // MUST EXIST!!!!!!
  }

  sleep(ms: number): Promise<any> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  // tslint:disable-next-line:typedef
  _disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log('Disconnected');
  }

  // on error, schedule a reconnection attempt
  // tslint:disable-next-line:typedef
  errorCallBack(error: string) {
    console.log('errorCallBack -> ' + error);
    setTimeout(() => {
      this._connectToGame(this.gameId);
    }, 5000);
  }

  // tslint:disable-next-line:typedef
  _sendPawnMovment(message: any) {
    console.log('calling send pawn movment');
    this.stompClient.send('/app/' + this.gameId + '/movePawn', {}, JSON.stringify(message));
  }

  // tslint:disable-next-line:typedef
  _sendPutWall(message: any) {
    console.log('calling send put wall');
    this.stompClient.send('/app/' + this.gameId + '/roomStateRequest', {}, JSON.stringify(message));
  }

  // tslint:disable-next-line:typedef
  _hello(message: any) {
    console.log('calling send put wall');
    this.stompClient.send('/app/hello', {}, JSON.stringify(message));
  }

  /**
   * Send message to sever via web socket
   * @param {*} message
   */
  // tslint:disable-next-line:typedef
  _send(message: any) {
    console.log('calling logout api via web socket');
    this.stompClient.send('/app/hello', {}, JSON.stringify(message));
  }

  public _sendRoomStatusRequest(message: any): void {
    console.log('asking for room status');
    this.stompClient.send('/app/' + this.gameId + '/roomStateRequest', {}, JSON.stringify(message));
  }

  // tslint:disable-next-line:typedef
  onMessageReceived(messageEvent: any) {
    const message = JSON.parse(messageEvent.body);
    this.msgHandler.handleMessage(message);
  }
}
