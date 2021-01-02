import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
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
  @Output() emitWallClicked: EventEmitter<Wall> = new EventEmitter<Wall>();

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

  public onMouseEnter(x: number, y: number, direction: Direction): void {
    const directionNeedToCheck: Direction = direction === Direction.Down ? Direction.Right : Direction.Down;
    if (!this.walls[x + '_' + y + '_' + directionNeedToCheck] && x < this.rowSize - 1 && y < this.rowSize - 1) {
      this.emitHoveredWallId.emit(x + '_' + y + '_' + direction);
    }
  }

  public onMouseLeave(): void {
    this.emitHoveredWallId.emit('');
  }

  public onWallClicked(x: number, y: number, direction: Direction): void {
    this.emitWallClicked.emit({position: {x, y}, direction});
  }
}
