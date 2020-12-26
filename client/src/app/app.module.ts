import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MenuComponent } from './menu/menu.component';
import { GameHistoryComponent } from './game-history/game-history.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MaterialModule} from './material-module';
import {BoardGameComponent} from './board-game/board-game.component';
import {BoardCellComponent} from './board-cell/board-cell.component';

import {GameConsoleComponent} from './game-console/game-console.component';
import {GameOptionsComponent} from './game-options/game-options.component';
import {GameScreenComponent} from './game-screen/game-screen.component';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {OptionButtonComponent} from './option-button/option-button.component';
import {BoardWallComponent} from './board-wall/board-wall.component';

import {StoreModule} from '@ngrx/store';
import {wallsReducer} from './reducers/walls/walls.reducer';
import {pawnsReducer} from './reducers/pawns/pawns.reducer';
import { PlayDialogComponent } from './play-dialog/play-dialog.component';
import {FormsModule} from '@angular/forms';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';

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
    PlayDialogComponent
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
        FormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
