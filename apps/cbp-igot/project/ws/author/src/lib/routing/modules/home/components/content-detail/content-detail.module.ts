import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import {
  AtGlanceModule,
  AuthorCardModule,
  CardTableModule,
  LeftMenuModule,
  PipeContentRouteModule,
  UserContentDetailedRatingModule,
  UserContentRatingModule,
} from '@ws-widget/collection'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { ContentDetailComponent } from './components/content-detail/content-detail.component'
import { MyContentRoutingModule } from './content-detail-routing.module'
import { MyContentService } from './services/content-detail.service'
import { MatSortModule, MatTableModule } from '@angular/material'
import { PipeDurationTransformModule } from '@ws-widget/utils'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { ContentDetailHomeComponent } from './components/content-detail-home/content-detail-home.component'
import { ContentInsightsComponent } from './components/content-Insights/content-Insights.component'
import { AppTocResolverService } from './resolvers/app-toc-resolver.service'
import { AppTocService } from './services/app-toc.service'
import { MyTocService } from './services/my-toc.service'
import { ContentDiscussionComponent } from './components/content-discussion/content-discussion.component'
import { LocalDataService } from './services/local-data.service'

@NgModule({
  declarations: [ContentDetailHomeComponent, ContentDetailComponent, ContentInsightsComponent, ContentDiscussionComponent],
  imports: [
    CommonModule,
    SharedModule,
    MyContentRoutingModule,
    PipeContentRouteModule,
    PipeDurationTransformModule,
    MatTableModule,
    MatSortModule,
    CardTableModule,
    LeftMenuModule,
    WidgetResolverModule,
    AtGlanceModule,
    AuthorCardModule,
    UserContentRatingModule,
    UserContentDetailedRatingModule,
  ],
  providers: [
    AppTocService,
    MyContentService,
    AppTocResolverService,
    MyTocService,
    LocalDataService,

  ],
  entryComponents: [],
})
export class ContentDetailModule { }
