import {createAction, props} from '@ngrx/store';
import {Pawn} from '../../interfaces/pawn';
import {Update} from '@ngrx/entity';

export const loadPawns = createAction('[Pawns Component] Load Pawns', props<{ pawns: Pawn[] }>());
export const addPawn = createAction('[Pawns Component] Add Pawn', props<{ pawn: Pawn }>());
export const changePawnPosition = createAction('[Pawns Component] Change Pawn Position', props<{ pawn: Pawn }>());
export const addPawns = createAction('[Pawns Component] Add Pawns', props<{ pawns: Pawn[] }>());
export const updatePawn = createAction('[Pawns Component] Update Pawn', props<{ update: Update<Pawn> }>());
export const clearPawns = createAction('[Pawns Component] Clear Pawns');
