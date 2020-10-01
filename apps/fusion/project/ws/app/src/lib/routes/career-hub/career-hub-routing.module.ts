import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { CareersHomeComponent } from './routes/careers-home/careers-home.component'
import { CareersComponent } from './routes/careers/careers.component'
import { CareerDetailComponent } from './routes/career-detail/career-detail.component'
import { CareerRecentResolve } from './resolvers/careers-resolve'
import { CareerDetailResolve } from './resolvers/careers-detail-resolve'

const routes: Routes = [
  {
    path: '',
    component: CareersHomeComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: 'home',
        component: CareersComponent,
        resolve: {
          topics: CareerRecentResolve,
        },
      },
      {
        path: 'home/:topicId',
        component: CareerDetailComponent,
        // data: {
        //   load: ['ckeditor'],
        // },
        resolve: {
          topic: CareerDetailResolve,
        },
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    CareerRecentResolve,
    CareerDetailResolve,
  ],
})
export class CareerHubRoutingModule { }
