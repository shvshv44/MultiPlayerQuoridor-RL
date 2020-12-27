import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GameRoomService {

  public gameID: string;
  public playerName: string;
  public allPlayers: string[];

  constructor() {
    this.gameID = 'error';
    this.playerName = 'error';
    this.allPlayers = [];
  }
}
