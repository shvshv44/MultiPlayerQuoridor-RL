import {createFeatureSelector, createSelector} from '@ngrx/store';
import {GlobalState} from './global.reducer';
import {selectSelectedPawnName} from '../pawns/pawns.selectors';

export const selectGlobalState = createFeatureSelector<GlobalState>('global');

export const selectIsGameEnded = createSelector(selectGlobalState, (globalState) => globalState.isGameEnded);
export const selectGameId = createSelector(selectGlobalState, (globalState) => globalState.gameId);
export const selectPawnName = createSelector(selectGlobalState, (globalState) => globalState.pawnName);
export const selectIsMyTurn = createSelector(selectGlobalState, selectSelectedPawnName,
  (globalState, pawnName) => globalState.pawnName === pawnName);
export const selectCurrentPlayerMoves = createSelector(selectGlobalState, (globalState) => globalState.currentPlayerMoves);
