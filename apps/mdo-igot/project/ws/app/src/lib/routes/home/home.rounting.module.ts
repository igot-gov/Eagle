import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
// import { InitResolver } from './resol./routes/profile-v2/discuss-all.component'
import { HomeResolve } from './resolvers/home-resolve'
import { AboutComponent } from './routes/about/about.component'
import { HomeComponent } from './routes/home/home.component'
import { UsersViewComponent } from './routes/users-view/users-view.component'
const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    redirectTo: '',
    children: [
      {
        path: '',
        component: UsersViewComponent,
        resolve: {
          profile: HomeResolve,
        },
      },
      {
        path: 'about',
        component: AboutComponent,
        // resolve: {
        //   profile: HomeResolve,
        // },
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
