import { Component, OnInit } from '@angular/core';
import {GameRoomService} from '../game-room.service';
import {WebSocketApiService} from '../web-socket-api.service';
import {MessageHandlerService} from '../message-handler.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-game-room-screen',
  templateUrl: './game-room-screen.component.html',
  styleUrls: ['./game-room-screen.component.scss']
})
export class GameRoomScreenComponent implements OnInit {

  constructor(public gameRoom: GameRoomService,
              public webSocket: WebSocketApiService,
              public messageHandler: MessageHandlerService,
              public router: Router) {
    messageHandler.assignHandler('RoomStateResponse', (message) => {
      this.gameRoom.allPlayers = message.players;
    });
  }

  ngOnInit(): void {
    if (this.gameRoom.gameID === 'error') {
      this.router.navigateByUrl('/menu');
    }

    this.webSocket._sendRoomStatusRequest(
      {
        type: 'RoomStateRequest',
        gameID: this.gameRoom.gameID
      });
  }
}
