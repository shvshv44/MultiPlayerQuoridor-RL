import {Injectable} from '@angular/core';
import {webSocket} from 'rxjs/webSocket';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private subject: any;

  constructor() {
    this.subject = webSocket('ws://localhost:8080');

    this.subject.subscribe(
      (msg: string) => console.log('message received: ' + msg), // Called whenever there is a message from the server.
      (err: any) => console.log(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete')); // Called when connection is closed (for whatever reason).
  }


  // This will send a message to the server once a connection is made. Remember value is serialized with JSON.stringify by default!
  sendMessage(msg: any): void {
    this.subject.next(msg);
  }

  // Closes the connection.
  close(): void {
    this.subject.complete();
  }
}
