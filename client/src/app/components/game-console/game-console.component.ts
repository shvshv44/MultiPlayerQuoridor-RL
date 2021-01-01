import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-game-console',
  templateUrl: './game-console.component.html',
  styleUrls: ['./game-console.component.scss']
})
export class GameConsoleComponent implements OnInit {

  consoleLines: string [] = [];

  constructor() {
    for (let index = 0; index < 100; index++) {
      this.consoleLines.push('console line ' + index);
    }
  }

  ngOnInit(): void {
  }

}
