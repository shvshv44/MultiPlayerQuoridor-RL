import {Action, createReducer, on} from '@ngrx/store';
import {addPawn, addPawns, clearPawns, loadPawns, updatePawn,} from './pawns.actions';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Pawn} from '../../interfaces/pawn';


/*
export const pawns: Map<string, Position> = new Map();

const pawnsReducerAction = createReducer(
  pawns,
  on(setPawns, (state: Position[], {pawns}) => ([...pawns])),
);

export function isGameEndedReducer(state: Position[], action: Action): any {
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
  on(addPawn, (state, {pawn}) => {
    return adapter.addOne(pawn, state);
  }),
  on(addPawns, (state, {pawns}) => {
    return adapter.addMany(pawns, state);
  }),
  on(updatePawn, (state, {update}) => {
    return adapter.updateOne(update, state);
  }),
  on(loadPawns, (state, {pawns}) => {
    return adapter.setAll(pawns, state);
  }),
  on(clearPawns, state => {
    return adapter.removeAll({...state, selectedPawnId: null});
  }));

export function pawnsReducer(state: PawnsState | undefined, action: Action): any {
  return pawnsReducerAction(state, action);
}
