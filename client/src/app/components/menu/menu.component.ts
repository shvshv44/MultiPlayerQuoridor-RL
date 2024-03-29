import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {PlayDialogComponent} from '../play-dialog/play-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {PlayDialogData} from '../../interfaces/play-dialog-data';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {WebSocketApiService} from '../../services/web-socket-api/web-socket-api.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ConfigService} from '../../services/config/config.service';
import {Store} from '@ngrx/store';
import {setGameId, setPawnName} from '../../reducers/global/global.actions';
import {Clipboard} from '@angular/cdk/clipboard';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  private dialogData: PlayDialogData;
  private readonly serverURL;

  constructor(private router: Router,
              private dialog: MatDialog,
              private http: HttpClient,
              private webSocket: WebSocketApiService,
              private snackBar: MatSnackBar,
              private configService: ConfigService,
              private store: Store,
              private clipboard: Clipboard) {
    this.dialogData = {gameId: '', action: 'none', playerName: ''};
    this.serverURL = this.configService.getConfig().serverUrl;
  }

  ngOnInit(): void {
  }

  callHello(): void {
    this.webSocket._hello({id: 5});
  }

  onGameRulesClick(): void {
    this.router.navigateByUrl('/game-rules');
  }

  onGameHistoryClick(): void {
    this.router.navigateByUrl('/game-history');
  }

  async onPlayButtonClick(): Promise<void> {
    const dialogRef = this.dialog.open(PlayDialogComponent, {
      height: '300px',
      width: '650px',
      data: this.dialog
    });

    await dialogRef.afterClosed().subscribe(async result => {
      if (result !== undefined) {
        await this.onCreateOrJoinGame(result);
      }
    });
  }

  async onCreateOrJoinGame(result: PlayDialogData): Promise<void> {
    if (result.action === 'create') {
      await this.createGame(result.playerName);
    } else if (result.action === 'join') {
      await this.joinGame(result.gameId, result.playerName);
    }
  }

  async createGame(playerName: string): Promise<void> {
    if (playerName !== undefined) {
      const createGameURL = this.serverURL + '/CreateGame' + '/' + playerName;
      const returnedGameId = await this.http.get(createGameURL, {responseType: 'text'}).toPromise();
      this.clipboard.copy(returnedGameId);
      await this.connectAndGoToRoomScreen(returnedGameId, playerName);
    } else {
      this.snackBar.open('You must choose a name!', 'close', {duration: 10000});
    }
  }

  async joinGame(gameId: string, playerName: string): Promise<void> {
    if (playerName !== undefined) {
      const joinGameURL = this.serverURL + '/JoinGame/' + gameId + '/' + playerName;
      const returnedGameId = await this.http.get(joinGameURL, {responseType: 'text'}).toPromise().catch((err: HttpErrorResponse) => {
        this.snackBar.open(err.error, 'close', {duration: 10000});
      });

      if (typeof returnedGameId === 'string') {
        await this.connectAndGoToRoomScreen(returnedGameId, playerName);
      }
    } else {
      this.snackBar.open('You must choose a name!', 'close', {duration: 10000});
    }
  }

  async connectAndGoToRoomScreen(gameId: string, playerName: string): Promise<void> {
    await this.webSocket._connectToGame(gameId, playerName);
    this.defaultGameState(gameId, playerName);
    this.router.navigateByUrl('/game-room-screen');
  }

  private defaultGameState(gameId: string, playerName: string): void {
    this.store.dispatch(setPawnName({pawnName: playerName}));
    this.store.dispatch(setGameId({gameId}));
  }
}
