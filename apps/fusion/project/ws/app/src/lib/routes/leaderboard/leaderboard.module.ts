import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { LeaderboardRoutingModule } from './leaderboard-routing.module'
import { HallOfFameItemComponent } from './components/hall-of-fame-item/hall-of-fame-item.component'
import { HomeComponent } from './components/home/home.component'
import { LeaderboardComponent } from './components/leaderboard/leaderboard.component'
import { LeaderboardControlsComponent } from './components/leaderboard-controls/leaderboard-controls.component'
import { LeaderboardItemComponent } from './components/leaderboard-item/leaderboard-item.component'
import {
  MatTabsModule,
  MatCardModule,
  MatDividerModule,
  MatButtonModule,
  MatToolbarModule,
  MatIconModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
} from '@angular/material'
import { CardListComponent } from './components/card-list/card-list.component'
import { CardListItemComponent } from './components/card-list-item/card-list-item.component'
import { PipeNameTransformModule, PipeCountTransformModule } from '@ws-widget/utils'
import { HallOfFameComponent } from './components/hall-of-fame/hall-of-fame.component'
import { UserImageModule } from '@ws-widget/collection'

@NgModule({
  declarations: [
    HallOfFameItemComponent,
    HomeComponent,
    LeaderboardComponent,
    LeaderboardControlsComponent,
    LeaderboardItemComponent,
    CardListComponent,
    CardListItemComponent,
    HallOfFameComponent,
  ],
  imports: [
    CommonModule,
    LeaderboardRoutingModule,
    MatTabsModule,
    MatCardModule,
    MatDividerModule,
    MatButtonModule,
    MatTabsModule,
    MatToolbarModule,
    MatIconModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    PipeNameTransformModule,
    PipeCountTransformModule,
    UserImageModule,
  ],
})
export class LeaderboardModule {}
