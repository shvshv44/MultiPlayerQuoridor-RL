import {MatButtonModule} from '@angular/material/button';
import {NgModule} from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {ClipboardModule} from '@angular/cdk/clipboard';

@NgModule({
  exports: [
    MatButtonModule,
    MatListModule,
    MatCardModule,
    ClipboardModule
  ]
})
export class MaterialModule {
}
