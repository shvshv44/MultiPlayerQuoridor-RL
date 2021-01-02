import {Injectable} from '@angular/core';
import {WebSocketMessageType} from '../../enums/web-socket-message-type.enum';
import {Store} from '@ngrx/store';
import {setSelectedPawn, updatePawn} from '../../reducers/pawns/pawns.actions';
import {selectSelectedPawnName} from '../../reducers/pawns/pawns.selectors';
import {EndGame, setCurrentPlayerMoves} from '../../reducers/global/global.actions';
import {AddWall} from '../../reducers/walls/walls.actions';

@Injectable({
  providedIn: 'root'
})
export class MessageHandlerService {
  handlers = new Map<WebSocketMessageType, (message: any) => void>();

  constructor(private store: Store) {
    this.assignHandler(WebSocketMessageType.EndTurn, this.endTurn);
  }

  assignHandler(type: WebSocketMessageType, handler: (message: any) => void): void {
    this.handlers.set(type, handler);
  }

  // tslint:disable-next-line:typedef
  handleMessage(message: any) {
    // tslint:disable-next-line:triple-equals
    if (message.type != null && this.handlers.has(message.type)) {
      console.log('handle message of type ' + message.type);
      // @ts-ignore
      (this.handlers.get(message.type))(message);
    }
  }

  endTurn(message: any): void {
    if (message.position) {
      this.store.select(selectSelectedPawnName).subscribe(selectedPawnName => {
        this.store.dispatch(updatePawn({update: {id: selectedPawnName, changes: message.position}}));
      });
    } else {
      this.store.dispatch(AddWall({wall: message.wall}));
    }
    this.store.dispatch(setCurrentPlayerMoves({positions: message.currentPlayerMoves}));
    this.store.dispatch(setSelectedPawn({pawnName: message.nextPlayerTurn}));
    if (message.isGameEnded) {
      this.store.dispatch(EndGame());
    }
  }
}
