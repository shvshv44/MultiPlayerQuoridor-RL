import {Component} from '@angular/core';
import {WebSocketApiService} from './web-socket-api.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'client';

  constructor(private webSocketApiService: WebSocketApiService) {
  }
}
