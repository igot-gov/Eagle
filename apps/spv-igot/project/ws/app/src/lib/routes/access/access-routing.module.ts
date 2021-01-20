import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { RolesAccessComponent } from './routes/roles-access/roles-access.component'
import { UsersComponent } from './routes/users/users.component'

const routes: Routes = [
  {
    path: '',
    component: UsersComponent,
    children: [
      {
        path: ':role/roles-access',
        component: RolesAccessComponent,
      },
      {
        path: ':role/users',
        component: UsersComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccessRoutingModule { }
