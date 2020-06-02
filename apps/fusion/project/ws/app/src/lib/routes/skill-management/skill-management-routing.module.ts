import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { SkillManagementComponent } from './skill-management.component'
import { AddSkillComponent } from './routes/add-skill/add-skill.component'
import { PageResolve } from '@ws-widget/utils'
import { AddLearningPathComponent } from './routes/add-learning-path/add-learning-path.component'
import { AllSkillsComponent } from './routes/all-skills/all-skills.component'
import { CreatedSkillsComponent } from './routes/created-skills/created-skills.component'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'add-skill',
  },
  {
    path: 'add-skill',
    component: AddSkillComponent,
    data: {
      pageType: 'feature',
      pageKey: 'profile',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'created-skills',
    component: CreatedSkillsComponent,
    data: {
      pageType: 'feature',
      pageKey: 'profile',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'all-skills',
    component: AllSkillsComponent,
    data: {
      pageType: 'feature',
      pageKey: 'profile',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'add-learning-path',
    component: AddLearningPathComponent,
    data: {
      pageType: 'feature',
      pageKey: 'search',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
]

@NgModule({
  imports: [RouterModule.forChild(
    [
      {
        path: '',
        component: SkillManagementComponent,
        children: routes,
      },
    ]
  )],
  exports: [RouterModule],
})
export class SkillManagementRoutingModule { }
