import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Pawn} from '../../interfaces/pawn';
import {Wall} from '../../interfaces/wall';
import {Dictionary} from '@ngrx/entity';
import {Position} from '../../interfaces/position';
import {Direction} from '../../enums/direction';

@Component({
  selector: 'app-board-game',
  templateUrl: './board-game.component.html',
  styleUrls: ['./board-game.component.scss']
})
export class BoardGameComponent implements OnInit {
  @Input() pawns: Pawn[];
  @Input() walls: Dictionary<Wall>;
  @Input() currentPawnMoves: Position[];
  @Input() pawnName: string;
  @Input() isMyTurn: boolean;
  @Output() emitWallClicked: EventEmitter<Wall> = new EventEmitter<Wall>();
  @Output() emitCellClicked: EventEmitter<Direction> = new EventEmitter<Direction>();

  currentHoveredWallId = '';

  readonly boardRowSize = 9;

  constructor() {
  }

  ngOnInit(): void {
  }

  public counter(count: number): any {
    return new Array(count);
  }

  public handleHover(hoveredWallId: string): void {
    this.currentHoveredWallId = hoveredWallId;
  }

  public handleWallClicked(wall: Wall): void {
    this.emitWallClicked.emit(wall);
  }

  public handleCellClicked(direction: Direction): void {
    this.emitCellClicked.emit(direction);
  }
}
