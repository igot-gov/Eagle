import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { DiscussComponent } from './routes/discuss-home/discuss.component'
import { DiscussCommetsComponent } from './components/discuss-comments/discuss-comments.component'
import { DiscussCategoriesComponent } from './routes/discuss-categories/discuss-categories.component'
import { DiscussGroupsComponent } from './routes/discuss-groups/discuss-groups.component'
import { DiscussLeaderboardComponent } from './routes/discuss-leaderboard/discuss-leaderboard.component'
import { DiscussMyDiscussionsComponent } from './routes/discuss-my-discussions/discuss-my-discussions.component'
import { DiscussTagsComponent } from './routes/discuss-tags/discuss-tags.component'
import { DiscussRoutingModule } from './dicuss.rounting.module'
import { DiscussCardComponent } from './components/discuss-card/discuss-card.component'
import { LeftMenuComponent } from './components/left-menu/left-menu.component'
import { RightMenuComponent } from './components/right-menu/right-menu.component'
import { MatGridListModule } from '@angular/material/grid-list'
import { MatExpansionModule } from '@angular/material/expansion'
import { MatDividerModule } from '@angular/material/divider'
import { MatIconModule, MatListModule } from '@angular/material'
import { MatCardModule } from '@angular/material/card'
import { DiscussAllComponent } from './routes/discuss-all/discuss-all.component'


@NgModule({
  declarations: [
    DiscussComponent,
    DiscussAllComponent,
    DiscussCardComponent,
    DiscussCommetsComponent,
    DiscussCategoriesComponent,
    DiscussGroupsComponent,
    DiscussLeaderboardComponent,
    DiscussMyDiscussionsComponent,
    DiscussTagsComponent,
    LeftMenuComponent,
    RightMenuComponent,
  ],
  imports: [
    CommonModule,
    DiscussRoutingModule,
    MatGridListModule,
    MatExpansionModule,
    MatDividerModule,
    MatIconModule,
    MatCardModule,
    MatListModule
  ],
})
export class DiscussModule {

}
