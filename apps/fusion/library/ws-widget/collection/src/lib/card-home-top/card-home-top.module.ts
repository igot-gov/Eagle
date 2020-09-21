import { NgModule } from '@angular/core'
import { CardHomeTopComponent } from './card-home-top.component'
import { CardBadgeComponent } from './card-badges/card-badges.component'
import { CardCompetencyComponent } from './card-competency/card-competency.component'
import { CardGoalComponent } from './card-goal/card-goal.component'
import { CardLearningStatusComponent } from './card-learning-status/card-learning-status.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'

@NgModule({
  declarations: [CardHomeTopComponent, CardBadgeComponent, CardCompetencyComponent, CardGoalComponent, CardLearningStatusComponent],
  imports: [MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [CardHomeTopComponent],
})
export class CardHomeTopModule {

}
