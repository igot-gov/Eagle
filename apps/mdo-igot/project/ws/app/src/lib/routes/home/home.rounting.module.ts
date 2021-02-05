import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
// import { InitResolver } from './resol./routes/profile-v2/discuss-all.component'
import { HomeResolve } from './resolvers/home-resolve'
import { DepartmentResolve } from './resolvers/department-resolve'
import { AboutComponent } from './routes/about/about.component'
import { HomeComponent } from './routes/home/home.component'
import { UsersViewComponent } from './routes/users-view/users-view.component'
import { RolesAccessComponent } from './routes/roles-access/roles-access.component'
// import { PageResolve } from '@ws-widget/utils'
import { ApprovalsComponent } from './routes/approvals/approvals.component'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'users',
  },
  {
    path: '',
    component: HomeComponent,
    resolve: {
      department: DepartmentResolve,
      tabs: HomeResolve,
    },
    children: [
      {
        path: 'users',
        component: UsersViewComponent,
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
        path: 'approvals',
        component: ApprovalsComponent,
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
