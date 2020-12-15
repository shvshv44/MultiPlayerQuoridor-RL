import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-board-cell',
  templateUrl: './board-cell.component.html',
  styleUrls: ['./board-cell.component.scss']
})
export class BoardCellComponent implements OnInit {

  @Input()
  row = -1;

  @Input()
  col = -1;

  @Input()
  playerCode = -1; // if a player stands on the cell

  constructor() { }

  ngOnInit(): void {
  }

}
