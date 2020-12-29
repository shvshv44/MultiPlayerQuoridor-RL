import {Action, createReducer, on, State} from '@ngrx/store';
import {Position} from '../../interfaces/position';
import {setPawns} from './pawns.actions';

export const pawns: Map<string, Position[]> = new Map<string, Position[]>();

const pawnsReducerAction = createReducer(
  pawns,
  on(setPawns, (state: Position[], {pawns}) => ()),
);

export function pawnsReducer(state: Position[], action: Action): any {
  return pawnsReducerAction(state, action);
}

export function initPawns()
