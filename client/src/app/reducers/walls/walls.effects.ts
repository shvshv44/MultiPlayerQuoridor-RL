import {Actions, createEffect, ofType} from '@ngrx/effects';
import {AddWallServer} from './walls.actions';
import {switchMap} from 'rxjs/operators';
import {WebSocketApiService} from '../../services/web-socket-api/web-socket-api.service';
import {from} from 'rxjs';

export class WallsEffects {

  loadWalls$ = createEffect(() => this.actions$.pipe(
    ofType(AddWallServer),
    switchMap((action) => {
      this.webSocketApiService._sendPutWall(action.wall);
      return from([]);
    })), {dispatch: false});

  constructor(private actions$: Actions, private webSocketApiService: WebSocketApiService) {
  }
}
