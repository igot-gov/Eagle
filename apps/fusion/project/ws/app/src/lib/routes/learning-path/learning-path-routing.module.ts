import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
// import { DynamicNetworkComponent } from './dynamic-network/routes/dynamic-network/dynamic-network.component'
// import { CdpHomeComponent } from './career-development-path/routes/cdp-home/cdp-home.component'
// import { CdpPathComponent } from './career-development-path/routes/cdp-path/cdp-path.component'
// import { CdpLandingComponent } from './career-development-path/routes/cdp-landing/cdp-landing.component'
import { PageResolve } from '@ws-widget/utils'
import { PageComponent } from '@ws-widget/collection'
const BASE_URL = `assets/configurations/${location.host.replace(':', '_')}`
const routes: Routes = [
  // {
  //   path: 'cdp/:title',
  //   component: CdpHomeComponent,
  //   data: {
  //     pageType: 'feature',
  //     pageKey: 'cdp',
  //   },
  //   resolve: {
  //     pageData: PageResolve,
  //   },
  // },
  // {
  //   path: 'cdp/:cdArea/:cdPath/:child',
  //   component: CdpPathComponent,
  //   data: {
  //     pageType: 'feature',
  //     pageKey: 'cdp',
  //   },
  //   resolve: {
  //     pageData: PageResolve,
  //   },
  // },
  // {
  //   path: 'dlp/:child/:subNode',
  //   component: DynamicNetworkComponent,
  //   data: {
  //     pageType: 'feature',
  //     pageKey: 'dlp',
  //   },
  //   resolve: {
  //     pageData: PageResolve,
  //   },
  // },
  // {
  //   path: '',
  //   component: CdpLandingComponent,
  //   data: {
  //     pageType: 'feature',
  //     pageKey: 'cdp',
  //   },
  //   resolve: {
  //     pageData: PageResolve,
  //   },
  // },
  {
    path: '',
    component: PageComponent,
    data: {
      pageUrl: `${BASE_URL}/page/lex_auth_013029919942787072147`,
    },
    resolve: {
      pageData: PageResolve,
    },
  },

]
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LearningPathRoutingModule { }
