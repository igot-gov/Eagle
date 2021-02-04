import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { ViewUserComponent } from './routes/view-user/view-user.component'
import { CreateUserComponent } from './routes/create-user/create-user.component'
import { DepartmentResolve } from '../users/resolvers/department-resolve'
import { UserResolve } from '../users/resolvers/user-resolve'
import { WorkflowHistoryResolve } from '../users/resolvers/workflow-history-resolve'

const routes: Routes = [
  {
    path: ':userId/details',
    component: ViewUserComponent,
    resolve: {
      profileData: UserResolve,
      workflowHistoryData: WorkflowHistoryResolve,
    },
  },
  {
    path: 'create-user',
    component: CreateUserComponent,
    resolve: {
      department: DepartmentResolve,
    },
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    UserResolve,
    DepartmentResolve,
    WorkflowHistoryResolve,
  ],
})
export class UsersRoutingModule { }
