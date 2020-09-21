import { NgModule } from '@angular/core'
import { CardHomeComponent } from './card-home.component'
import { CardBadgeComponent } from './card-badges/card-badges.component'
import { CardCompetencyComponent } from './card-competency/card-competency.component'
import { CardGoalComponent } from './card-goal/card-goal.component'
import { CardLearningStatusComponent } from './card-learning-status/card-learning-status.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'

@NgModule({
  declarations: [CardHomeComponent, CardBadgeComponent, CardCompetencyComponent, CardGoalComponent, CardLearningStatusComponent],
  imports: [MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [CardHomeComponent],
})
export class CardHomeModule {

}
