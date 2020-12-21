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
        path: 'users',
        component: UsersComponent,
      },
      {
        path: 'basicinfo',
        component: CreateMdoComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CreateMDORoutingModule { }
