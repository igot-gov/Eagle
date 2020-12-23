import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeFilterPipe } from './pipe-filter.pipe'

@NgModule({
  declarations: [PipeFilterPipe],
  imports: [
    CommonModule,
  ],
  exports: [PipeFilterPipe],
})
export class PipeFilterModule { }
