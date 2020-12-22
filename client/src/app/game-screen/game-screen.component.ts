import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {setWalls} from '../reducers/walls/walls.actions';
import {Direction} from '../enums/direction';
import {Wall} from "../interfaces/wall";

// @ts-ignore
@Component({
  selector: 'app-game-screen',
  templateUrl: './game-screen.component.html',
  styleUrls: ['./game-screen.component.scss']
})
export class GameScreenComponent implements OnInit {

  constructor(private readonly store: Store) {

    // For Guy use this syntax to init the new walls - examples:
    this.store.dispatch(setWalls({walls: []}));
    this.store.dispatch(setWalls({walls: [{position: {x: 5, y: 7}, direction: Direction.Down}]}));

    // For Shaked take the walls like this
    // @ts-ignore
    this.store.select(state => state.walls).subscribe(value => console.log(value, '----walls----'));
  }

  ngOnInit(): void {
  }

}
