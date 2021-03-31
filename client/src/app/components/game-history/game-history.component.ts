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
    const mockGames = [
      {
        gameId: 'fb854e01-75f4-47df-89dd-fc4c2dae9ca7'
      },
      {
        gameId: 'f805e54f-964d-4c78-a57d-0719aab55a74'
      }
    ];
    this.gamesIds = mockGames.map(gameId => gameId.gameId);

    /* this.http.get(this.config.getConfig().serverUrl + '/HistoryIds').subscribe((gamesIds: { gameId: string }[]) => {
       this.gamesIds = gamesIds.map(gameId => gameId.gameId);
     });*/
  }

  ngOnInit(): void {
  }

  async getGameAction(gameId: string): Promise<void> {
    this.currGameId = gameId;

    /* this.http.get(this.config.getConfig().serverUrl + '/History' + gameId).subscribe((gamesIds: { gameId: string }[]) => {
       // NEED TO TAKE FROM DOWN
     });*/

    const gameActions: any = {
      gameId: 'f805e54f-964d-4c78-a57d-0719aab55a74',
      winner: {
        uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
      },
      history: [
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 1,
              x: 4
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 7,
              x: 4
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 2,
              x: 4
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 6,
              x: 4
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            wall: {
              position: {
                y: 4,
                x: 4
              },
              wallDirection: 'Right'
            },
            actionType: 'PLACE_WALL'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 5,
              x: 4
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 3,
              x: 4
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 4,
              x: 4
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 3,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 4,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 5,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 3,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 6,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 2,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 7,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
          },
          action: {
            position: {
              y: 1,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        },
        {
          pawn: {
            uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
          },
          action: {
            position: {
              y: 8,
              x: 3
            },
            actionType: 'MOVE_PAWN'
          }
        }
      ],
      playOrder: [
        {
          uuid: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb'
        },
        {
          uuid: '74cec3d0-13c2-44fd-85be-e3fda7b00e08'
        }
      ],
      pawns: [
        {
          name: '7de12ba3-6bd6-4d43-b456-bf97d29b72bb',
          position: {x: 4, y: 0}
        },
        {
          name: '74cec3d0-13c2-44fd-85be-e3fda7b00e08',
          position: {x: 4, y: 8}
        }],
      startingWallCount: 8
    };

    const pawns: Pawn[] = gameActions.pawns.map(player => {
      return {
        position: player.position, name: player.name, numOfWalls: gameActions.startingWallCount
      };
    });

    this.store.dispatch(addPawns({pawns}));

    let i = 0;
    for (const turn of gameActions.history) {
      // SLEEP
      setTimeout(() => this.doAction(turn), i * 100);
      i += 10;
    }
  }

  doAction(turn: any): void {
    if (turn.action.actionType === 'MOVE_PAWN') {
      this.store.dispatch(updatePawn({
        update: {
          id: turn.pawn.uuid,
          changes: {position: turn.action.position}
        }
      }));
    } else {
      this.store.dispatch(AddWall({wall: turn.action.wall}));
    }
  }
}
