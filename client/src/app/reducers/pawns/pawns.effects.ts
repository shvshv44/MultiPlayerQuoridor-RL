import {Actions, createEffect, ofType} from '@ngrx/effects';
import {switchMap} from 'rxjs/operators';
import {WebSocketApiService} from '../../services/web-socket-api/web-socket-api.service';
import {from} from 'rxjs';
import {changePawnPosition} from './pawns.actions';
import {Injectable} from '@angular/core';

@Injectable()
export class PawnsEffects {

  loadWalls$ = createEffect(() => this.actions$.pipe(
    ofType(changePawnPosition),
    switchMap((action) => {
      this.webSocketApiService._sendPawnMovement(action.direction);
      return from([]);
    })), {dispatch: false});

  constructor(private actions$: Actions, private webSocketApiService: WebSocketApiService) {
  }
}
