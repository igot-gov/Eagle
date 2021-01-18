import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { AtGlanceModule, DisplayContentTypeModule } from '@ws-widget/collection'
import { DefaultThumbnailModule, PipeDurationTransformModule } from '@ws-widget/utils'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { AceEditorModule } from 'ng2-ace-editor'
import { CKEditorModule } from 'ng2-ckeditor'
import { CatalogSelectModule } from '../shared/components/catalog-select/catalog-select.module'
import { AceEditorComponent } from './components/ace-editor/ace-editor.component'
import { AuthEditorActionButtonsComponent } from './components/auth-editor-action-buttons/auth-editor-action-buttons.component'
import { AuthLanguageSelectBarComponent } from './components/auth-language-select-bar/auth-language-select-bar.component'
import { AuthPickerComponent } from './components/auth-picker/auth-picker.component'
import { AddThumbnailComponent } from './components/add-thumbnail/add-thumbnail.component'
import { EditMetaComponent } from './components/edit-meta/edit-meta.component'
import { PlainCKEditorComponent } from './components/plain-ckeditor/plain-ckeditor.component'
import { MatQuillComponent } from './components/rich-text-editor/my-own.component'
import { QuillComponent } from './components/rich-text-editor/quill.component'
import { DragDropDirective } from './directives/drag-drop.directive'
import { UploadService } from './services/upload.service'
import { MyContentService } from '../../my-content/services/my-content.service'
import { BaseComponent } from './components/editor/base/base.component'
import { EditMetaV2Component } from './components/editor/edit-meta-v2/edit-meta-v2.component'
import { LiveHtmlEditorComponent } from './components/live-html-editor/live-html-editor.component'
import { OptionsComponent } from './components/options/options.component'
import { FormsModule } from '@angular/forms'
import { CompetencyAddPopUpComponent } from './components/competency-add-popup/competency-add-popup'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { MatRadioModule, MatTableModule } from '@angular/material'
import { ContentQualityComponent } from './components/content-quality/content-quality.component'
import { RouterModule } from '@angular/router'
import { ContentSelfCurationComponent } from './components/content-self-curation/content-self-curation.component'
import { CurationProgressCardComponent } from './components/content-self-curation/curation-progress-card/curation-progress-card.component'
import { SelfCurationService } from './services/self-curation.service'
import { ContentSummaryComponent } from './components/content-summary/content-summary.component'
import { ContentQualityService } from './services/content-quality.service'
import { CompetenceViewComponent } from './components/edit-meta/competencies-view/competencies-view.component'
import { CompetenceCardComponent } from './components/edit-meta/competencies-card/competencies-card.component'
import { ConfirmModalComponent } from './components/confirm-modal/confirm-modal.component'
import { CurationDetailComponent } from './components/content-self-curation/curation-detail/curation-detail.component'

@NgModule({
  declarations: [
    MatQuillComponent,
    QuillComponent,
    PlainCKEditorComponent,
    EditMetaComponent,
    DragDropDirective,
    AceEditorComponent,
    AuthLanguageSelectBarComponent,
    AuthPickerComponent,
    AuthEditorActionButtonsComponent,
    BaseComponent,
    EditMetaV2Component,
    LiveHtmlEditorComponent,
    OptionsComponent,
    CompetencyAddPopUpComponent,
    ContentQualityComponent,
    ContentSelfCurationComponent,
    CurationProgressCardComponent,
    ContentSummaryComponent,
    CompetenceViewComponent,
    CompetenceCardComponent,
    AddThumbnailComponent,
    ConfirmModalComponent,
    CurationDetailComponent,
  ],
  imports: [
    CommonModule,
    DefaultThumbnailModule,
    PipeDurationTransformModule,
    DisplayContentTypeModule,
    CKEditorModule,
    FormsModule,
    SharedModule,
    AceEditorModule,
    CatalogSelectModule,
    WidgetResolverModule,
    MatRadioModule,
    RouterModule,
    MatTableModule,
    AtGlanceModule,
  ],
  exports: [
    MatQuillComponent,
    QuillComponent,
    PlainCKEditorComponent,
    EditMetaComponent,
    AddThumbnailComponent,
    DragDropDirective,
    AceEditorComponent,
    AuthEditorActionButtonsComponent,
    AuthPickerComponent,
    LiveHtmlEditorComponent,
    OptionsComponent,
    CompetencyAddPopUpComponent,
    ContentQualityComponent,
    ContentSelfCurationComponent,
    ContentSummaryComponent,
    CompetenceCardComponent,
    ConfirmModalComponent,
    CurationDetailComponent,
  ],
  entryComponents: [AuthPickerComponent, AddThumbnailComponent, CompetencyAddPopUpComponent,
    CompetenceViewComponent, ConfirmModalComponent, CurationDetailComponent],
  providers: [MyContentService, UploadService, SelfCurationService, ContentQualityService, AddThumbnailComponent],
})
export class EditorSharedModule { }
