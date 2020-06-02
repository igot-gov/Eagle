import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { CompassRoleComponent } from './routes/compass-role/compass-role.component'
import { LearningPathComponent } from './routes/learning-path/learning-path.component'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: CompassRoleComponent,
  },
  // {
  //   path: ':roleId',
  //   component: CompassRoleComponent,
  // },
  {
    path: 'lp/:id',
    component: LearningPathComponent,
  },
]
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CompassRolesRoutingModule { }
