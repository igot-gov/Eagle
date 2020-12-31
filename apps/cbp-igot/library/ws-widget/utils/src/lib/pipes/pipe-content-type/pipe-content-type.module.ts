import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeContentTypePipe } from './pipe-content-type.pipe'

@NgModule({
  declarations: [PipeContentTypePipe],
  imports: [
    CommonModule,
  ],
  exports: [PipeContentTypePipe],
})
export class PipeContentTypeModule { }
