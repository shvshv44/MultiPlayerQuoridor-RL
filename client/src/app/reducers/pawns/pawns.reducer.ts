import {setWalls} from './pawns.actions';
import {Action, createReducer, on, State} from '@ngrx/store';
import {Wall} from '../../interfaces/wall';
import {Position} from "../../interfaces/position";

export const pawns: Position[] = [];

const wallsReducerAction = createReducer(
  pawns,
  on(setWalls, (state: Position[], {pawns}) => ([...pawns])),
);

export function wallsReducer(state: Position[], action: Action): any {
  return wallsReducerAction(state, action);
}
