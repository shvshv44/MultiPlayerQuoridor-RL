import {createAction, props} from '@ngrx/store';
import {Position} from '../../interfaces/position';
import {Pawn} from '../../interfaces/pawn';
import {EntityMap, EntityMapOne, Predicate, Update} from '@ngrx/entity';

export const setPawns = createAction('[Pawns Component] Set pawns', props<{ pawns: Map<string, Position> }>());
export const changePawnPosition = createAction('[Pawns Component] Change Pawn Position ', props<{ name: string, position: Position }>());

export const loadPawns = createAction('[Pawns Component] Load Pawns', props<{ Pawns: Pawn[] }>());
export const addPawn = createAction('[Pawns Component] Add Pawn', props<{ Pawn: Pawn }>());
export const setPawn = createAction('[Pawns Component] Set Pawn', props<{ Pawn: Pawn }>());
export const upsertPawn = createAction('[Pawns Component] Upsert Pawn', props<{ Pawn: Pawn }>());
export const addPawns = createAction('[Pawns Component] Add Pawns', props<{ Pawns: Pawn[] }>());
export const upsertPawns = createAction('[Pawns Component] Upsert Pawns', props<{ Pawns: Pawn[] }>());
export const updatePawn = createAction('[Pawns Component] Update Pawn', props<{ update: Update<Pawn> }>());
export const updatePawns = createAction('[Pawns Component] Update Pawns', props<{ updates: Update<Pawn>[] }>());
export const mapPawn = createAction('[Pawns Component] Map Pawn', props<{ entityMap: EntityMapOne<Pawn> }>());
export const mapPawns = createAction('[Pawns Component] Map Pawns', props<{ entityMap: EntityMap<Pawn> }>());
export const deletePawn = createAction('[Pawns Component] Delete Pawn', props<{ id: string }>());
export const deletePawns = createAction('[Pawns Component] Delete Pawns', props<{ ids: string[] }>());
export const deletePawnsByPredicate = createAction('[Pawns Component] Delete Pawns By Predicate', props<{ predicate: Predicate<Pawn> }>());
export const clearPawns = createAction('[Pawns Component] Clear Pawns');
