import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {selectPawnArray, selectSelectedPawnName} from '../../reducers/pawns/pawns.selectors';
import {Observable} from 'rxjs';
import {Pawn} from '../../interfaces/pawn';
import {Wall} from '../../interfaces/wall';
import {selectWallsDictionary} from '../../reducers/walls/walls.selectors';
import {addPawn, changePawnPosition, setSelectedPawn, updatePawn} from '../../reducers/pawns/pawns.actions';
import {AddWall, AddWallServer} from '../../reducers/walls/walls.actions';
import {Direction} from '../../enums/direction';
import {Dictionary} from '@ngrx/entity';
import {Position} from '../../interfaces/position';
import {selectCurrentPlayerMoves, selectIsMyTurn} from '../../reducers/global/global.selectors';
import {setCurrentPlayerMoves, setPawnName} from '../../reducers/global/global.actions';
import {MessageHandlerService} from '../../services/message-handler/message-handler.service';
import {WebSocketMessageType} from '../../enums/web-socket-message-type.enum';
import {first} from 'rxjs/operators';

// @ts-ignore
@Component({
  selector: 'app-game-screen',
  templateUrl: './game-screen.component.html',
  styleUrls: ['./game-screen.component.scss']
})
export class GameScreenComponent implements OnInit {
  pawns$: Observable<Pawn[]>;
  walls$: Observable<Dictionary<Wall>>;
  currentPawnMoves$: Observable<Position[]>;
  isMyTurn$: Observable<boolean>;
  pawnName$: Observable<string>;

  constructor(private store: Store,
              private msgHandler: MessageHandlerService) {
    this.pawns$ = this.store.select(selectPawnArray);
    this.store.select(selectPawnArray).subscribe(v => console.log(v));
    this.walls$ = this.store.select(selectWallsDictionary);
    this.isMyTurn$ = this.store.select(selectIsMyTurn);
    this.currentPawnMoves$ = this.store.select(selectCurrentPlayerMoves);
    this.pawnName$ = this.store.select(selectSelectedPawnName);

    this.msgHandler.assignHandler(WebSocketMessageType.NewTurnEvent, (message => {
      this.store.dispatch(setSelectedPawn({pawnName: message.nextPlayerToPlay}));
      this.store.dispatch(setCurrentPlayerMoves({positions: message.avialiableMoves}));
    }) );

    this.msgHandler.assignHandler(WebSocketMessageType.EndTurnEvent, (message => {
      if (message.currentTurnMove.type === 'MovePawn') {
        this.store.dispatch(updatePawn({update: {id: message.playerPlayed, changes: {position: message.currentTurnMove.pawnPosition }}}));
      } else {
        this.store.dispatch(AddWall({wall: message.currentTurnMove.wall}));
      }

    }) );

    // // TODO: need to delete after getting real data
    // this.store.dispatch(addPawn({
    //   pawn: {
    //     name: 's',
    //     position: {
    //       x: 0, y: 0
    //     }
    //   }
    // }));
    //
    // this.store.dispatch(setSelectedPawn({
    //   pawnName: 's'
    // }));
    //
    // this.store.dispatch(addPawn({
    //   pawn: {
    //     name: 'tamir',
    //     position: {
    //       x: 5, y: 5
    //     }
    //   }
    // }));
    //
    //
    // this.store.dispatch(AddWall({
    //   wall: {
    //     direction: Direction.Right, position: {
    //       x: 0, y: 0
    //     }
    //   }
    // }));
    //
    // this.store.dispatch(setCurrentPlayerMoves({
    //   positions: [{
    //     x: 0, y: 1
    //   }]
    // }));
    //
    // this.store.dispatch(setPawnName({
    //   pawnName: 's'
    // }));
  }

  ngOnInit(): void {
  }

  public handleWallClicked(wall: Wall): void {
    this.store.dispatch(AddWallServer({wall}));
  }

  public handleCellClicked(direction: Direction): void {
    this.store.dispatch(changePawnPosition({direction}));
  }
}
