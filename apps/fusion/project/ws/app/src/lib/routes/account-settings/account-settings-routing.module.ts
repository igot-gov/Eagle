import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AccountSettingsComponent } from './components/account-settings/account-settings.component'
import { ViewprofileResolverService } from './services/viewprofile-resolver.service'
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: AccountSettingsComponent,
    resolve: {
      viewprofile: ViewprofileResolverService,
    },

  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountSettingsRoutingModule { }
