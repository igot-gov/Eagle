import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UITableComponent } from './components/user-list/ui-table.component'
import { UserListDisplayComponent } from './components/user-list-display/user-list-display.component'
import { MatTableModule } from '@angular/material/table'
import { MatTooltipModule } from '@angular/material/tooltip'
import { MatSortModule } from '@angular/material/sort'

@NgModule({
  declarations: [UITableComponent, UserListDisplayComponent],
  imports: [
    CommonModule,
    MatTableModule,
    MatTooltipModule,
    MatSortModule,
  ],
})
export class UITableModule { }
