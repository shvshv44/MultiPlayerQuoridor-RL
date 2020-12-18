import {Component, ElementRef, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-board-wall',
  templateUrl: './board-wall.component.html',
  styleUrls: ['./board-wall.component.scss']
})
export class BoardWallComponent implements OnInit {
  @Input() isActive = false;
  @Input() isVertical = true;
  @Input() row = -1;
  @Input() col = -1;

  constructor(public elementRef: ElementRef) {
  }

  ngOnInit(): void {
  }

}
