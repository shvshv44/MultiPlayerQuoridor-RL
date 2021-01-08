import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Pawn} from '../../interfaces/pawn';
import {Wall} from '../../interfaces/wall';
import {Dictionary} from '@ngrx/entity';
import {Direction} from '../../enums/direction';
import {Position} from '../../interfaces/position';

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
  @Input() currentPawnMoves: Position[];
  @Input() pawnName: string;
  @Input() isMyTurn: boolean;
  @Output() emitHoveredWallId: EventEmitter<string> = new EventEmitter<string>();
  @Output() emitWallClicked: EventEmitter<Wall> = new EventEmitter<Wall>();
  @Output() emitCellClicked: EventEmitter<Direction> = new EventEmitter<Direction>();

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

  public isLegalPosition(x: number, y: number): boolean {
    for (const position of this.currentPawnMoves) {
      if (position.x === x && position.y === y) {
        return true;
      }
    }

    return false;
  }

  public onMouseEnter(x: number, y: number, direction: Direction): void {
    const directionNeedToCheck: Direction = direction === Direction.Down ? Direction.Right : Direction.Down;
    if (direction === Direction.Right && x !== this.rowSize - 1) {
      this.emitHoveredWallId.emit(x + '_' + y + '_' + direction);
    }

    if (direction === Direction.Down && y !== this.rowSize - 1) {
      this.emitHoveredWallId.emit(x + '_' + y + '_' + direction);
    }
  }

  public onMouseLeave(): void {
    this.emitHoveredWallId.emit('');
  }

  public onCellClicked(x: number, y: number): void {
    // @ts-ignore
    const currPawnPosition: Position = this.pawns.find(pawn => pawn.name === this.pawnName).position;
    for (const position of this.currentPawnMoves) {
      if (position.x === x && position.y === y) {
        if (x > currPawnPosition.x) {
          this.emitCellClicked.emit(Direction.Right);
        } else if (x < currPawnPosition.x) {
          this.emitCellClicked.emit(Direction.Left);
        } else if (y > currPawnPosition.y) {
          this.emitCellClicked.emit(Direction.Down);
        } else if (y < currPawnPosition.y) {
          this.emitCellClicked.emit(Direction.Up);
        }
      }
    }
  }

  public onWallClicked(x: number, y: number, direction: Direction): void {
    const directionNeedToCheck: Direction = direction === Direction.Down ? Direction.Right : Direction.Down;
    if (direction === Direction.Right && x !== this.rowSize - 1) {
      this.emitWallClicked.emit({position: {x, y}, wallDirection: direction});
    }

    if (direction === Direction.Down && y !== this.rowSize - 1) {
      this.emitWallClicked.emit({position: {x, y}, wallDirection: direction});
    }
  }
}
