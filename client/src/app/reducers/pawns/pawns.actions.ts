import {createAction, props} from '@ngrx/store';
import {Position} from '../../interfaces/position';

export const setPawns = createAction('[Pawns Component] Set pawns', props<{ pawns: Position[] }>());
