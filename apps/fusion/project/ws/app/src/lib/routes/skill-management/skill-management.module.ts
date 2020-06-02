import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { RouterModule } from '@angular/router'
import { SkillManagementComponent } from './skill-management.component'
import { SkillManagementRoutingModule } from './skill-management-routing.module'
import { AddSkillComponent } from './routes/add-skill/add-skill.component'
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
import { BtnPageBackModule } from '@ws-widget/collection'
import {
  HorizontalScrollerModule,
  DefaultThumbnailModule,
  PipeLimitToModule,
} from '@ws-widget/utils'
import { AddLearningPathComponent } from './routes/add-learning-path/add-learning-path.component'
import { CreatedSkillsComponent } from './routes/created-skills/created-skills.component'
import { AllSkillsComponent } from './routes/all-skills/all-skills.component'
import { CardSkillComponent } from './components/card-skill/card-skill.component'
import { ApproveEndorsementRequestComponent } from './components/approve-endorsement-request/approve-endorsement-request.component'
import { RejectEndorsementRequestComponent } from './components/reject-endorsement-request/reject-endorsement-request.component'

@NgModule({
  declarations: [
    SkillManagementComponent,
    AddSkillComponent,
    AddLearningPathComponent,
    CreatedSkillsComponent,
    AllSkillsComponent,
    CardSkillComponent,
    ApproveEndorsementRequestComponent,
    RejectEndorsementRequestComponent,
  ],
  imports: [
    CommonModule,
    SkillManagementRoutingModule,
    RouterModule,
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
    FormsModule,
    ReactiveFormsModule,
    BtnPageBackModule,
    HorizontalScrollerModule,
    DefaultThumbnailModule,
    PipeLimitToModule,
  ],
})
export class SkillManagementModule { }
