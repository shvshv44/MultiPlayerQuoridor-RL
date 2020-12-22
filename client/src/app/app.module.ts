import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MenuComponent } from './menu/menu.component';
import { GameHistoryComponent } from './game-history/game-history.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MaterialModule} from './material-module';
import { RulesComponent } from './rules/rules.component';
import { BoardGameComponent } from './board-game/board-game.component';
import { BoardCellComponent } from './board-cell/board-cell.component';
import { GameConsoleComponent } from './game-console/game-console.component';
import { GameOptionsComponent } from './game-options/game-options.component';
import { GameScreenComponent } from './game-screen/game-screen.component';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { OptionButtonComponent } from './option-button/option-button.component';
import { BoardWallComponent } from './board-wall/board-wall.component';

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    GameHistoryComponent,
    RulesComponent,
    BoardGameComponent,
    BoardCellComponent,
    GameConsoleComponent,
    GameOptionsComponent,
    GameScreenComponent,
    OptionButtonComponent,
    BoardWallComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    BrowserAnimationsModule,
    ScrollingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
