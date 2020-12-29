
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
    this.dialogData = {gameId: '', action: 'none'};
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
      width: '250px',
      data: this.dialog
    });

    await dialogRef.afterClosed().subscribe(async result => await this.onCreateOrJoinGame(result));
  }

  async onCreateOrJoinGame(result: PlayDialogData): Promise<void> {
    if (result.action === 'create') {
      await this.createGame('shaq');
    } else if (result.action === 'join') {
      this.joinGame(result.gameId);
    }
  }

  async createGame(playerName: string): Promise<void> {
    const createGameURL = this.serverURL + '/CreateGame' + '/' + playerName;
    const value = await this.http.get(createGameURL, {responseType: 'text'}).toPromise();

    await this.webSocket._connectToGame(value);
    this.gameRoom.gameID = value;
    this.gameRoom.playerName = playerName;
    this.gameRoom.allPlayers = [playerName];
    this.router.navigateByUrl('/game-room-screen');
  }

  joinGame(gameId: string): void {
    const createGameURL = this.serverURL + '/JoinGame/' + gameId + '/shaq';
    this.http.get(createGameURL, {responseType: 'text'})
      .subscribe(value => {
        console.log(value); // TODO: open tcp and go to room
      });
  }
}
