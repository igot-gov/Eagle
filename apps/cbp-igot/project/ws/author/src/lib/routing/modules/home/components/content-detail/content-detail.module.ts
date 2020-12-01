import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { AtGlanceModule, CardTableModule, LeftMenuModule, PipeContentRouteModule } from '@ws-widget/collection'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { ContentDetailComponent } from './components/content-detail/content-detail.component'
import { MyContentRoutingModule } from './content-detail-routing.module'
import { MyContentService } from './services/content-detail.service'
import { MatSortModule, MatTableModule } from '@angular/material'
import { PipeDurationTransformModule } from '@ws-widget/utils'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { ContentDetailHomeComponent } from './components/content-detail-home/content-detail-home.component'

@NgModule({
  declarations: [ContentDetailHomeComponent, ContentDetailComponent],
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
  ],
  providers: [MyContentService],
})
export class ContentDetailModule { }
