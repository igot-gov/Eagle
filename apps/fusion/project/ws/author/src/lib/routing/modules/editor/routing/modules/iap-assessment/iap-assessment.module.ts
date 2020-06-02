import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { MatPaginatorModule, MatTableModule, MatSliderModule } from '@angular/material'
import { ConfirmDialogComponent } from '@ws/author/src/lib/modules/shared/components/confirm-dialog/confirm-dialog.component'
import { SharedModule } from '../../../../../../modules/shared/shared.module'
import { AuthViewerModule } from '../../../../../../modules/viewer/viewer.module'
import { EditorContentService } from '../../../services/editor-content.service'
import { EditorService } from '../../../services/editor.service'
import { PlainCKEditorComponent } from '../../../shared/components/plain-ckeditor/plain-ckeditor.component'
import { EditorSharedModule } from '../../../shared/shared.module'
import { IapAssessmentService } from '../iap-assessment/services/iap-assessment.service'
import { GeneralDetailsComponent } from './components/general-details/general-details.component'
import { IapAssessmentComponent } from './components/iap-assessment/iap-assessment.component'
import { SectionDialogComponent } from './components/section-dialog/section-dialog.component'
import { ViewQuestionDialogComponent } from './components/view-question-dialog/view-question-dialog.component'
import { IapAssessmentRoutingModule } from './iap-assessment.routing.module'
import { ViewDndQuestionComponent } from './components/view-dnd-question/view-dnd-question.component'
import { ViewHotspotQuestionComponent } from './components/view-hotspot-question/view-hotspot-question.component'
import { ViewSliderQuestionComponent } from './components/view-slider-question/view-slider-question.component'

@NgModule({
  declarations: [
    IapAssessmentComponent,
    SectionDialogComponent,
    ViewQuestionDialogComponent,
    GeneralDetailsComponent,
    ViewDndQuestionComponent,
    ViewHotspotQuestionComponent,
    ViewSliderQuestionComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    EditorSharedModule,
    IapAssessmentRoutingModule,
    AuthViewerModule,
    MatPaginatorModule,
    MatTableModule,
    MatSliderModule,
  ],
  exports: [GeneralDetailsComponent],
  providers: [EditorContentService, EditorService, PlainCKEditorComponent, IapAssessmentService],
  entryComponents: [IapAssessmentComponent, SectionDialogComponent, ViewQuestionDialogComponent, ConfirmDialogComponent],
})
export class IapAssessmentModule { }
