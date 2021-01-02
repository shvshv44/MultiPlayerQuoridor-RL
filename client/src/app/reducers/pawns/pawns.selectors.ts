import {adapter, PawnsState} from './pawns.reducer';
import {createFeatureSelector, createSelector} from '@ngrx/store';

export const getSelectedUserId = (state: PawnsState) => state.selectedPawnName;

// get the selectors
const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = adapter.getSelectors();

// select the array of user ids
export const selectUserIds = selectIds;

// select the dictionary of user entities
export const selectUserEntities = selectEntities;

// select the array of users
export const selectAllUsers = selectAll;

// select the total user count
export const selectUserTotal = selectTotal;

export const selectPawnState = createFeatureSelector<PawnsState>('pawns');

export const selectPawnArray = createSelector(selectPawnState, selectAll);

export const selectSelectedPawnName = createSelector(selectPawnState, (pawnState) => pawnState.selectedPawnName);
