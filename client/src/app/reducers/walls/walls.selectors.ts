import {createFeatureSelector, createSelector} from '@ngrx/store';
import {wallEntityAdapter, WallsState} from './walls.reducer';

const {
  selectEntities,
  selectAll
} = wallEntityAdapter.getSelectors();

export const selectWallsState = createFeatureSelector<WallsState>('walls');
export const selectWallsArray = createSelector(selectWallsState, selectAll);
export const selectWallsDictionary = createSelector(selectWallsState, selectEntities);
