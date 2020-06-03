import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { MiniProfileComponent } from './mini-profile.component'
import {
  MatDialogModule,
  MatProgressSpinnerModule,
  MatCardModule,
  MatDividerModule,
  MatButtonModule,
  MatIconModule,
  MatChipsModule,
  MatTooltipModule,
} from '@angular/material'
import { PipeNameTransformModule, DefaultThumbnailModule } from '@ws-widget/utils'
import { UserImageModule } from '../_common/user-image/user-image.module'
import {
  PipeContentRouteModule,
} from '../_common/pipe-content-route/pipe-content-route.module'
import { ProfileImageModule } from '../_common/profile-image/profile-image.module'
@NgModule({
  declarations: [MiniProfileComponent],
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    PipeNameTransformModule,
    UserImageModule,
    MatChipsModule,
    MatDividerModule,
    MatCardModule,
    PipeContentRouteModule,
    MatProgressSpinnerModule,
    ProfileImageModule,
    MatTooltipModule,
    DefaultThumbnailModule,
  ],
  entryComponents: [MiniProfileComponent],
  exports: [MiniProfileComponent],
})
export class MiniProfileModule { }
