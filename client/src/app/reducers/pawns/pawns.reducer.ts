import {Action, createReducer, on} from '@ngrx/store';
import {addPawn, addPawns, clearPawns, loadPawns, setSelectedPawn, updatePawn,} from './pawns.actions';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Pawn} from '../../interfaces/pawn';

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
  }), on(setSelectedPawn, (state, {pawnName}) => {
    return {...state, selectedPawnName: pawnName};
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
