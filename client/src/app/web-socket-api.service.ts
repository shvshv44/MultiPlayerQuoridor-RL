import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';


@Injectable({
  providedIn: 'root'
})
export class WebSocketApiService {
  webSocketEndPoint = 'http://localhost:8080/quoridor-websocket';
  topic = '/topic/gameStatus/';
  stompClient: any;
  gameId = '-1';

  constructor() {
    this._connect();
  }

  // tslint:disable-next-line:typedef
  _connect() {
    console.log('Initialize WebSocket Connection');
    const ws = new SockJS(this.webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
  }

  // tslint:disable-next-line:typedef
  _connectToGame(gameId: string) {
    this.gameId = gameId;
    // tslint:disable-next-line:variable-name
    const _this = this;

    // tslint:disable-next-line:only-arrow-functions typedef
    _this.stompClient.connect({}, function () {
      // tslint:disable-next-line:only-arrow-functions typedef
      _this.stompClient.subscribe(_this.topic + gameId, function(sdkEvent: any) {
        _this.onMessageReceived(sdkEvent);
      });
      // _this.stompClient.reconnect_delay = 2000;
    }, this.errorCallBack);

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
      this._connect();
    }, 5000);
  }

  /**
   * Send message to sever via web socket
   * @param {*} message
   */
  // tslint:disable-next-line:typedef
  _sendPawnMovment(message: any) {
    console.log('calling send pawn movment');
    this.stompClient.send('/app/' + this.gameId + '/movePawn', {}, JSON.stringify(message));
  }

  // tslint:disable-next-line:typedef
  _sendPutWall(message: any) {
    console.log('calling send put wall');
    this.stompClient.send('/app/' + this.gameId + '/putWall', {}, JSON.stringify(message));
  }

  public _sendRoomStatusRequest(message: any): void {
    console.log('asking for room status');
    this.stompClient.send('/app/' + this.gameId + '/roomStatusRequest', {}, JSON.stringify(message));
  }

  // tslint:disable-next-line:typedef
  onMessageReceived(message: string) {
    console.log('Message Recieved from Server :: ' + message);
    // this.appComponent.handleMessage(JSON.stringify(message.body));
  }
}
