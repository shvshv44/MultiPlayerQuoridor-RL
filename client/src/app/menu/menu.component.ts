import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {WebSocketApiService} from '../web-socket-api.service';


@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  constructor(private router: Router,
              private webSocketApiService: WebSocketApiService) {
  }

  ngOnInit(): void {
  }

  callHello(): void {
    this.webSocketApiService._hello({id: 5});
  }

  onGameHistoryClick(): void {
    this.router.navigateByUrl('/game-history');
  }
}
