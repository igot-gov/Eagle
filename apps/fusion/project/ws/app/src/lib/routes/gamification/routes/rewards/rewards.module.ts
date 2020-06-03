import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { RewardsRoutingModule } from './rewards-routing.module'
import { MyRewardsComponent } from './components/my-rewards/my-rewards.component'
import {
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,
  MatProgressBarModule,
  MatDividerModule,
  MatProgressSpinnerModule,

} from '@angular/material'

@NgModule({
  declarations: [MyRewardsComponent],
  imports: [
    CommonModule,
    RewardsRoutingModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatDividerModule,
    MatProgressSpinnerModule,

  ],
})
export class RewardsModule { }
