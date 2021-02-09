import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { HomeComponent } from './routes/home/home.component'
import { PrivilegesComponent } from './routes/privileges/privileges.component'
import { UsersComponent } from './routes/users/users.component'

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: ':role/privileges',
        component: PrivilegesComponent,
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
