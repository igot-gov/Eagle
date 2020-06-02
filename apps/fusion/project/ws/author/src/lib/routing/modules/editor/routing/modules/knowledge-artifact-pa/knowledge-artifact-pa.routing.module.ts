import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { KnowledgeArtifactPaComponent } from './components/knowledge-artifact-pa/knowledge-artifact-pa.component'

const routes: Routes = [
  {
    path: '',
    component: KnowledgeArtifactPaComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class KnowledgeArtifactPaRoutingModule { }
