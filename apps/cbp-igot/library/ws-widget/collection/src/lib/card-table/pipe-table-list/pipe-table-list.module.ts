import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeListPipe } from './pipe-table-list.pipe'

@NgModule({
  declarations: [PipeListPipe],
  imports: [
    CommonModule,
  ],
  exports: [PipeListPipe],
})
export class PipeTableListModule { }
