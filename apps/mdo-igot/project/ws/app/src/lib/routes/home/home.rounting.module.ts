import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
// import { InitResolver } from './resol./routes/profile-v2/discuss-all.component'
import { HomeResolve } from './resolvers/home-resolve'
import { AboutComponent } from './routes/about/about.component'
import { HomeComponent } from './routes/home/home.component'
import { UsersViewComponent } from './routes/users-view/users-view.component'
import { RolesAccessComponent } from './routes/roles-access/roles-access.component'
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'users',
  },
  {
    path: '',
    component: HomeComponent,
    // redirectTo: '',
    // resolve: {
    // profile: HomeResolve,
    // },
    children: [
      {
        path: 'users',
        component: UsersViewComponent,
        resolve: {
          // profile: HomeResolve,
        },
        children: [],
      },
      {
        path: 'about',
        component: AboutComponent,
        // resolve: {
        //   profile: HomeResolve,
        // },
      },
      {
        path: 'roles-access',
        component: RolesAccessComponent,
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    HomeResolve,
  ],
})
export class HomeRoutingModule { }
