import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeRelativeTimePipe } from './pipe-relative-time.pipe'

@NgModule({
  declarations: [PipeRelativeTimePipe],
  imports: [
    CommonModule,
  ],
  exports: [PipeRelativeTimePipe],
})
export class PipeRelativeTimeModule { }
