import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
// import { InitResolver } from './resol./routes/profile-v2/discuss-all.component'
import { HomeResolve } from './resolvers/home-resolve'
import { AboutComponent } from './routes/about/about.component'
import { HomeComponent } from './routes/home/home.component'
import { UsersViewComponent } from './routes/users-view/users-view.component'
import { RolesAccessComponent } from './routes/roles-access/roles-access.component'
import { DirectoryViewComponent } from './routes/directory/directroy.component'
import { CreateMdoComponent } from './routes/create-mdo/create-mdo.component'
import { CreateUserComponent } from './routes/create-user/create-user.component'
import { DepartmentResolve } from './resolvers/department-resolve'
// import { PageResolve } from '@ws-widget/utils'
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'directory',
  },
  {
    path: '',
    component: HomeComponent,
    resolve: {
      department: DepartmentResolve,
    },
    children: [
      {
        path: 'users',
        component: UsersViewComponent,
        children: [],
      },
      {
        path: 'about',
        component: AboutComponent,
      },
      {
        path: 'roles-access',
        component: RolesAccessComponent,
      },
      {
        path: 'directory',
        component: DirectoryViewComponent,
      },
      {
        path: ':department/create-department',
        component: CreateMdoComponent,
      },
      {
        path: 'create-user',
        component: CreateUserComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    HomeResolve,
    DepartmentResolve,
  ],
})
export class HomeRoutingModule { }
