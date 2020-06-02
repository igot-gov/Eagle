import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { EditorSharedModule } from '../../../shared/shared.module'
import { KnowledgeBoardRoutingModule } from './knowledge-borad-routing.module'
import { KnowledgeBoardComponent } from './components/knowledge-board/knowledge-board.component'

@NgModule({
  declarations: [
    KnowledgeBoardComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    EditorSharedModule,
    KnowledgeBoardRoutingModule,
  ],
})
export class KnowledgeBoardModule { }
