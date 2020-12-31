import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeMetaPipe } from './pipe-table-meta.pipe'

@NgModule({
  declarations: [PipeMetaPipe],
  imports: [
    CommonModule,
  ],
  exports: [PipeMetaPipe],
})
export class PipeTableMetaModule { }
