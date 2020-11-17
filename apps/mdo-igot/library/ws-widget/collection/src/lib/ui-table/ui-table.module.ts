import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UIUserTableComponent } from './user-list/ui-user-table.component'
import { MatTableModule } from '@angular/material/table'
import { MatTooltipModule } from '@angular/material/tooltip'
import { MatSortModule } from '@angular/material/sort'
import { MatIconModule } from '@angular/material/icon'
import { AppButtonComponent } from '../app-button/app-button.component'

@NgModule({
  declarations: [UIUserTableComponent, AppButtonComponent],
  imports: [
    CommonModule,
    MatTableModule,
    MatTooltipModule,
    MatSortModule,
    MatIconModule,
  ],

  exports: [UIUserTableComponent],
})
export class UITableModule { }
