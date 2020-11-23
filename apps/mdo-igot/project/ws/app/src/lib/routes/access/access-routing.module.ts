import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { HomeComponent } from './routes/home/home.component'
import { PrivilegesComponent } from './routes/privileges/privileges.component'
import { UsersViewComponent } from '../../routes/home/routes/users-view/users-view.component'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'privileges',
  },
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: 'privileges',
        component: PrivilegesComponent,
      },
      {
        path: 'home/users',
        component: UsersViewComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccessRoutingModule { }
