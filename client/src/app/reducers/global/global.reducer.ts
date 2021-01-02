import {Action, createReducer, on} from '@ngrx/store';
import {EndGame, StartGame} from './global.actions';

export interface GlobalState {
  isGameEnded: boolean;
}

export const initialState: GlobalState = {
  isGameEnded: false
};

const globalReducerAction = createReducer<GlobalState>(initialState,
  on(StartGame, (state) => {
    return {...state, isGameEnded: false};
  }),
  on(EndGame, (state) => {
    return {...state, isGameEnded: true};
  }),
);

export function globalReducer(state: GlobalState | undefined, action: Action): any {
  return globalReducerAction(state, action);
}
