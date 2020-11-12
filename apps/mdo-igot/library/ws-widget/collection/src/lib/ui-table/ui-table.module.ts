import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UIUserTableComponent } from './user-list/ui-user-table.component'
import { MatTableModule } from '@angular/material/table'
import { MatTooltipModule } from '@angular/material/tooltip'
import { MatSortModule } from '@angular/material/sort'

@NgModule({
  declarations: [UIUserTableComponent],
  imports: [
    CommonModule,
    MatTableModule,
    MatTooltipModule,
    MatSortModule,
  ],

  exports: [UIUserTableComponent],
})
export class UITableModule { }
