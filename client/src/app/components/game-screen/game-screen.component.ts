import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {selectPawnArray, selectSelectedPawnName} from '../../reducers/pawns/pawns.selectors';
import {Observable} from 'rxjs';
import {Pawn} from '../../interfaces/pawn';
import {Wall} from '../../interfaces/wall';
import {selectWallsDictionary} from '../../reducers/walls/walls.selectors';
import {changePawnPosition, clearPawns, setSelectedPawn, updatePawn} from '../../reducers/pawns/pawns.actions';
import {AddWall, AddWallServer, ClearAllWalls} from '../../reducers/walls/walls.actions';
import {Direction} from '../../enums/direction';
import {Dictionary} from '@ngrx/entity';
import {Position} from '../../interfaces/position';
import {selectCurrentPlayerMoves, selectIsMyTurn, selectPawnName} from '../../reducers/global/global.selectors';
import {clearGlobal, setCurrentPlayerMoves} from '../../reducers/global/global.actions';
import {MessageHandlerService} from '../../services/message-handler/message-handler.service';
import {WebSocketMessageType} from '../../enums/web-socket-message-type.enum';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import {Router} from '@angular/router';

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
              private msgHandler: MessageHandlerService,
              private router: Router) {
    this.pawns$ = this.store.select(selectPawnArray);
    this.store.select(selectPawnArray).subscribe(v => console.log(v));
    this.walls$ = this.store.select(selectWallsDictionary);
    this.isMyTurn$ = this.store.select(selectIsMyTurn);
    this.currentPawnMoves$ = this.store.select(selectCurrentPlayerMoves);
    this.pawnName$ = this.store.select(selectSelectedPawnName);

    this.msgHandler.assignHandler(WebSocketMessageType.NewTurnEvent, (message => {
      this.store.dispatch(setSelectedPawn({pawnName: message.nextPlayerToPlay}));
      this.store.dispatch(setCurrentPlayerMoves({positions: message.avialiableMoves}));
    }));

    this.msgHandler.assignHandler(WebSocketMessageType.EndTurnEvent, (message => {
      if (message.currentTurnMove.type === 'MovePawn') {
        this.store.dispatch(updatePawn({
          update: {
            id: message.playerPlayed,
            changes: {position: message.currentTurnMove.pawnPosition}
          }
        }));
      } else {
        const wall: Wall = message.currentTurnMove.wall;
        const newWall: Wall = wall.wallDirection === Direction.Down ? {
          position: {x: wall.position.x + 1, y: wall.position.y},
          wallDirection: Direction.Down
        } : {position: {x: wall.position.x, y: wall.position.y + 1}, wallDirection: Direction.Right};
        this.store.dispatch(AddWall({wall: newWall}));
        this.store.dispatch(updatePawn({
          update: {
            id: message.playerPlayed,
            changes: {numOfWalls: message.currentTurnMove.numOfWalls}
          }
        }));
      }
    }));

    this.msgHandler.assignHandler(WebSocketMessageType.GameOverEvent, (message => {
      const subscribeEndGame: any = this.store.select(selectPawnName).subscribe(pawnName => {
        if (pawnName !== '') {
          if (pawnName === message.winnerName) {
            Swal.fire({
              position: 'center',
              icon: 'success',
              title: `YOU ARE THE WINNER !`,
              showConfirmButton: true,
              timer: 5000,
              showCancelButton: false,
              confirmButtonColor: '#5ed659',
              confirmButtonText: 'winner'
            });
          } else {
            Swal.fire({
              position: 'center',
              icon: 'error',
              title: `The winner is - ${message.winnerName}`,
              showConfirmButton: true,
              timer: 5000,
              showCancelButton: false,
              confirmButtonColor: '#5ed659',
              confirmButtonText: 'loser'
            });
          }

          this.store.dispatch(ClearAllWalls());
          this.store.dispatch(clearPawns());
          this.store.dispatch(clearGlobal());
          router.navigateByUrl('/menu');
        }
      });

      subscribeEndGame.unsubscribe();
    }));
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
