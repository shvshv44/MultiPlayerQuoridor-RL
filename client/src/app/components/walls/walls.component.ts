import {Component, Input, OnInit} from '@angular/core';
import {Pawn} from '../../interfaces/pawn';

@Component({
  selector: 'app-walls',
  templateUrl: './walls.component.html',
  styleUrls: ['./walls.component.scss']
})
export class WallsComponent implements OnInit {
  @Input() pawns: Pawn[];

  constructor() { }

  ngOnInit(): void {
    console.log(this.pawns);
  }

}
