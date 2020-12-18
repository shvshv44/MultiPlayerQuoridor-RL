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
  playerLocations: any[] = [];


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
    const endOfTheBoard = this.boardRowSize - 1;
    this.playerLocations.push({row: halfOfABoard,   col: endOfTheBoard}); // right
    this.playerLocations.push({row: 0,              col: halfOfABoard});  // up
    this.playerLocations.push({row: halfOfABoard,   col: 0});             // left
    this.playerLocations.push({row: endOfTheBoard,  col: halfOfABoard});  // down

    let currentPlayerCode = 1;
    for (const location of this.playerLocations) {
      this.cells[location.row * this.boardRowSize + location.col].playerCode = currentPlayerCode;
      currentPlayerCode++;
    }
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

  playerAskForWall(row: number, col: number, isVertical: boolean): void {
    // TODO: send a request to the server using web socket service!
    console.log('Player ask for wall ' + row + ' ' + col + ' in direction: ' + ((isVertical) ? 'vertical' : 'horizontal'));
  }

  playerAskToMove(row: number, col: number): void {
    // TODO: send a request to the server using web socket service!
    console.log('Player ask to move ' + row + ' ' + col + ' ');
  }

}
