import { NgModule } from '@angular/core'
import{ContentQualityCheckComponent} from './content-quality-check.component'
import {MatTableModule} from '@angular/material/table'
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatSelectModule} from '@angular/material/select'
import { CommonModule } from '@angular/common'

@NgModule({
  declarations: [ContentQualityCheckComponent],
  imports: [MatTableModule, MatFormFieldModule, MatSelectModule, CommonModule],
  exports:[ContentQualityCheckComponent],
  entryComponents: [ContentQualityCheckComponent],
})
export class ContentQualityCheckModule {}
