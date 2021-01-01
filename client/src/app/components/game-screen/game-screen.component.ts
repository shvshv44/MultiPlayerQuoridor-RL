import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {selectPawnArray} from '../../reducers/pawns/pawns.selectors';
import {Observable} from 'rxjs';
import {Pawn} from '../../interfaces/pawn';
import {Wall} from '../../interfaces/wall';
import {selectWallsArray, selectWallsDictionary} from '../../reducers/walls/walls.selectors';
import {addPawn} from '../../reducers/pawns/pawns.actions';
import {AddWall} from '../../reducers/walls/walls.actions';
import {Direction} from '../../enums/direction';
import {Dictionary} from '@ngrx/entity';

// @ts-ignore
@Component({
  selector: 'app-game-screen',
  templateUrl: './game-screen.component.html',
  styleUrls: ['./game-screen.component.scss']
})
export class GameScreenComponent implements OnInit {
  pawns$: Observable<Pawn[]>;
  walls$: Observable<Dictionary<Wall>>;

  constructor(private store: Store) {
    this.pawns$ = this.store.select(selectPawnArray);
    this.walls$ = this.store.select(selectWallsDictionary);
    this.store.dispatch(addPawn({
      pawn: {
        name: 'sharon',
        position: {
          x: 0, y: 0
        }
      }
    }));

    this.store.dispatch(addPawn({
      pawn: {
        name: 'tamir',
        position: {
          x: 5, y: 5
        }
      }
    }));


    this.store.dispatch(AddWall({
      wall: {
        direction: Direction.Right, position: {
          x: 0, y: 0
        }
      }
    }));
  }

  ngOnInit(): void {
  }

}
