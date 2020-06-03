import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { PageResolve } from '../../../../../../../library/ws-widget/utils/src/public-api'
import { LearningPathHomeComponent } from './routes/learning-path-home/learning-path-home.component'
import { LearningPathDetailsComponent } from './components/learning-path-details/learning-path-details.component'

const routes: Routes = [
  {
    path: '',
    data: {
      pageType: 'feature',
      pageKey: 'learning-paths',
    },
    resolve: {
      pageData: PageResolve,
    },
    children: [
      {
        path: '',
        component: LearningPathHomeComponent,
      },
      {
        path: ':id',
        component: LearningPathDetailsComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AcademyLearningPathsRoutingModule { }
