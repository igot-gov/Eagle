import { NgModule } from '@angular/core'
import { CardHomeTopComponent } from './card-home-top.component'
import { CardBadgeComponent } from './card-badges/card-badges.component'
import { CardCompetencyComponent } from './card-competency/card-competency.component'
import { CardGoalComponent } from './card-goal/card-goal.component'
import { MatProgressBarModule } from '@angular/material/progress-bar'
import { BrowserModule } from '@angular/platform-browser'
import { AvatarPhotoModule } from '../_common/avatar-photo/avatar-photo.module'
import { CardLearningStatusComponent } from './card-learning-status/card-learning-status.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'

@NgModule({
  declarations: [CardHomeTopComponent,
    CardBadgeComponent, CardCompetencyComponent, CardGoalComponent, CardLearningStatusComponent],
  imports: [AvatarPhotoModule, BrowserModule, MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
    MatExpansionModule, MatIconModule, MatProgressSpinnerModule, MatProgressBarModule],
  entryComponents: [CardHomeTopComponent],
})
export class CardHomeTopModule {

}
