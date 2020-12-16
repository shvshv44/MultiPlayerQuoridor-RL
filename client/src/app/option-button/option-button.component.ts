import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-option-button',
  templateUrl: './option-button.component.html',
  styleUrls: ['./option-button.component.scss']
})
export class OptionButtonComponent implements OnInit {
  @Input() btnName = '';

  constructor() { }

  ngOnInit(): void {
  }

}
