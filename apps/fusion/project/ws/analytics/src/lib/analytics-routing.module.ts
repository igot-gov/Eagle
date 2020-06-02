import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
// import { PageComponent } from '@ws-widget/collection'
// import { PageResolve } from '../../../../../library/ws-widget/utils/src/public-api'
import { AnalyticsGuard } from './guards/analytics.guard'

// const BASE_URL = `assets/configurations/${location.host.replace(':', '_')}`
const routes: Routes = [
  // {
  //   path: '',
  //   pathMatch: 'full',
  //   redirectTo: 'learning-analytics',
  // },
  {
    path: '',
    loadChildren: () => import('./routes/learning-analytics/learning-analytics.module').then(u => u.LearningAnalyticsModule),
    canActivate: [AnalyticsGuard],
  },
  {
    path: 'channel',
    loadChildren: () => import('./routes/channel/channel.module').then(u => u.ChannelModule),
  },
  {
    path: 'knowledge-board',
    loadChildren: () => import('./routes/channel/channel.module').then(u => u.ChannelModule),
  },
  {
    path: 'apps',
    loadChildren: () => import('./routes/maq-app/maq-app.module').then(u => u.MaqAppModule),
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AnalyticsRoutingModule { }
