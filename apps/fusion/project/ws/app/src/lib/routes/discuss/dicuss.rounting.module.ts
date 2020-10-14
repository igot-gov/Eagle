import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { DiscussComponent } from './routes/discuss-home/discuss.component'
import { DiscussAllComponent } from './routes/discuss-all/discuss-all.component'
import { DiscussCategoriesComponent } from './routes/discuss-categories/discuss-categories.component'
import { DiscussGroupsComponent } from './routes/discuss-groups/discuss-groups.component'
import { DiscussTagsComponent } from './routes/discuss-tags/discuss-tags.component'
import { DiscussLeaderboardComponent } from './routes/discuss-leaderboard/discuss-leaderboard.component'
import { DiscussMyDiscussionsComponent } from './routes/discuss-my-discussions/discuss-my-discussions.component'
import { DiscussionComponent } from './routes/discussion/discussion.component'
import { InitResolver } from './resolvers/init-resolve.service'
import { DiscussTagsResolve } from './resolvers/discuss-tags-resolve'
import { DiscussCategoriesResolve } from './resolvers/discuss-category-resolve'
import { DiscussRecentResolve } from './resolvers/discuss-recent-resolve'
// import { DiscussCategoriesResolve } from './resolvers/discuss-category-resolve'
import { DiscussTopicResolve } from './resolvers/discuss-topic-resolve'
import { DiscussUnreadResolve } from './resolvers/discuss-unread-resolve'
import { DiscussProfileResolve } from './resolvers/discuss-profile-resolve'

const routes: Routes = [
  {
    path: '',
    component: DiscussComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: 'home',
        component: DiscussAllComponent,
        resolve: {
          availCategories: DiscussCategoriesResolve,
          availableTags: DiscussTagsResolve,
          recent: DiscussRecentResolve,
          // unread: DiscussUnreadResolve,
        },
      },
      {
        path: 'home/:topicId',
        component: DiscussionComponent,
        data: {
          load: ['ckeditor'],
        },
        resolve: {
          script: InitResolver,
          topic: DiscussTopicResolve,
        },
      },
      {
        path: 'categories',
        component: DiscussCategoriesComponent,
        resolve: {
          availCategories: DiscussCategoriesResolve,
        },
      },
      {
        path: 'groups',
        component: DiscussGroupsComponent,
      },
      {
        path: 'tags',
        component: DiscussTagsComponent,
        resolve: {
          availableTags: DiscussTagsResolve,
        },
      },
      {
        path: 'leaderboard',
        component: DiscussLeaderboardComponent,
      },
      {
        path: 'my-discussions',
        component: DiscussMyDiscussionsComponent,
        resolve: {
          profile: DiscussProfileResolve,
        },
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    DiscussTagsResolve,
    DiscussCategoriesResolve,
    DiscussRecentResolve,
    DiscussTopicResolve,
    DiscussUnreadResolve,
    DiscussProfileResolve,
  ],
})
export class DiscussRoutingModule { }
