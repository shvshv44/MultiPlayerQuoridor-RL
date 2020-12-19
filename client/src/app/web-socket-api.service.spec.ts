import { TestBed } from '@angular/core/testing';

import { WebSocketApiService } from './web-socket-api.service';

describe('WebSocketApiService', () => {
  let service: WebSocketApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebSocketApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
