import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { SkillsComponent } from './routes/skills/skills.component'
import { SkillDetailsComponent } from './routes/skill-details/skill-details.component'
import { RoleDetailsComponent } from './routes/role-details/role-details.component'
import { RolesComponent } from './routes/roles/roles.component'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { CompetencyCardComponent } from './components/competency-card/competency-card.component'
import { AvailableCardComponent } from './components/available-card/available-card.component'
import {
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  MatListModule,
  MatRippleModule,
  MatSidenavModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,
  MatGridListModule,
  MatSnackBarModule,
  MatExpansionModule,
  MatSelectModule,
  MatProgressSpinnerModule,
  MatDatepickerModule,
  MatStepperModule,
  MatTableModule,
  MatFormFieldModule,
  MatInputModule,
  MatAutocompleteModule,
  MatChipsModule,
  MatMenuModule,
  MatDialogModule,
  MatSlideToggleModule,
  MatPaginatorModule,
  MatRadioModule,
} from '@angular/material'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { RouterModule } from '@angular/router'
import {
  HorizontalScrollerModule,
  DefaultThumbnailModule,
  PipeLimitToModule,
} from '@ws-widget/utils'
import { SkillsHomeComponent } from './routes/skills-home/skills-home.component'
import { CardSkillComponent } from './components/card-skill/card-skill.component'
import { AllSkillsComponent } from './routes/all-skills/all-skills.component'
import { ShareRoleComponent } from './routes/share-role/share-role.component'
import { EditRoleComponent } from './routes/edit-role/edit-role.component'
import { DeleteRoleComponent } from './routes/delete-role/delete-role.component'
import { AddSkillComponent } from './routes/add-skill/add-skill.component'
import { EditSkillComponent } from './routes/edit-skill/edit-skill.component'
import { ProjectEndorsementComponent } from './routes/project-endorsement/project-endorsement.component'
import { CreateProjectEndorsementComponent } from './routes/create-project-endorsement/create-project-endorsement.component'
import { ApproveProjectEndorsementComponent } from './routes/approve-project-endorsement/approve-project-endorsement.component'
import { UserAutocompleteModule } from '@ws-widget/collection'
import { AllRolesComponent } from './routes/all-roles/all-roles.component'
import { CopyRoleComponent } from './routes/copy-role/copy-role.component'
import { CopySkillComponent } from './routes/copy-skill/copy-skill.component'
import { LineGraphComponent } from './components/line-graph/line-graph.component'
import { ApproveEndorsementRequestComponent } from './components/approve-endorsement-request/approve-endorsement-request.component'
import { RejectEndorsementRequestComponent } from './components/reject-endorsement-request/reject-endorsement-request.component'
import { AddRoleComponent } from './routes/add-role/add-role.component'
import { AddLearningPathComponent } from './routes/add-learning-path/add-learning-path.component'
import { EditLearningPathComponent } from './routes/edit-learning-path/edit-learning-path.component'
import { EndorsedSkillsComponent } from './routes/endorsed-skills/endorsed-skills.component'
import { MySkillsComponent } from './routes/my-skills/my-skills.component'
import { PendingSkillsComponent } from './routes/pending-skills/pending-skills.component'
import { CourseCardComponent } from './components/course-card/course-card.component'
@NgModule({
  declarations: [
    SkillsComponent,
    SkillDetailsComponent,
    RoleDetailsComponent,
    RolesComponent,
    CompetencyCardComponent,
    AvailableCardComponent,
    SkillsHomeComponent,
    CardSkillComponent,
    AllSkillsComponent,
    ShareRoleComponent,
    EditRoleComponent,
    DeleteRoleComponent,
    AddSkillComponent,
    EditSkillComponent,
    ProjectEndorsementComponent,
    CreateProjectEndorsementComponent,
    ApproveProjectEndorsementComponent,
    AllRolesComponent,
    CopyRoleComponent,
    CopySkillComponent,
    LineGraphComponent,
    ApproveEndorsementRequestComponent,
    RejectEndorsementRequestComponent,
    AddRoleComponent,
    AddLearningPathComponent,
    EditLearningPathComponent,
    EndorsedSkillsComponent,
    MySkillsComponent,
    PendingSkillsComponent,
    CourseCardComponent,
  ],
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatListModule,
    MatRippleModule,
    MatSidenavModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatGridListModule,
    MatSnackBarModule,
    MatExpansionModule,
    MatSelectModule,
    RouterModule,
    WidgetResolverModule,
    FormsModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    HorizontalScrollerModule,
    DefaultThumbnailModule,
    MatStepperModule,
    MatTableModule,
    PipeLimitToModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatChipsModule,
    MatMenuModule,
    MatDialogModule,
    UserAutocompleteModule,
    MatSlideToggleModule,
    MatPaginatorModule,
    MatRadioModule,
  ],
  entryComponents: [
    ApproveEndorsementRequestComponent,
    RejectEndorsementRequestComponent,
    DeleteRoleComponent,
  ],
})
export class SkillsModule {}
