import { NgModule } from '@angular/core'
import { CardHomeNetworkComponent } from './card-home-network.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { AvatarPhotoModule } from '../_common/avatar-photo/avatar-photo.module'

@NgModule({
  declarations: [CardHomeNetworkComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
    MatExpansionModule, MatIconModule, MatProgressSpinnerModule, AvatarPhotoModule],
  entryComponents: [CardHomeNetworkComponent],
})
export class CardHomeNetworkModule {

}
