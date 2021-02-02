import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { UsersComponent } from './routes/users/users.component'

const routes: Routes = [
  {
    path: '',
    component: UsersComponent,
    children: [
      {
        path: ':roleId/users',
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
