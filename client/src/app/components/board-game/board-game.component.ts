import {Component, OnInit, ViewChildren, QueryList, ElementRef, Input, AfterViewInit} from '@angular/core';
import {BoardCellComponent} from '../board-cell/board-cell.component';
import {BoardWallComponent} from '../board-wall/board-wall.component';

@Component({
  selector: 'app-board-game',
  templateUrl: './board-game.component.html',
  styleUrls: ['./board-game.component.scss']
})
export class BoardGameComponent implements OnInit, AfterViewInit {

  readonly boardRowSize = 9;
  @Input() playerLocations: any[] = [];

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
      this.cells[this.calculateCellIndex(location.row, location.col)].playerCode = currentPlayerCode;
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
      wallIndex = this.calculateWallIndex(row, col, true);
      operation(this.verticalWalls[wallIndex]);
      operation(this.verticalWalls[wallIndex + this.boardRowSize - 1]);
    } else {
      wallIndex = this.calculateWallIndex(row, col, false);
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

  public movePawn(row: number, col: number, playerCode: number): void {
    const prevLocation = this.playerLocations[playerCode - 1];
    const newLocation = {row, col};
    this.cells[this.calculateCellIndex(prevLocation.row, prevLocation.col)].playerCode = 0;
    this.playerLocations[playerCode - 1] = newLocation;
    this.cells[this.calculateCellIndex(newLocation.row, newLocation.col)].playerCode = playerCode;
  }

  public placeWall(row: number, col: number, isVertical: boolean): void {
    const wallIndex = this.calculateWallIndex(row, col, isVertical);

    if (isVertical) {
      this.verticalWalls[wallIndex].isActive = true;
      this.verticalWalls[wallIndex + this.boardRowSize - 1].isActive = true;
    } else {
      this.horizontalWalls[wallIndex].isActive = true;
      this.horizontalWalls[wallIndex + 1].isActive = true;
    }
  }

  private calculateCellIndex(row: number, col: number): number {
    return row * this.boardRowSize + col;
  }

  private calculateWallIndex(row: number, col: number, isVertical: boolean): number {
    let wallIndex = 0;
    if (isVertical) {
      wallIndex = row * (this.boardRowSize - 1) + col;
    } else {
      wallIndex = row * (this.boardRowSize) + col;
    }
    return wallIndex;
  }

}
