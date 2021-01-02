import {Component, Inject, Input, OnInit} from '@angular/core';
import {PlayerColorEnum} from '../../enums/player-color.enum';

@Component({
  selector: 'app-board-cell',
  templateUrl: './board-cell.component.html',
  styleUrls: ['./board-cell.component.scss']
})
export class BoardCellComponent implements OnInit {
  @Input() playerCode: number | undefined; // if a player stands on the cell
  @Input() legalPosition: boolean | undefined; // if a player stands on the cell

  PlayerColorEnum = PlayerColorEnum;

  constructor() {
  }

  ngOnInit(): void {
  }

}
