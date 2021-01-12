import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { PageResolve } from '@ws-widget/utils'
import { UserProfileComponent } from './components/user-profile/user-profile.component'

const routes: Routes = [
  {
    path: 'details',
    component: UserProfileComponent,
    data: {
      pageType: 'feature',
      pageKey: 'edit-profile',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserProfileRoutingModule { }
