import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { ContentComponent } from './components/content/content.component'
import { HomeComponent } from './components/home/home.component'
import { AnalyticsComponent } from './routes/analytics/analytics.component'
import { ReportsComponent } from './components/reports/reports.component'
import { MyLearningPathwayComponent } from './components/my-learning-pathway/my-learning-pathway.component'
import { LearningAnalyticsGuard } from './guards/learning-analytics.guard'
import { PageResolve } from '@ws-widget/utils'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: 'home',
    component: HomeComponent,
    data: {
      pageType: 'feature',
      pageKey: 'analytics',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'content',
    component: ContentComponent,
    data: {
      pageType: 'feature',
      pageKey: 'analytics',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'pathway',
    component: MyLearningPathwayComponent,
  },
  {
    path: 'reports',
    component: ReportsComponent,
    canActivate: [LearningAnalyticsGuard],
  },
]

@NgModule({
  imports: [RouterModule.forChild(
    [
      {
        path: '',
        component: AnalyticsComponent,
        children: routes,
        data: {
          pageType: 'feature',
          pageKey: 'analytics',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
    ]
  )],
  exports: [RouterModule],
})
export class LearningAnalyticsRoutingModule { }
