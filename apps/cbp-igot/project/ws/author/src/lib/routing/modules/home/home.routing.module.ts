import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
// import { DepartmentResolver } from '../../../services/department-resolv.servive'
import { InitResolver } from '../../../services/init-resolve.service'
// import { DashboardComponent } from './components/dashboard/dashboard.component'
// import { MyContentComponent } from '../my-content/components/my-content/my-content.component'
import { AuthHomeComponent } from './components/home/home.component'

const routes: Routes = [
  {
    path: 'home',
    component: AuthHomeComponent,
    pathMatch: 'full',
    redirectTo: 'cbp',
  },
  {
    path: 'cbp',
    loadChildren: () =>
      import('./components/my-content/my-content.module').then(u => u.MyContentModule),
    data: { load: ['ordinals', 'ckeditor', 'meta'] },
    resolve: {
      script: InitResolver,
      // departmentData: DepartmentResolver, comment for sunbird BE to work should be removed
    },
  },
  {
    path: 'content-detail',
    loadChildren: () =>
      import('./components/content-detail/content-detail.module').then(u => u.ContentDetailModule),
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
