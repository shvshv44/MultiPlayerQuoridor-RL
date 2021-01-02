import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Wall} from '../../interfaces/wall';
import {Action, createReducer, on} from '@ngrx/store';
import {AddWall, ClearAllWalls, LoadWallsSuccess} from './walls.actions';

export interface WallsState extends EntityState<Wall>{
}

export function selectWallId(wall: Wall): string {
  return `${wall.position.x}_${wall.position.y}_${wall.direction}`;
}

export const wallEntityAdapter: EntityAdapter<Wall> = createEntityAdapter<Wall>({
  selectId: selectWallId,
  sortComparer: false,
});

export const initialState: WallsState = wallEntityAdapter.getInitialState();

const wallsReducerAction = createReducer<WallsState>(initialState,
  on(AddWall, (state, action) => {
    return wallEntityAdapter.addOne(action.wall, state);
  }),
  on(LoadWallsSuccess, (state, action) => {
    return wallEntityAdapter.setAll(action.walls, state);
  }),
  on(ClearAllWalls, (state) => {
    return wallEntityAdapter.removeAll(state);
  }));

export function wallsReducer(state: WallsState | undefined, action: Action): any {
  return wallsReducerAction(state, action);
}
