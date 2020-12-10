import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { PageResolve } from '@ws-widget/utils'
import { ContentDetailComponent } from './components/content-detail/content-detail.component'
import { ContentDetailHomeComponent } from './components/content-detail-home/content-detail-home.component'
import { ContentInsightsComponent } from './components/content-Insights/content-Insights.component'
import { AppTocResolverService } from './resolvers/app-toc-resolver.service'
// import { AppTocResolverService } from './resolvers/app-toc-resolver.service'
// import { GeneralGuard } from '../../../../../../../../../../src/app/guards/general.guard'

const routes: Routes = [
  {
    path: '',
    component: ContentDetailHomeComponent,
    data: {
      pageType: 'feature',
      pageKey: 'content-detail',
    },
    resolve: {
      pageData: PageResolve,
    },
    // redirectTo:
    children: [
      {
        path: ':contentId',
        component: ContentDetailComponent,
        pathMatch: 'full',
        redirectTo: ':contentId/overview',
      },
      {
        path: ':contentId/overview',
        component: ContentDetailComponent,
        resolve: {
          // content: AppTocResolverService,

        },
      },
      {
        path: ':contentId/insights',
        component: ContentInsightsComponent,
        data: {
          pageType: 'feature',
          pageKey: 'toc',
          // requiredFeatures: ['tocAnalytics'],
        },
        resolve: {
          pageData: PageResolve,
          content: AppTocResolverService,
        },
        // canActivate: [GeneralGuard],
        // runGuardsAndResolvers: 'always',
      },
      {
        path: ':contentId/notifications',
        component: ContentDetailComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  // providers: [],
  exports: [RouterModule],
})
export class MyContentRoutingModule { }
