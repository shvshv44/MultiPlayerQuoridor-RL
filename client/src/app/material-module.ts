import {MatButtonModule} from '@angular/material/button';
import {NgModule} from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';

@NgModule({
  exports: [
    MatButtonModule,
    MatListModule,
    MatCardModule
  ]
})
export class MaterialModule {
}
