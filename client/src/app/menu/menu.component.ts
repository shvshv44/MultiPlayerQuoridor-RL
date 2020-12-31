
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {PlayDialogComponent} from '../play-dialog/play-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {PlayDialogData} from '../interfaces/play-dialog-data';
import {HttpClient} from '@angular/common/http';
import {GameRoomService} from '../game-room.service';
import {WebSocketApiService} from '../web-socket-api.service';


@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {


  private dialogData: PlayDialogData;
  private readonly serverURL = 'http://localhost:8080';

  constructor(private router: Router,
              private dialog: MatDialog,
              private http: HttpClient,
              private gameRoom: GameRoomService,
              private webSocket: WebSocketApiService) {
    this.dialogData = {gameId: '', action: 'none', playerName: ''};
  }

  ngOnInit(): void {
  }

  callHello(): void {
    this.webSocket._hello({id: 5});
  }

  onGameHistoryClick(): void {
    this.router.navigateByUrl('/game-history');
  }

  async onPlayButtonClick(): Promise<void> {
    const dialogRef = this.dialog.open(PlayDialogComponent, {
      height: '400px',
      width: '600px',
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
    const createGameURL = this.serverURL + '/CreateGame' + '/' + playerName;
    const returnedGameId = await this.http.get(createGameURL, {responseType: 'text'}).toPromise();

    await this.connectAndGoToRoomScreen(returnedGameId, playerName);
  }

  async joinGame(gameId: string, playerName: string): Promise<void> {
    const createGameURL = this.serverURL + '/JoinGame/' + gameId + '/' + playerName;
    const returnedGameId = await this.http.get(createGameURL, {responseType: 'text'}).toPromise();

    await this.connectAndGoToRoomScreen(returnedGameId, playerName);
  }

  async connectAndGoToRoomScreen(gameId: string, playerName: string): Promise<void> {
    await this.webSocket._connectToGame(gameId);
    this.defaultGameState(gameId, playerName);
    this.router.navigateByUrl('/game-room-screen');
  }

  private defaultGameState(gameId: string, playerName: string): void {
    this.gameRoom.gameID = gameId;
    this.gameRoom.playerName = playerName;
    this.gameRoom.allPlayers = [playerName];
  }
}
