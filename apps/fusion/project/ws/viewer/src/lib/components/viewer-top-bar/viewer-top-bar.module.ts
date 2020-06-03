import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import {
  MatToolbarModule,
  MatIconModule,
  MatButtonModule,
  MatTooltipModule,
} from '@angular/material'
import { ViewerTopBarComponent } from './viewer-top-bar.component'
import { BtnFullscreenModule, BtnPageBackModule } from '@ws-widget/collection'
import { RouterModule } from '@angular/router'
@NgModule({
  declarations: [ViewerTopBarComponent],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    BtnFullscreenModule,
    BtnPageBackModule,
    MatTooltipModule,
    RouterModule,
  ],
  exports: [ViewerTopBarComponent],
})
export class ViewerTopBarModule { }
