import {Component, EventEmitter, Injectable, Input, OnInit, Output} from '@angular/core';
import {Pawn} from '../../interfaces/pawn';
import {Wall} from '../../interfaces/wall';
import {Dictionary} from '@ngrx/entity';
import {Direction} from '../../enums/direction';

@Component({
  selector: 'app-board-row',
  templateUrl: './board-row.component.html',
  styleUrls: ['./board-row.component.scss']
})


export class BoardRowComponent implements OnInit {
  @Input() rowSize: number;
  @Input() rowIndex: number;
  @Input() pawns: Pawn[];
  @Input() walls: Dictionary<Wall>;
  @Input() hoveredWallId: string;
  @Output() emitHoveredWallId: EventEmitter<string> = new EventEmitter<string>();

  Direction = Direction;

  constructor() {
  }

  ngOnInit(): void {
  }

  public counter(count: number): any {
    return new Array(count);
  }

  public getExistingPawn(x: number, y: number): number {
    for (const indexPawn in this.pawns) {
      if (this.pawns[indexPawn].position.x === x && this.pawns[indexPawn].position.y === y) {
        return +indexPawn + 1;
      }
    }

    return 0;
  }

  public onMouseEnter(wallId: string): void{
    this.emitHoveredWallId.emit(wallId);
  }

  public onMouseLeave(): void{
    this.emitHoveredWallId.emit('');
  }
}
