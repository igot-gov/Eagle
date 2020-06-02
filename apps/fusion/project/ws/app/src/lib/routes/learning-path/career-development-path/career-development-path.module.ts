import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CdpCardComponent } from './components/cdp-card/cdp-card.component'
import { CdpHomeComponent } from './routes/cdp-home/cdp-home.component'
import { CdpPathComponent } from './routes/cdp-path/cdp-path.component'
import { CdpLandingComponent } from './routes/cdp-landing/cdp-landing.component'
import {
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  MatListModule,
  MatRippleModule,
  MatToolbarModule,
  MatTooltipModule,
  MatGridListModule,
  MatSnackBarModule,
  MatProgressSpinnerModule,
  MatStepperModule,
  MatFormFieldModule,
  MatInputModule,
  MatChipsModule,
  MatProgressBarModule,
} from '@angular/material'
import { RouterModule } from '@angular/router'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { CdpHomeCardComponent } from './components/cdp-home-card/cdp-home-card.component'
import {
  HorizontalScrollerModule,
  DefaultThumbnailModule,
  PipeLimitToModule,
} from '@ws-widget/utils'
import { BtnContentLikeModule, BtnPageBackModule, PageModule  } from '@ws-widget/collection'
import { WidgetResolverModule } from '@ws-widget/resolver'

@NgModule({
  declarations: [
    CdpCardComponent,
    CdpHomeComponent,
    CdpPathComponent,
    CdpHomeCardComponent,
    CdpLandingComponent,
  ],
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatListModule,
    MatRippleModule,
    MatToolbarModule,
    MatTooltipModule,
    MatGridListModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatStepperModule,
    RouterModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatChipsModule,
    HorizontalScrollerModule,
    DefaultThumbnailModule,
    MatProgressBarModule,
    MatInputModule,
    BtnContentLikeModule,
    PipeLimitToModule,
    BtnPageBackModule,
    WidgetResolverModule,
    PageModule,
  ],
  exports: [CdpCardComponent],
})
export class CareerDevelopmentPathModule {}
