import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-game-history',
  templateUrl: './game-history.component.html',
  styleUrls: ['./game-history.component.scss']
})
export class GameHistoryComponent implements OnInit {
  gamesIds: string[] = ['1', '2', '3', '4', '5'];

  constructor() { }

  ngOnInit(): void {
  }

}
