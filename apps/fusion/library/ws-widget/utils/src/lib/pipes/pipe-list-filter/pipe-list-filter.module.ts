import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeListFilterPipe } from './pipe-list-filter.pipe'

@NgModule({
  declarations: [PipeListFilterPipe],
  imports: [
    CommonModule,
  ],
  exports: [PipeListFilterPipe],
})
export class PipeListFilterModule { }
