import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { HomeComponent } from './routes/home/home.component'
import { HomeResolve } from './resolvers/home-resolve'
import { WorkflowResolve } from './resolvers/workflow-resolve'
import { WorkflowHistoryResolve } from './resolvers/workflow-history-resolve'

const routes: Routes = [
  {
    path: ':userId/to-approve',
    component: HomeComponent,
    runGuardsAndResolvers: 'always',
    resolve: {
      profileData: HomeResolve,
      workflowData: WorkflowResolve,
      workflowHistoryData: WorkflowHistoryResolve,
    },
  },

]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    HomeResolve,
    WorkflowResolve,
    WorkflowHistoryResolve,
  ],
})
export class ApprovalsRoutingModule { }
