import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { InitResolver } from '../../../services/init-resolve.service'
// import { DashboardComponent } from './components/dashboard/dashboard.component'
// import { MyContentComponent } from '../my-content/components/my-content/my-content.component'
import { AuthHomeComponent } from './components/home/home.component'

const routes: Routes = [
  {
    path: '',
    component: AuthHomeComponent,
    pathMatch: 'full',
    redirectTo: 'my-content',
  },
  {
    path: 'my-content',
    loadChildren: () =>
      import('./components/my-content/my-content.module').then(u => u.MyContentModule),
    data: { load: ['ordinals', 'ckeditor', 'meta'] },
    resolve: {
      script: InitResolver,
    },
  },
]
@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: AuthHomeComponent,
        children: routes,
      },
    ]),
  ],
  exports: [RouterModule],
})
export class WsAuthorHomeRoutingModule { }
