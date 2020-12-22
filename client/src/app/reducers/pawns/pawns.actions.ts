import {createAction, props} from '@ngrx/store';
import {Position} from '../../interfaces/position';

export const setWalls = createAction('[Walls Component] Set walls', props<{ pawns: Position[] }>());
