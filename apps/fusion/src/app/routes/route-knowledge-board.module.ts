import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { KnowledgeBoardModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    KnowledgeBoardModule,
  ],
  exports: [
    KnowledgeBoardModule,
  ],
})
export class RouteKnowledgeBoardModule { }
