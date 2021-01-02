import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardRowComponent } from './board-row.component';

describe('BoardRowComponent', () => {
  let component: BoardRowComponent;
  let fixture: ComponentFixture<BoardRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BoardRowComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BoardRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
