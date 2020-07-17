import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { RouterModule } from '@angular/router'
import { MatButtonModule, MatIconModule } from '@angular/material'
import { BtnPageBackNavComponent } from './btn-page-back-nav.component'

@NgModule({
  declarations: [BtnPageBackNavComponent],
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
  ],
  exports: [BtnPageBackNavComponent],
  entryComponents: [BtnPageBackNavComponent],
})
export class BtnPageBackNavModule { }
