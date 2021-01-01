import {Component, OnInit} from '@angular/core';
import {GameRoomService} from '../game-room.service';
import {WebSocketApiService} from '../web-socket-api.service';
import {MessageHandlerService} from '../message-handler.service';
import {Router} from '@angular/router';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';
import {WebSocketMessageType} from '../web-socket-message-type.enum';

@Component({
  selector: 'app-game-room-screen',
  templateUrl: './game-room-screen.component.html',
  styleUrls: ['./game-room-screen.component.scss']
})
export class GameRoomScreenComponent implements OnInit {

  private readonly serverURL = 'http://localhost:8080';

  constructor(public gameRoom: GameRoomService,
              public webSocket: WebSocketApiService,
              public messageHandler: MessageHandlerService,
              private router: Router,
              private http: HttpClient,
              private snackBar: MatSnackBar) {
    messageHandler.assignHandler(WebSocketMessageType.RoomStateResponse, (message) => {
      this.gameRoom.allPlayers = message.players;
    });

    messageHandler.assignHandler(WebSocketMessageType.StartGameEvent, (message) => {
      // TODO: Sharon/Guy Raviv need to laod the game data to the Store!!!
      console.log(message);
      router.navigateByUrl('/game-screen');
    });
  }

  ngOnInit(): void {
    if (this.gameRoom.gameID === 'error') {
      this.router.navigateByUrl('/menu');
    }

    this.webSocket._sendRoomStatusRequest(
      {
        type: WebSocketMessageType.RoomStateRequest,
        gameID: this.gameRoom.gameID
      });
  }

  async startGame(): Promise<void> {
    const startGameURL = this.serverURL + '/StartGame/' + this.gameRoom.gameID;
    await this.http.get(startGameURL, {responseType: 'text'}).toPromise().catch((err: HttpErrorResponse) => {
      this.snackBar.open(err.error, 'close', { duration: 10000, });
    });
  }
}
