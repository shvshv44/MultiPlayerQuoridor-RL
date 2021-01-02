import {createAction, props} from '@ngrx/store';
import {Pawn} from '../../interfaces/pawn';
import {Update} from '@ngrx/entity';

export const EndGame = createAction('[Global Component] End Game');
export const StartGame = createAction('[Global Component] Start Game');

