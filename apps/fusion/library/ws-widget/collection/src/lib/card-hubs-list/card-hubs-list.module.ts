import { NgModule } from '@angular/core'
import { CardHubsListComponent } from './card-hubs-list.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { AvatarPhotoModule } from '../_common/avatar-photo/avatar-photo.module'
import { HorizontalScrollerModule, PipeNameTransformModule } from '@ws-widget/utils'
import { RouterModule } from '@angular/router'

@NgModule({
  declarations: [CardHubsListComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
    MatExpansionModule, MatIconModule, MatProgressSpinnerModule, AvatarPhotoModule,
    HorizontalScrollerModule, PipeNameTransformModule, RouterModule],
  entryComponents: [CardHubsListComponent],
})
export class CardHubsListModule {

}
