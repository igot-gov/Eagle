import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { HomeComponent } from './routes/home/home.component'

import { RouterModule } from '@angular/router'
import { ApprovalsRoutingModule } from './approvals.routing.module'
import { BtnPageBackModule, ScrollspyLeftMenuModule } from '@ws-widget/collection'
import {
  MatSidenavModule, MatGridListModule, MatListModule, MatCardModule, MatFormFieldModule, MatInputModule,
  MatIconModule, MatButtonModule, MatRadioModule, MatDialogModule,
} from '@angular/material'
import { NeedsApprovalComponent } from './routes/needs-approval/needs-approval.component'
import { BasicInfoComponent } from './routes/basic-info/basic-info.component'
import { PositionComponent } from './routes/position/position.component'
import { EducationComponent } from './routes/education/education.component'
import { FormsModule } from '@angular/forms'
import { CertificationAndSkillsComponent } from './routes/certification-and-skills/certification-and-skills.component'
@NgModule({
  declarations: [HomeComponent, NeedsApprovalComponent, BasicInfoComponent, PositionComponent,
    EducationComponent, CertificationAndSkillsComponent],
  imports: [
    CommonModule, RouterModule, ApprovalsRoutingModule, BtnPageBackModule,
    MatSidenavModule, MatListModule, ScrollspyLeftMenuModule, MatCardModule, FormsModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatGridListModule,
    MatRadioModule, MatDialogModule,
  ],
})
export class ApprovalsModule { }
