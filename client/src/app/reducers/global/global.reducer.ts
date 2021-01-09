import {Action, createReducer, on} from '@ngrx/store';
import {clearGlobal, EndGame, setCurrentPlayerMoves, setGameId, setPawnName, StartGame} from './global.actions';
import {Position} from '../../interfaces/position';

export interface GlobalState {
  isGameEnded: boolean;
  currentPlayerMoves: Position[];
  gameId: string;
  pawnName: string;
}

export const initialState: GlobalState = {
  isGameEnded: false,
  currentPlayerMoves: [],
  gameId: '',
  pawnName: ''
};

const globalReducerAction = createReducer<GlobalState>(initialState,
  on(StartGame, (state) => {
    return {...state, isGameEnded: false};
  }),
  on(EndGame, (state) => {
    return {...state, isGameEnded: true};
  }),
  on(setGameId, (state, {gameId}) => {
    return {...state, gameId};
  }),
  on(setPawnName, (state, {pawnName}) => {
    return {...state, pawnName};
  }),
  on(setCurrentPlayerMoves, (state, {positions}) => {
    return {...state, currentPlayerMoves: positions};
  }),
  on(clearGlobal, (state, {}) => {
    return {...state, currentPlayerMoves: [], gameId: '', isGameEnded: false, pawnName: ''};
  })
);

export function globalReducer(state: GlobalState | undefined, action: Action): any {
  return globalReducerAction(state, action);
}
