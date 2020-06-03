import { NgModule } from '@angular/core'
import { RouterModule, Routes } from '@angular/router'
import { PageResolve } from '@ws-widget/utils'
import { ProfileComponent } from './profile.component'
import { FeatureUsageComponent } from './routes/analytics/routes/feature-usage/feature-usage.component'
import { LearningComponent } from './routes/analytics/routes/learning/learning.component'
import { PlansComponent } from './routes/analytics/routes/plans/plans.component'
import { RefactoringComponent } from './routes/analytics/routes/refactoring/refactoring.component'
import { AchievementsComponent } from './routes/competency/components/achievements/achievements.component'
import { CardDetailComponent } from './routes/competency/components/card-detail/card-detail.component'
import { CompetencyHomeComponent } from './routes/competency/components/competency-home/competency-home.component'
import { CompetencyResolverService } from './routes/competency/resolver/assessment.resolver'
import { DashboardComponent } from './routes/dashboard/components/dashboard/dashboard.component'
import { InterestComponent } from './routes/interest/components/interest/interest.component'
import { InterestUserResolve } from './routes/interest/resolvers/interest-user.resolve'
import { LearningHistoryComponent } from './routes/learning/components/learning-history/learning-history.component'
import { LearningHomeComponent } from './routes/learning/components/learning-home/learning-home.component'
import { LearningTimeComponent } from './routes/learning/components/learning-time/learning-time.component'
import { LearningHistoryResolver } from './routes/learning/resolvers/learning-history.resolver'
import { LearningTimeResolver } from './routes/learning/resolvers/learning-time.resolver'
import { SettingsComponent } from './routes/settings/settings.component'
import { SkillGuard } from './routes/skills/guards/skill.guard'
import { AddLearningPathComponent } from './routes/skills/routes/add-learning-path/add-learning-path.component'
import { AddRoleComponent } from './routes/skills/routes/add-role/add-role.component'
import { AddSkillComponent } from './routes/skills/routes/add-skill/add-skill.component'
import { AllRolesComponent } from './routes/skills/routes/all-roles/all-roles.component'
import { AllSkillsComponent } from './routes/skills/routes/all-skills/all-skills.component'
// tslint:disable-next-line:max-line-length
import { ApproveProjectEndorsementComponent } from './routes/skills/routes/approve-project-endorsement/approve-project-endorsement.component'
import { CopyRoleComponent } from './routes/skills/routes/copy-role/copy-role.component'
import { CopySkillComponent } from './routes/skills/routes/copy-skill/copy-skill.component'
import { CreateProjectEndorsementComponent } from './routes/skills/routes/create-project-endorsement/create-project-endorsement.component'
import { DeleteRoleComponent } from './routes/skills/routes/delete-role/delete-role.component'
import { EditLearningPathComponent } from './routes/skills/routes/edit-learning-path/edit-learning-path.component'
import { EditRoleComponent } from './routes/skills/routes/edit-role/edit-role.component'
import { EditSkillComponent } from './routes/skills/routes/edit-skill/edit-skill.component'
import { ProjectEndorsementComponent } from './routes/skills/routes/project-endorsement/project-endorsement.component'
import { RoleDetailsComponent } from './routes/skills/routes/role-details/role-details.component'
import { RolesComponent } from './routes/skills/routes/roles/roles.component'
import { ShareRoleComponent } from './routes/skills/routes/share-role/share-role.component'
import { SkillDetailsComponent } from './routes/skills/routes/skill-details/skill-details.component'
import { SkillsHomeComponent } from './routes/skills/routes/skills-home/skills-home.component'
import { SkillsComponent } from './routes/skills/routes/skills/skills.component'
import { BadgesComponent } from './routes/badges/badges.component'
import { BadgesResolver } from './routes/badges/badges.resolver'
import { EndorsedSkillsComponent } from './routes/skills/routes/endorsed-skills/endorsed-skills.component'
import { MySkillsComponent } from './routes/skills/routes/my-skills/my-skills.component'
import { PendingSkillsComponent } from './routes/skills/routes/pending-skills/pending-skills.component'
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    data: {
      pageType: 'feature',
      pageKey: 'profile',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'competency',
    component: CompetencyHomeComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'assessment',
      },
      {
        path: 'badges',
        component: BadgesComponent,
        resolve: {
          badges: BadgesResolver,
        },
      },
      {
        path: ':type',
        component: AchievementsComponent,
        resolve: {
          competencyData: CompetencyResolverService,
        },
      },
      {
        path: ':type/details',
        component: CardDetailComponent,
      },
    ],
    data: {
      pageType: 'feature',
      pageKey: 'profile',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'learning',
    component: LearningHomeComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'time',
      },
      {
        path: 'time',
        component: LearningTimeComponent,
        resolve: {
          timeSpentData: LearningTimeResolver,
          pageData: PageResolve,
        },
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        runGuardsAndResolvers: 'paramsOrQueryParamsChange',
      },
      {
        path: 'history',
        component: LearningHistoryComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          learningHistory: LearningHistoryResolver,
          pageData: PageResolve,
        },
      },
    ],
    data: {
      pageType: 'feature',
      pageKey: 'profile',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
  {
    path: 'interest',
    component: InterestComponent,
    resolve: {
      interests: InterestUserResolve,
    },
  },
  {
    path: 'skills',
    component: SkillsHomeComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'my-skills',
      },
      {
        path: 'roles',
        component: RolesComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'skill-details/:id',
        component: SkillDetailsComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'role-details/:id',
        component: RoleDetailsComponent,
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
        path: 'add-learning-path/:id/:levelId',
        component: AddLearningPathComponent,
        data: {
          pageType: 'feature',
          pageKey: 'search',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'edit-learning-path/:id/:levelId',
        component: EditLearningPathComponent,
        data: {
          pageType: 'feature',
          pageKey: 'search',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'all-roles',
        component: AllRolesComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'add-role',
        component: AddRoleComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'edit-role/:id',
        component: EditRoleComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'copy-role/:id',
        component: CopyRoleComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'assign-role/:id',
        component: ShareRoleComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'delete-role/:id',
        component: DeleteRoleComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
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
        path: 'edit-skill/:id',
        component: EditSkillComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'copy-skill/:id',
        component: CopySkillComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'my-skills',
        component: MySkillsComponent,
        children: [
          {
            path: '',
            pathMatch: 'full',
            redirectTo: 'skills',
          },
          {
            path: 'endorsed-skills',
            component: EndorsedSkillsComponent,
            data: {
              pageType: 'feature',
              pageKey: 'profile',
            },
            resolve: {
              pageData: PageResolve,
            },
          },
          {
            path: 'pending-skills',
            component: PendingSkillsComponent,
            data: {
              pageType: 'feature',
              pageKey: 'profile',
            },
            resolve: {
              pageData: PageResolve,
            },
          },
          {
            path: 'endorsement',
            component: ProjectEndorsementComponent,
            data: {
              pageType: 'feature',
              pageKey: 'profile',
            },
            resolve: {
              pageData: PageResolve,
            },
          },
          {
            path: 'skills',
            component: SkillsComponent,
            data: {
              pageType: 'feature',
              pageKey: 'profile',
            },
            resolve: {
              pageData: PageResolve,
            },
          },
        ],
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'create-endorsement/:skillId/:skillLevelId',
        component: CreateProjectEndorsementComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
      {
        path: 'approve-endorsement',
        component: ApproveProjectEndorsementComponent,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
    ],
    data: {
      pageType: 'feature',
      pageKey: 'profile',
    },
    resolve: {
      pageData: PageResolve,
    },
    canActivate: [SkillGuard],
  },
  {
    path: 'refactoring',
    component: RefactoringComponent,
  },
  {
    path: 'plans',
    component: PlansComponent,
  },
  {
    path: 'collaborators',
    component: LearningComponent,
  },
  {
    path: 'feature-usage',
    component: FeatureUsageComponent,
  },
  {
    path: 'settings',
    component: SettingsComponent,
  },
]

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: ProfileComponent,
        children: routes,
        data: {
          pageType: 'feature',
          pageKey: 'profile',
        },
        resolve: {
          pageData: PageResolve,
        },
      },
    ]),
  ],
  exports: [RouterModule],
})
export class ProfileRoutingModule { }
