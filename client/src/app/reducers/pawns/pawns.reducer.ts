import {Action, createReducer, on} from '@ngrx/store';
import {Position} from '../../interfaces/position';
import {
  addPawn,
  addPawns,
  clearPawns,
  deletePawn,
  deletePawns,
  deletePawnsByPredicate,
  loadPawns,
  mapPawn,
  mapPawns,
  setPawn,
  updatePawn,
  updatePawns,
  upsertPawn,
  upsertPawns
} from './pawns.actions';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Pawn} from '../../interfaces/pawn';


/*
export const pawns: Map<string, Position> = new Map();

const pawnsReducerAction = createReducer(
  pawns,
  on(setPawns, (state: Position[], {pawns}) => ([...pawns])),
);

export function pawnsReducer(state: Position[], action: Action): any {
  return pawnsReducerAction(state, action);
}
*/

export interface PawnsState extends EntityState<Pawn> {
  // additional entities state properties
  selectedPawnName: string;
}

export function selectPawnName(a: Pawn): string {
  return a.name;
}

export const adapter: EntityAdapter<Pawn> = createEntityAdapter<Pawn>({
  selectId: selectPawnName,
  sortComparer: false,
});

export const initialState: PawnsState = adapter.getInitialState({
  // additional entity state properties
  selectedPawnName: '',
});

const pawnsReducerAction = createReducer(initialState,
  // tslint:disable-next-line:no-shadowed-variable
  on(addPawn, (state, {Pawn}) => {
    return adapter.addOne(Pawn, state);
  }),
  // tslint:disable-next-line:no-shadowed-variable
  on(setPawn, (state, {Pawn}) => {
    return adapter.setOne(Pawn, state);
  }),
  // tslint:disable-next-line:no-shadowed-variable
  on(upsertPawn, (state, {Pawn}) => {
    return adapter.upsertOne(Pawn, state);
  }),
  on(addPawns, (state, {Pawns}) => {
    return adapter.addMany(Pawns, state);
  }),
  on(upsertPawns, (state, {Pawns}) => {
    return adapter.upsertMany(Pawns, state);
  }),
  on(updatePawn, (state, {update}) => {
    return adapter.updateOne(update, state);
  }),
  on(updatePawns, (state, {updates}) => {
    return adapter.updateMany(updates, state);
  }),
  on(mapPawn, (state, {entityMap}) => {
    // @ts-ignore
    return adapter.map(entityMap, state);
  }),
  on(mapPawns, (state, {entityMap}) => {
    return adapter.map(entityMap, state);
  }),
  on(deletePawn, (state, {id}) => {
    return adapter.removeOne(id, state);
  }),
  on(deletePawns, (state, {ids}) => {
    return adapter.removeMany(ids, state);
  }),
  on(deletePawnsByPredicate, (state, {predicate}) => {
    return adapter.removeMany(predicate, state);
  }),
  on(loadPawns, (state, {Pawns}) => {
    return adapter.setAll(Pawns, state);
  }),
  on(clearPawns, state => {
    return adapter.removeAll({...state, selectedPawnId: null});
  }));

export function pawnsReducer(state: PawnsState | undefined, action: Action): any {
  return pawnsReducerAction(state, action);
}
