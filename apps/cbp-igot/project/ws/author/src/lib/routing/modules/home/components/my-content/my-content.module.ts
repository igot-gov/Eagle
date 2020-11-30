import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { CardTableModule, PipeContentRouteModule } from '@ws-widget/collection'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { MyContentComponent } from './components/my-content/my-content.component'
import { MyContentRoutingModule } from './my-content-routing.module'
import { MyContentService } from './services/my-content.service'
import { MatSortModule, MatTableModule } from '@angular/material'

@NgModule({
  declarations: [MyContentComponent],
  imports: [
    CommonModule,
    SharedModule,
    MyContentRoutingModule,
    PipeContentRouteModule,
    MatTableModule,
    MatSortModule,
    CardTableModule,
  ],
  providers: [MyContentService],
})
export class MyContentModule { }
