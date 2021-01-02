import {createAction, props} from '@ngrx/store';
import {Pawn} from '../../interfaces/pawn';
import {Update} from '@ngrx/entity';
import {Direction} from '../../enums/direction';

export const loadPawns = createAction('[Pawns Component] Load Pawns', props<{ pawns: Pawn[] }>());
export const addPawn = createAction('[Pawns Component] Add Pawn', props<{ pawn: Pawn }>());
export const addPawns = createAction('[Pawns Component] Add Pawns', props<{ pawns: Pawn[] }>());
export const setSelectedPawn = createAction('[Pawns Component] Set Selected Pawn', props<{ pawnName: string }>());
export const changePawnPosition = createAction('[Pawns Component] Change Pawn Position', props<{ direction: Direction }>());
export const updatePawn = createAction('[Pawns Component] Update Pawn', props<{ update: Update<Pawn> }>());
export const clearPawns = createAction('[Pawns Component] Clear Pawns');

