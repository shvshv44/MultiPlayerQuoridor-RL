import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {PlayDialogData} from '../../interfaces/play-dialog-data';

@Component({
  selector: 'app-play-dialog',
  templateUrl: './play-dialog.component.html',
  styleUrls: ['./play-dialog.component.scss']
})
export class PlayDialogComponent implements OnInit {

  constructor( public dialogRef: MatDialogRef<PlayDialogComponent>,
               @Inject(MAT_DIALOG_DATA) public data: PlayDialogData) { }

  ngOnInit(): void {
  }

  onCreateGame(): void {
    this.data.action = 'create';
    this.dialogRef.close(this.data);
  }

  onJoinGame(): void {
    this.data.action = 'join';
    this.dialogRef.close(this.data);
  }

  onCreateGameAgainstAgent(): void {
    this.data.action = 'createAgainstAgent';
    this.dialogRef.close(this.data);
  }
}
