import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { CompetenceComponent } from './routes/competence-home/competence.component'
import { CompetenceAllComponent } from './routes/competence-all/competence-all.component'
import { InitResolver } from './resolvers/init-resolve.service'


const routes: Routes = [
  {
    path: '',
    component: CompetenceComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: 'home',
        component: CompetenceAllComponent,
        resolve: {},
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    InitResolver
  ],
})
export class CompetencieRoutingModule { }
