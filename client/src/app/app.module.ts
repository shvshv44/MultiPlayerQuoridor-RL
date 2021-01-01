import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MenuComponent} from './components/menu/menu.component';
import {GameHistoryComponent} from './components/game-history/game-history.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from './material-module';
import {BoardGameComponent} from './components/board-game/board-game.component';
import {BoardCellComponent} from './components/board-cell/board-cell.component';

import {GameConsoleComponent} from './components/game-console/game-console.component';
import {GameOptionsComponent} from './components/game-options/game-options.component';
import {GameScreenComponent} from './components/game-screen/game-screen.component';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {OptionButtonComponent} from './components/option-button/option-button.component';
import {BoardWallComponent} from './components/board-wall/board-wall.component';

import {StoreModule} from '@ngrx/store';
import {wallsReducer} from './reducers/walls/walls.reducer';
import {pawnsReducer} from './reducers/pawns/pawns.reducer';
import {PlayDialogComponent} from './components/play-dialog/play-dialog.component';
import {FormsModule} from '@angular/forms';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {GameRoomScreenComponent} from './components/game-room-screen/game-room-screen.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {EffectsModule} from '@ngrx/effects';
import {WallsEffects} from './reducers/walls/walls.effects';
import {PawnsEffects} from './reducers/pawns/pawns.effects';
import { BoardRowComponent } from './components/board-row/board-row.component';

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    GameHistoryComponent,
    BoardGameComponent,
    BoardCellComponent,
    GameConsoleComponent,
    GameOptionsComponent,
    GameScreenComponent,
    OptionButtonComponent,
    BoardWallComponent,
    PlayDialogComponent,
    GameRoomScreenComponent,
    BoardRowComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    BrowserAnimationsModule,
    ScrollingModule,
    StoreModule.forRoot({walls: wallsReducer, pawns: pawnsReducer}),
    EffectsModule.forRoot([WallsEffects, PawnsEffects]),
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
