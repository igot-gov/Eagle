import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { SignupAutoComponent } from './signup-auto.component'

const routes: Routes = [
  {
    path: '',
    component: SignupAutoComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SignupAutoRoutingModule { }
