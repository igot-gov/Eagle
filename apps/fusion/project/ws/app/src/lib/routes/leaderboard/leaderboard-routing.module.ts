import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { HomeComponent } from './components/home/home.component'
import { LeaderboardComponent } from './components/leaderboard/leaderboard.component'
import { HallOfFameComponent } from './components/hall-of-fame/hall-of-fame.component'
import { HallOfFameResolver } from './resolvers/hall-of-fame.resolver'
import { LeaderboardResolver } from './resolvers/leaderboard.resolver'

const routes: Routes = [
  {
    path: 'hall-of-fame',
    component: HallOfFameComponent,
    resolve: {
      hallOfFameResolve: HallOfFameResolver,
    },
  },
  {
    path: 'weekly',
    component: LeaderboardComponent,
    resolve: {
      leaderboardResolve: LeaderboardResolver,
    },
  },
  {
    path: 'monthly',
    component: LeaderboardComponent,
    resolve: {
      leaderboardResolve: LeaderboardResolver,
    },
  },
  {
    path: '',
    redirectTo: 'weekly',
  },
]

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: HomeComponent,
        children: routes,
      },
    ]),
  ],
  exports: [RouterModule],
})
export class LeaderboardRoutingModule {}
