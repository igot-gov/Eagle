import { UploadPdfComponent } from './components/upload-pdf/upload-pdf.component'
import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AppSetupHomeComponent } from './app-setup-home.component'
import { HomeComponent } from '../app-setup/components/home/home.component'
import { LangSelectComponent } from './components/lang-select/lang-select.component'
import { AboutVideoComponent } from '../info/about-video/about-video.component'
import { TncAppResolverService } from '../../../../../../../src/app/services/tnc-app-resolver.service'
import { TncComponent } from './components/tnc/tnc.component'
import { SetupDoneComponent } from './components/setup-done/setup-done.component'
import { PageResolve } from '../../../../../../../library/ws-widget/utils/src/public-api'
import { InterestComponent } from './module/interest/interest/interest.component'
import { BadgesResolver } from '../profile/routes/badges/badges.resolver'

const routes: Routes = []

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: AppSetupHomeComponent,
        children: routes,
      },
      {
        path: 'home',
        component: HomeComponent,
        children: [
          {
            path: '',
            redirectTo: 'lang',
            pathMatch: 'full',
          },
          {
            path: 'upload-pdf',
            component: UploadPdfComponent,
          },
          {
            path: 'lang',
            component: LangSelectComponent,
          },
          {
            path: 'tnc',
            component: TncComponent,
            resolve: {
              tnc: TncAppResolverService,
            },
          },
          {
            path: 'about-video',
            component: AboutVideoComponent,
          },
          {
            path: 'interest',
            component: InterestComponent,
            data: {
              pageType: 'feature',
              pageKey: 'interest',
            },
            resolve: {
              pageData: PageResolve,
            },
          },
          {
            path: 'done',
            component: SetupDoneComponent,
            resolve: {
              badges: BadgesResolver,
            },
          },
        ],
      },
    ]),
  ],
  exports: [RouterModule],
})
export class AppSetupRoutingModule {}
