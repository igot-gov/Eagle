import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UITableComponent } from './components/user-list/ui-table.component'
import { MatTableModule } from '@angular/material/table'
import { MatTooltipModule } from '@angular/material/tooltip'
import { MatSortModule } from '@angular/material/sort'

@NgModule({
  declarations: [UITableComponent],
  imports: [
    CommonModule,
    MatTableModule,
    MatTooltipModule,
    MatSortModule,
  ],

  exports: [UITableComponent],
})
export class UITableModule { }
