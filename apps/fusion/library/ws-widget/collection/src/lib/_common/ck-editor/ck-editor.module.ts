import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CkEditorComponent } from './ck-editor.component'
import { CKEditorModule } from 'ng2-ckeditor'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { CKEditorService } from './ck-editor.service'

@NgModule({
  declarations: [CkEditorComponent],
  imports: [
    CommonModule,
    CKEditorModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [
    CkEditorComponent,
  ],
  entryComponents: [
    CkEditorComponent,
  ],
  providers: [CKEditorService],
})
export class CkEditorModule { }
