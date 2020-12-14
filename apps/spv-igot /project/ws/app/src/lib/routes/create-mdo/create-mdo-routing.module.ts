import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { HomeComponent } from './routes/home/home.component'
import { CreateMdoComponent } from './routes/create-mdo/create-mdo.component'
import { UsersComponent } from './routes/users/users.component'

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: ':role/create-department',
        component: CreateMdoComponent,
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
export class CreateMDORoutingModule { }
