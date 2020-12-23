import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeFilterSearchPipe } from './pipe-filter-search.pipe'

@NgModule({
  declarations: [PipeFilterSearchPipe],
  imports: [
    CommonModule,
  ],
  exports: [PipeFilterSearchPipe],
})
export class PipeFilterSearchModule { }
