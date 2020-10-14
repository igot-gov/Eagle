import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { ProfileComponent } from './routes/profile/profile.component'
// import { InitResolver } from './resol./routes/profile-v2/discuss-all.component'
import { Profilev2Resolve } from './resolvers/profile-v2-resolve'
import { ProfileViewComponent } from './routes/profile-view/profile-view.component'

const routes: Routes = [
  {
    path: '',
    component: ProfileComponent,
    children: [
      {
        path: ':userId',
        component: ProfileViewComponent,
        resolve: {
          profile: Profilev2Resolve,
        },
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    Profilev2Resolve,
  ],
})
export class ProfileV2RoutingModule { }
