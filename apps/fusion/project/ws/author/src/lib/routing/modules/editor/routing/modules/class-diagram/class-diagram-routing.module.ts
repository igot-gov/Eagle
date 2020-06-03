import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { ClassDiagramComponent } from './component/class-diagram/class-diagram.component'

const routes: Routes = [
  {
    path: '',
    component: ClassDiagramComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ClassDiagramRoutingModule { }
