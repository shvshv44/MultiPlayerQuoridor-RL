import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {PlayDialogComponent} from '../play-dialog/play-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {PlayDialogData} from '../interfaces/play-dialog-data';
import {HttpClient} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
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

  onGameHistoryClick(): void{
    this.router.navigateByUrl('/game-history');
  }

  onPlayButtonClick(): void {
    const dialogRef = this.dialog.open(PlayDialogComponent, {
      width: '250px',
      data: this.dialog
    });

    dialogRef.afterClosed().subscribe(result => this.onCreateOrJoinGame(result));
  }

  onCreateOrJoinGame(result: PlayDialogData): void {
    if (result.action === 'create') {
      this.createGame('shaq');
    } else if (result.action === 'join') {
      this.joinGame(result.gameId);
    }
  }

  createGame(playerName: string): void {
    const createGameURL = this.serverURL + '/CreateGame' + '/' + playerName;
    this.http.get(createGameURL, {responseType: 'text'})
      .subscribe(value => {
        this.webSocket._connectToGame(value);
        this.gameRoom.gameID = value;
        this.gameRoom.playerName = playerName;
        this.gameRoom.allPlayers = [playerName];
        this.router.navigateByUrl('/game-room-screen');
      });
  }

  joinGame(gameId: string): void {
    const createGameURL = this.serverURL + '/JoinGame/' + gameId + '/shaq';
    this.http.get(createGameURL, {responseType: 'text'})
      .subscribe(value => {
        console.log(value); // TODO: open tcp and go to room
      });
  }
}
