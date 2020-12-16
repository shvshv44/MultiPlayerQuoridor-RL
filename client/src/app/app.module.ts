import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BoardGameComponent } from './board-game/board-game.component';
import { BoardCellComponent } from './board-cell/board-cell.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GameConsoleComponent } from './game-console/game-console.component';
import { GameOptionsComponent } from './game-options/game-options.component';
import { GameScreenComponent } from './game-screen/game-screen.component';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { OptionButtonComponent } from './option-button/option-button.component';

@NgModule({
  declarations: [
    AppComponent,
    BoardGameComponent,
    BoardCellComponent,
    GameConsoleComponent,
    GameOptionsComponent,
    GameScreenComponent,
    OptionButtonComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ScrollingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
