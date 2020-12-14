import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { FracComponent } from './components/frac/frac.component'

const routes: Routes = []

@NgModule({
  imports: [RouterModule.forChild([
    {
      path: '',
      component: FracComponent,
      children: routes,
    },
  ]),
  ],
})
export class FracRoutingModule { }
