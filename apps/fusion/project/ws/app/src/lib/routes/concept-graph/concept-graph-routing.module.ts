import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { ConceptHomeComponent } from './routes/concept-home/concept-home.component'
import { ConceptGraphComponent } from './routes/concept-graph/concept-graph.component'
import { ConceptRootComponent } from './routes/concept-root/concept-root.component'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ConceptHomeComponent,
  },
  {
    path: ':ids',
    component: ConceptGraphComponent,
  },
]

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: ConceptRootComponent,
        children: routes,
      },
    ]),
  ],
  exports: [RouterModule],
})
export class ConceptGraphRoutingModule {}
