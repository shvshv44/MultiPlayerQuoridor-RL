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

  initActiveCells(): void {
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
      // TODO: make the hover trigger
      operation(this.verticalWalls[wallIndex]);
      operation(this.verticalWalls[wallIndex + this.boardRowSize - 1]);
    } else {
      wallIndex = row * (this.boardRowSize) + col;
      operation(this.horizontalWalls[wallIndex]);
      operation(this.horizontalWalls[wallIndex + 1]);
      // TODO: make the hover trigger
    }
  }

  private addHoverToWall(wall: BoardWallComponent): void {
    wall.elementRef.nativeElement.getElementsByClassName('wall')[0].classList.add('wall-hover');
  }

  private deleteHoverToWall(wall: BoardWallComponent): void {
    wall.elementRef.nativeElement.getElementsByClassName('wall')[0].classList.remove('wall-hover');
  }

}
