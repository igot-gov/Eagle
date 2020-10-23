import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { CompetenceComponent } from './routes/competence-home/competence.component'
import { CompetenceAllComponent } from './routes/competence-all/competence-all.component'
import { InitResolver } from './resolvers/init-resolve.service'
import { ProfileResolve } from './resolvers/profile-fetch'
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
        resolve: {
          profile: ProfileResolve,
        },
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    InitResolver,
    ProfileResolve,
  ],
})
export class CompetencieRoutingModule { }
