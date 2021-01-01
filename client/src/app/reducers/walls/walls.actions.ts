import {createAction, props} from '@ngrx/store';
import {Wall} from '../../interfaces/wall';

export const LoadWalls = createAction('[Walls Component] Load walls');
export const LoadWallsSuccess = createAction('[Walls Component] Load walls success', props<{ walls: Wall[] }>());
export const AddWall = createAction('[Walls Component] Add wall', props<{ wall: Wall }>());
export const AddWallServer = createAction('[Walls Component] Add wall Server', props<{ wall: Wall }>());
export const ClearAllWalls = createAction('[Walls Component] Clear all walls');
