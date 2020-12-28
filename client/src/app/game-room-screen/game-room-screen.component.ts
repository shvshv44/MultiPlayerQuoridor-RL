import { Component, OnInit } from '@angular/core';
import {GameRoomService} from '../game-room.service';

@Component({
  selector: 'app-game-room-screen',
  templateUrl: './game-room-screen.component.html',
  styleUrls: ['./game-room-screen.component.scss']
})
export class GameRoomScreenComponent implements OnInit {

  constructor(public gameRoom: GameRoomService) { }

  ngOnInit(): void {
  }

}
