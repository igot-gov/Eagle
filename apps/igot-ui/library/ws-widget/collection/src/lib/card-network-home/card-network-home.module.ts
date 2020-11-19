import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule, MatInputModule } from '@angular/material'
import { HorizontalScrollerModule, PipeNameTransformModule } from '@ws-widget/utils'
import { ActivityCardModule } from '../activity-card/activity-card.module'
import { TourModule } from '../_common/tour-guide/tour-guide.module'
import { AvatarPhotoModule } from '../_common/avatar-photo/avatar-photo.module'
import { CardNetworkHomeComponent } from './card-network-home.component'
import { ChallengeModule } from '../challenge/challenge.module'
import { MatGridListModule } from '@angular/material/grid-list'
import { FormsModule } from '@angular/forms'

@NgModule({
  declarations: [CardNetworkHomeComponent],
  imports: [
    CommonModule,
    AvatarPhotoModule,
    MatButtonModule,
    MatChipsModule,
    MatDividerModule,
    MatExpansionModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    HorizontalScrollerModule,
    ActivityCardModule,
    TourModule,
    PipeNameTransformModule,
    ChallengeModule,
    MatInputModule,
    MatGridListModule,
    FormsModule,
  ],
  entryComponents: [CardNetworkHomeComponent],
  providers: [],
})
export class CardNetworkHomeModule {

}
