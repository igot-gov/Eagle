import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { KnowledgeBoardComponent } from './components/knowledge-board/knowledge-board.component'

const routes: Routes = [
  {
    path: '',
    component: KnowledgeBoardComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class KnowledgeBoardRoutingModule { }
