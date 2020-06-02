import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { KbHomeComponent } from './routes/kb-home/kb-home.component'
import { KbDetailComponent } from './routes/kb-detail/kb-detail.component'
import { KbDetailResolve } from './resolvers/kb-detail.resolve'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: 'home',
    component: KbHomeComponent,
  },
  {
    path: ':id',
    resolve: {
      content: KbDetailResolve,
    },
    component: KbDetailComponent,
    runGuardsAndResolvers: 'paramsOrQueryParamsChange',
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [KbDetailResolve],
})
export class KnowledgeBoardRoutingModule {}
