import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ConfigService} from '../../services/config/config.service';
import {selectPawnArray} from '../../reducers/pawns/pawns.selectors';
import {selectWallsDictionary} from '../../reducers/walls/walls.selectors';
import {Pawn} from '../../interfaces/pawn';
import {Wall} from '../../interfaces/wall';
import {Position} from '../../interfaces/position';
import {Dictionary} from '@ngrx/entity';
import {Observable, of} from 'rxjs';
import {Store} from '@ngrx/store';
import {addPawns, updatePawn} from '../../reducers/pawns/pawns.actions';
import {AddWall} from '../../reducers/walls/walls.actions';
import {Direction} from '../../enums/direction';

@Component({
  selector: 'app-game-history',
  templateUrl: './game-history.component.html',
  styleUrls: ['./game-history.component.scss']
})
export class GameHistoryComponent implements OnInit {
  gamesIds: string[];
  pawns$: Observable<Pawn[]>;
  walls$: Observable<Dictionary<Wall>>;
  currentPawnMoves$: Observable<Position[]>;
  isMyTurn$: Observable<boolean>;
  pawnName$: Observable<string>;
  currGameId: string;

  constructor(private http: HttpClient,
              private config: ConfigService,
              private store: Store) {
    this.pawns$ = this.store.select(selectPawnArray);
    this.walls$ = this.store.select(selectWallsDictionary);
    this.isMyTurn$ = of(false);
    this.currentPawnMoves$ = of([]);
    this.pawnName$ = of('sharon');
    this.http.get(this.config.getConfig().serverUrl + '/HistoryIds').subscribe((gamesIds: { gameId: string }[]) => {
      this.gamesIds = gamesIds.map(gameId => gameId.gameId);
    });
  }

  ngOnInit(): void {
  }

  async getGameAction(gameId: string): Promise<void> {
    this.currGameId = gameId;

    this.http.get(this.config.getConfig().serverUrl + '/History/' + gameId).subscribe((gameActions: any) => {
      const pawns: Pawn[] = gameActions.playOrder.map(player => {
        return {
          position: gameActions.startingPosition[player.uuid], name: player.uuid, numOfWalls: gameActions.startingWallCount
        };
      });

      this.store.dispatch(addPawns({pawns}));

      let i = 0;
      for (const turn of gameActions.history) {
        // SLEEP
        setTimeout(() => this.doAction(turn), i * 100);
        i += 10;
      }
    });
  }

  doAction(turn: any): void {
    if (turn.action.actionType === 'MOVE_PAWN') {
      this.store.dispatch(updatePawn({
        update: {
          id: turn.pawn.uuid,
          changes: {position: turn.details.position}
        }
      }));
    } else {
      const wall = turn.action.wall;
      const newWall: Wall = wall.wallDirection === Direction.Down ? {
        position: {x: wall.position.x + 1, y: wall.position.y},
        wallDirection: Direction.Down
      } : {position: {x: wall.position.x, y: wall.position.y + 1}, wallDirection: Direction.Right};
      this.store.dispatch(AddWall({wall: newWall}));
    }
  }
}
