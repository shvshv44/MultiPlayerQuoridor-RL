import {createAction, props} from '@ngrx/store';
import {Position} from '../../interfaces/position';

export const EndGame = createAction('[Global Component] End Game');
export const StartGame = createAction('[Global Component] Start Game');
export const setPawnName = createAction('[Global Component] Set Pawn Name', props<{ pawnName: string }>());
export const setGameId = createAction('[Global Component] Set Game Id', props<{ gameId: string }>());
export const setCurrentPlayerMoves = createAction('[Global Component] Set Current Player Position', props<{ positions: Position[] }>());
export const clearGlobal = createAction('[Global Component] Clear Global');

