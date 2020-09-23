import { NgModule } from '@angular/core'
import { CardHomeDiscussComponent } from './card-home-discuss.component'
import {
  MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
  MatExpansionModule, MatIconModule, MatProgressSpinnerModule,
} from '@angular/material'
import { AvatarPhotoModule } from '../_common/avatar-photo/avatar-photo.module'
import { BrowserModule } from '@angular/platform-browser'

@NgModule({
  declarations: [CardHomeDiscussComponent],
  imports: [BrowserModule, AvatarPhotoModule, MatButtonModule, MatCardModule, MatChipsModule,
    MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [CardHomeDiscussComponent],
})
export class CardHomeDiscussModule {

}
