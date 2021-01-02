import {createFeatureSelector, createSelector} from '@ngrx/store';
import {GlobalState} from './global.reducer';

export const selectGlobalState = createFeatureSelector<GlobalState>('global');

export const selectIsGameEnded = createSelector(selectGlobalState, (globalState) => globalState.isGameEnded);
