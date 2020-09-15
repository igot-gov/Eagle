import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { DiscussComponent } from './routes/discuss-home/discuss.component'
import { DiscussAllComponent } from './routes/discuss-all/discuss-all.component'
import { DiscussCategoriesComponent } from './routes/discuss-categories/discuss-categories.component'
import { DiscussGroupsComponent } from './routes/discuss-groups/discuss-groups.component'
import { DiscussTagsComponent } from './routes/discuss-tags/discuss-tags.component'
import { DiscussLeaderboardComponent } from './routes/discuss-leaderboard/discuss-leaderboard.component'
import { DiscussMyDiscussionsComponent } from './routes/discuss-my-discussions/discuss-my-discussions.component'

const routes: Routes = [
  {
    path: '',
    component: DiscussComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'discussions',
      },
      {
        path: 'discussions',
        component: DiscussAllComponent,
      },
      {
        path: 'categories',
        component: DiscussCategoriesComponent,
      },
      {
        path: 'groups',
        component: DiscussGroupsComponent,
      },
      {
        path: 'tags',
        component: DiscussTagsComponent,
      },
      {
        path: 'leaderboard',
        component: DiscussLeaderboardComponent,
      },
      {
        path: 'my-discussions',
        component: DiscussMyDiscussionsComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DiscussRoutingModule { }
