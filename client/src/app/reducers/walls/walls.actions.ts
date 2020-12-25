import {createAction, props} from '@ngrx/store';
import {Wall} from '../../interfaces/wall';

export const setWalls = createAction('[Walls Component] Set walls', props<{ walls: Wall[] }>());
