import {Component, OnInit} from '@angular/core';
import {WebSocketApiService} from '../../services/web-socket-api/web-socket-api.service';
import {MessageHandlerService} from '../../services/message-handler/message-handler.service';
import {Router} from '@angular/router';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';
import {WebSocketMessageType} from '../../enums/web-socket-message-type.enum';
import {Store} from '@ngrx/store';
import {selectGameId, selectPawnName} from '../../reducers/global/global.selectors';
import {Pawn} from '../../interfaces/pawn';
import {selectPawnArray} from '../../reducers/pawns/pawns.selectors';
import {ConfigService} from '../../services/config/config.service';
import {addPawns, addPawnsWithoutPosition} from '../../reducers/pawns/pawns.actions';

@Component({
  selector: 'app-game-room-screen',
  templateUrl: './game-room-screen.component.html',
  styleUrls: ['./game-room-screen.component.scss']
})
export class GameRoomScreenComponent implements OnInit {
  private readonly serverURL;
  private readonly agentServerUrl;
  gameId: string;
  pawnName: string;
  pawns: Pawn[] = [];

  constructor(public webSocket: WebSocketApiService,
              public messageHandler: MessageHandlerService,
              private router: Router,
              private http: HttpClient,
              private snackBar: MatSnackBar,
              private store: Store,
              private config: ConfigService) {
    this.serverURL = this.config.getConfig().serverUrl;
    this.agentServerUrl = this.config.getConfig().agentServerUrl;
    this.store.select(selectGameId).subscribe(gameId => this.gameId = gameId);
    this.store.select(selectPawnName).subscribe(pawnName => this.pawnName = pawnName);
    this.store.select(selectPawnArray).subscribe(pawns => this.pawns = pawns);
    messageHandler.assignHandler(WebSocketMessageType.RoomStateResponse, (message) => {
      this.store.dispatch(addPawnsWithoutPosition({pawns: message.players}));
      console.log(message.players);
    });

    messageHandler.assignHandler(WebSocketMessageType.StartGameMessage, (message) => {
      const pawns: Pawn[] = message.players.map(player => {
        return {
          position: player.position, name: player.name, numOfWalls: message.numOfWalls
        };
      });

      this.store.dispatch(addPawns({pawns}));
      router.navigateByUrl('/game-screen');
    });


  }

  ngOnInit(): void {
    if (this.gameId === '') {
      this.router.navigateByUrl('/menu');
    }

    this.webSocket._sendRoomStatusRequest(
      {
        type: WebSocketMessageType.RoomStateRequest,
        gameID: this.gameId
      });
  }

  async startGame(): Promise<void> {
    const startGameURL = this.serverURL + '/StartGame/' + this.gameId;
    await this.http.get(startGameURL, {responseType: 'text'}).toPromise().catch((err: HttpErrorResponse) => {
      this.snackBar.open(err.error, 'close', {duration: 10000});
    });
  }

  async addAgent(): Promise<void> {
    const createGameURL = this.agentServerUrl + '/AddAgentToGame' + '/' + this.gameId;
    await this.http.get(createGameURL, {responseType: 'text'}).toPromise().catch((err: HttpErrorResponse) => {
      this.snackBar.open(err.error, 'close', {duration: 10000});
    });
  }
}
