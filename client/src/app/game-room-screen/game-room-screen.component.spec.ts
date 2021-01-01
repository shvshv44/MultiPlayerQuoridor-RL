import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameRoomScreenComponent } from './game-room-screen.component';

describe('GameRoomScreenComponent', () => {
  let component: GameRoomScreenComponent;
  let fixture: ComponentFixture<GameRoomScreenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GameRoomScreenComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameRoomScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
