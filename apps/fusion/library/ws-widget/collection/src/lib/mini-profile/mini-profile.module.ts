import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { RouterModule } from '@angular/router'
import { MatButtonModule, MatIconModule, MatMenuModule, MatRippleModule, MatCardModule } from '@angular/material'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { MiniProfileComponent } from './mini-profile.component'
import { AvatarPhotoModule } from '../_common/avatar-photo/avatar-photo.module'

@NgModule({
  declarations: [MiniProfileComponent],
  imports: [
    AvatarPhotoModule,
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatMenuModule,
    MatRippleModule,
    WidgetResolverModule,
  ],
  exports: [MiniProfileComponent],
  entryComponents: [MiniProfileComponent],
})
export class MiniProfileModule { }
