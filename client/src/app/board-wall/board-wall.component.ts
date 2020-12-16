import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-board-wall',
  templateUrl: './board-wall.component.html',
  styleUrls: ['./board-wall.component.scss']
})
export class BoardWallComponent implements OnInit {
  @Input() isActive = false;
  @Input() isHorizontal = true;

  constructor() { }

  ngOnInit(): void {
  }

}
