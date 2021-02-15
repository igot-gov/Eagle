import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { RouterModule } from '@angular/router'
import { AvatarPhotoComponent } from './avatar-photo.component'

@NgModule({
  declarations: [AvatarPhotoComponent],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [AvatarPhotoComponent],
  entryComponents: [AvatarPhotoComponent],
})
export class AvatarPhotoModule { }
