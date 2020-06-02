import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { InfosysagmComponent } from './infosysagm/infosysagm.component'

const routes: Routes = [
  {
    path: '',
    component: InfosysagmComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InfosysagmRoutingModule {}
