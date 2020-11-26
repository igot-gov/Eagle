import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CardTableComponent } from './card-table.component'
import { HorizontalScrollerModule, PipeCountTransformModule, PipeDurationTransformModule } from '@ws-widget/utils'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { PipeTableListModule } from './pipe-table-list/pipe-table-list.module'
import {
  MatButtonModule,
  MatIconModule,
  MatTooltipModule,
  MatProgressSpinnerModule,
  MatTableModule,
  MatSortModule,
  MatMenuModule,
  MatCardModule,
} from '@angular/material'
import { RouterModule } from '@angular/router'
@NgModule({
  declarations: [CardTableComponent],
  imports: [
    CommonModule,
    HorizontalScrollerModule,
    WidgetResolverModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatMenuModule,
    MatSortModule,
    PipeTableListModule,
    RouterModule,
    MatCardModule,
    PipeDurationTransformModule,
    PipeCountTransformModule,
  ],
  exports: [
    CardTableComponent,
  ],
  // entryComponents: [CardTableComponent],
})
export class CardTableModule { }
