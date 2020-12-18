import {Component, OnInit, ViewChildren, QueryList, ElementRef} from '@angular/core';
import {BoardCellComponent} from '../board-cell/board-cell.component';
import {BoardWallComponent} from '../board-wall/board-wall.component';

@Component({
  selector: 'app-board-game',
  templateUrl: './board-game.component.html',
  styleUrls: ['./board-game.component.scss']
})
export class BoardGameComponent implements OnInit {

  readonly boardRowSize = 9;

  @ViewChildren(BoardCellComponent) cellsAsList: QueryList<BoardCellComponent> = new QueryList<BoardCellComponent>();
  @ViewChildren(BoardWallComponent) wallsAsList: QueryList<BoardWallComponent> = new QueryList<BoardWallComponent>();

  activeCells: boolean[][] = [];
  cells: BoardCellComponent[] = [];
  horizontalWalls: BoardWallComponent[] = [];
  verticalWalls: BoardWallComponent[] = [];


  constructor() {
    this.initActiveCells();
  }

  ngOnInit(): void {
  }

  // tslint:disable-next-line:use-lifecycle-interface
  ngAfterViewInit(): void {
    this.createCellsAndWallsArrays();
    this.create4PlayersAtStart();
  }

  private create4PlayersAtStart(): void {
    const halfOfABoard = Math.round(this.boardRowSize / 2) - 1;
    const rightMiddleIndex  =   (halfOfABoard * this.boardRowSize) + (this.boardRowSize - 1);
    const upMiddleIndex     =   halfOfABoard;
    const leftMiddleIndex   =   halfOfABoard * this.boardRowSize;
    const downMiddleIndex   =   ((this.boardRowSize - 1) * this.boardRowSize) + halfOfABoard;
    this.cells[rightMiddleIndex].playerCode = 1;
    this.cells[upMiddleIndex].playerCode = 2;
    this.cells[leftMiddleIndex].playerCode = 3;
    this.cells[downMiddleIndex].playerCode = 4;
  }

  private createCellsAndWallsArrays(): void {
    const allWallsArray = this.wallsAsList.toArray();
    this.cells = this.cellsAsList.toArray();
    for (const wall of allWallsArray) {
      if (wall.isVertical) {
        this.verticalWalls.push(wall);
      } else {
        this.horizontalWalls.push(wall);
      }
    }
  }

  private initActiveCells(): void {
    for (let i = 0; i < 9; i++) {
      this.activeCells.push([]);
      for (let j = 0; j < 9; j++) {
        this.activeCells[i].push(false);
      }
    }
  }

  mouseHoverWallEvent(event: any, row: number, col: number): void {
    this.mouseWallEvent(event, row, col, this.addHoverToWall);
  }

  mouseOutWallEvent(event: any, row: number, col: number): void {
    this.mouseWallEvent(event, row, col, this.deleteHoverToWall);
  }

  mouseWallEvent(event: any, row: number, col: number, operation: (wall: BoardWallComponent) => void): void {
    let wallIndex = 0;
    if (event.target.classList.contains('vertical')) {
      wallIndex = row * (this.boardRowSize - 1) + col;
      operation(this.verticalWalls[wallIndex]);
      operation(this.verticalWalls[wallIndex + this.boardRowSize - 1]);
    } else {
      wallIndex = row * (this.boardRowSize) + col;
      operation(this.horizontalWalls[wallIndex]);
      operation(this.horizontalWalls[wallIndex + 1]);
    }
  }

  private addHoverToWall(wall: BoardWallComponent): void {
    wall.elementRef.nativeElement.getElementsByClassName('wall')[0].classList.add('wall-hover');
  }

  private deleteHoverToWall(wall: BoardWallComponent): void {
    wall.elementRef.nativeElement.getElementsByClassName('wall')[0].classList.remove('wall-hover');
  }

}
