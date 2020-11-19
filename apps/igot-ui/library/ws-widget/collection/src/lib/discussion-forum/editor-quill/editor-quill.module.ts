import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { EditorQuillComponent } from './editor-quill.component'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { QuillModule } from 'ngx-quill'

const SUPPORTED_FORMATS = [
  'background',
  'bold',
  'color',
  'font',
  'code',
  'italic',
  'link',
  'size',
  'strike',
  'script',
  'underline',
  'blockquote',
  'header',
  'indent',
  'list',
  'align',
  'direction',
  'code-block',
]

@NgModule({
  declarations: [EditorQuillComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    QuillModule.forRoot({
      formats: SUPPORTED_FORMATS,
      modules: {
        toolbar: [['blockquote', 'code-block'], ['bold', 'italic', 'underline', 'link']],
        history: {
          delay: 1500,
          userOnly: true,
        },
        syntax: false,
      },
      // placeholder: 'Ask a question, or add something you found helpful',
      theme: 'bubble',
    }),
  ],
  exports: [EditorQuillComponent],
})
export class EditorQuillModule { }
