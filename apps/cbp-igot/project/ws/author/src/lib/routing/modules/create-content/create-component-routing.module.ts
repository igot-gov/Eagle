import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { CreateHomeComponent } from './components/create-home/create-home.component'

const routes: Routes = [
  {
    path: 'create-content',
    component: CreateHomeComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: CreateHomeComponent,
      }
    ]
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CreateComponentRoutingModule { }
