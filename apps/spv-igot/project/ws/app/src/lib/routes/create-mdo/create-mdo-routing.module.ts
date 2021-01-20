import { RolesAccessComponent } from './../home/routes/roles-access/roles-access.component'
import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { HomeComponent } from './routes/home/home.component'
import { UsersComponent } from './routes/users/users.component'
import { CreateMdoComponent } from '../home/routes/create-mdo/create-mdo.component'

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: ':id/users',
        component: UsersComponent,
      },
      {
        path: ':department/basicinfo',
        component: CreateMdoComponent,
      },
      {
        path: ':department/roles-access',
        component: RolesAccessComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CreateMDORoutingModule { }
