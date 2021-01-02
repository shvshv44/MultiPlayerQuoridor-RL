import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {MenuComponent} from './menu/menu.component';
import {GameHistoryComponent} from './game-history/game-history.component';
import {GameScreenComponent} from './game-screen/game-screen.component';
import {GameRoomScreenComponent} from './game-room-screen/game-room-screen.component';
import {GameRulesComponent} from './game-rules/game-rules.component';

const routes: Routes = [
  { path: '', redirectTo: '/menu', pathMatch: 'full' },
  { path: 'menu', component: MenuComponent },
  { path: 'game-screen', component: GameScreenComponent },
  { path: 'game-history', component: GameHistoryComponent },
  { path: 'game-room-screen', component: GameRoomScreenComponent },
  { path: 'game-rules', component: GameRulesComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
