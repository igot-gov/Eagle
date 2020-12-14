import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { HomeComponent } from './routes/home/home.component'

const routes: Routes = [
  {
    path: ':approve/to-approve',
    component: HomeComponent,
    // children: [
    //   {
    //     path: ':approve/to-approve',
    //     component: ToApproveComponent,
    //   }
    // ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ApprovalsRoutingModule { }
