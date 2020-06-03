import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { AcademyLearningPathsRoutingModule } from './academy-learning-paths-routing.module'
import { LearningPathHomeComponent } from './routes/learning-path-home/learning-path-home.component'
import { MatCardModule, MatIconModule, MatToolbarModule } from '@angular/material'
import { DefaultThumbnailModule, PipeHtmlTagRemovalModule, HorizontalScrollerModule } from '../../../../../../../library/ws-widget/utils/src/public-api'
import { LearningPathDetailsComponent } from './components/learning-path-details/learning-path-details.component'
import { BtnPageBackModule } from '../../../../../../../library/ws-widget/collection/src/public-api'
import { WidgetResolverModule } from '../../../../../../../library/ws-widget/resolver/src/public-api'

@NgModule({
  declarations: [LearningPathHomeComponent, LearningPathDetailsComponent],
  imports: [
    CommonModule,
    AcademyLearningPathsRoutingModule,
    MatToolbarModule,
    MatIconModule,
    MatCardModule,
    DefaultThumbnailModule,
    PipeHtmlTagRemovalModule,
    BtnPageBackModule,
    WidgetResolverModule,
    HorizontalScrollerModule,
  ],
})
export class AcademyLearningPathsModule { }
