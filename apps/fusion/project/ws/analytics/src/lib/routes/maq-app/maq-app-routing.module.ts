import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { MaqAppComponent } from './maq-app.component'

const routes: Routes = [
  {
    path: ':tagName',
    component: MaqAppComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MaqAppRoutingModule { }
