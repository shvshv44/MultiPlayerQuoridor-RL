import {Actions} from '@ngrx/effects';
import {WebSocketApiService} from '../../services/web-socket-api/web-socket-api.service';
import {Injectable} from '@angular/core';

@Injectable()
export class GlobalEffects {

  constructor(private actions$: Actions, private webSocketApiService: WebSocketApiService) {
  }
}
