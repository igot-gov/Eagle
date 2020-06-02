import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { PersonProfileComponent } from './components/person-profile/person-profile.component'
const routes: Routes = []

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: PersonProfileComponent,
        children: routes,
      },
    ]),
  ],
  exports: [RouterModule],
})
export class PersonProfileRoutingModule { }
