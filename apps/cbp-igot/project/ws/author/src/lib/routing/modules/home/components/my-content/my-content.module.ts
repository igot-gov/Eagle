import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { AtGlanceModule, CardTableModule, PipeContentRouteModule } from '@ws-widget/collection'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { MyContentComponent } from './components/my-content/my-content.component'
import { MyContentRoutingModule } from './my-content-routing.module'
import { MyContentService } from './services/my-content.service'
import { MatSortModule, MatTableModule } from '@angular/material'
import { PipeDurationTransformModule } from '@ws-widget/utils'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { AllContentComponent } from './components/all-content/all-content.component'
import { MandatoryContentComponent } from './components/mandatory-content/mandatory-content.component'

@NgModule({
  declarations: [MyContentComponent, AllContentComponent, MandatoryContentComponent],
  imports: [
    CommonModule,
    SharedModule,
    MyContentRoutingModule,
    PipeContentRouteModule,
    PipeDurationTransformModule,
    MatTableModule,
    MatSortModule,
    CardTableModule,
    WidgetResolverModule,
    AtGlanceModule,
  ],
  providers: [MyContentService],
})
export class MyContentModule { }
