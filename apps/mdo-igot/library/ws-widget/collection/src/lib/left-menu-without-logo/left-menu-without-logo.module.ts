import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { RouterModule } from '@angular/router'
import { LeftMenuWithoutLogoComponent } from './left-menu-without-logo.component'
import { WidgetResolverModule } from '@ws-widget/resolver'
import {
  MatButtonModule,
  MatIconModule,
  MatTooltipModule,
  MatProgressSpinnerModule,
  MatChipsModule,
  MatCardModule,
  MatSidenavModule,
  MatListModule,
} from '@angular/material'

@NgModule({
  declarations: [LeftMenuWithoutLogoComponent],
  imports: [
    CommonModule,
    RouterModule,
    WidgetResolverModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatSidenavModule,
    MatChipsModule,
    MatCardModule,
    MatListModule,
  ],
  entryComponents: [LeftMenuWithoutLogoComponent],
  exports: [
    LeftMenuWithoutLogoComponent,
  ],
})
export class LeftMenuWithoutLogoModule { }
