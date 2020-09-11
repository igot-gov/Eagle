import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { DiscussComponent } from './components/discuss.component'

const routes: Routes = [
  {
    path: '',
    component: DiscussComponent,
  },

]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DiscussRoutingModule { }
