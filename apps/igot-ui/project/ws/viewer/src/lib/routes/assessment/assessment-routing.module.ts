import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AssessmentComponent } from './assessment.component'
import { ViewerResolve } from '../../viewer.resolve'

const routes: Routes = [
  {
    path: ':resourceId',
    component: AssessmentComponent,
    resolve: {
      content: ViewerResolve,
    },
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [],
})
export class AssessmentRoutingModule { }
