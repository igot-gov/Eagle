import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { RelativeUrlTablePipe } from './relative-url.pipe'

@NgModule({
  declarations: [RelativeUrlTablePipe],
  imports: [
    CommonModule,
  ],
  exports: [RelativeUrlTablePipe],
})
export class PipeRelativePathTableModule { }
