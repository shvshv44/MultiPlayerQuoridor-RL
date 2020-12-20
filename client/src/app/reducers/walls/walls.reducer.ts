import {setWalls} from './walls.actions';
import {Action, createReducer, on, State} from '@ngrx/store';
import {Wall} from '../../interfaces/wall';

export const initialState: Wall[] = [];

const wallsReducerAction = createReducer(
  initialState,
  on(setWalls, (state: Wall[], {walls}) => ([...walls])),
);

export function wallsReducer(state: Wall[], action: Action): any {
  return wallsReducerAction(state, action);
}
