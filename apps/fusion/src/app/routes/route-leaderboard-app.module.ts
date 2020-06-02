import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { LeaderboardModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    LeaderboardModule,
  ],
  exports: [
    LeaderboardModule,
  ],
})
export class RouteLeaderboardAppModule { }
