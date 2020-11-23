import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { MatCheckboxModule, MatCardModule, MatGridListModule } from '@angular/material'
import { GroupCheckboxComponent } from './group-checkbox.component'
import { MatIconModule } from '@angular/material/icon'
@NgModule({
  declarations: [GroupCheckboxComponent],
  imports: [
    CommonModule,
    MatCheckboxModule,
    MatCardModule,
    MatGridListModule,
    MatIconModule,
  ],
  exports: [GroupCheckboxComponent],
})
export class GroupCheckboxModule { }
