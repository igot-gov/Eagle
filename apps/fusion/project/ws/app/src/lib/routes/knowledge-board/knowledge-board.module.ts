import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { KnowledgeBoardRoutingModule } from './knowledge-board-routing.module'
import { KbHomeComponent } from './routes/kb-home/kb-home.component'
import {
  MatToolbarModule,
  MatIconModule,
  MatCardModule,
  MatButtonModule,
  MatTooltipModule,
  MatDividerModule,
  MatProgressSpinnerModule,
  MatSelectModule,
  MatInputModule,
  MatCheckboxModule,
  MatDialogModule,
  MatFormFieldModule,
  // MAT_SNACK_BAR_DEFAULT_OPTIONS
} from '@angular/material'
import {
  BtnPageBackModule,
  CardKnowledgeModule,
  BtnFollowModule,
  UserImageModule,
  BtnMailUserModule,
  PickerContentModule,
  BtnContentShareModule,
  DisplayContentTypeModule,
  UserContentRatingModule,
  BtnContentFeedbackV2Module,
  BtnContentFeedbackModule,
} from '@ws-widget/collection'

import {
  HorizontalScrollerModule,
  PipeLimitToModule,
  PipeDurationTransformModule,
  PipeCountTransformModule,
  PipePartialContentModule,
} from '@ws-widget/utils'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { KbDetailComponent } from './routes/kb-detail/kb-detail.component'
import { FormsModule } from '@angular/forms'
import { DragDropModule } from '@angular/cdk/drag-drop'
import { MatExpansionModule } from '@angular/material/expansion'
import { DeleteContentDialogComponent } from './routes/kb-detail/components/delete-content-dialog/delete-content-dialog.component'
@NgModule({
  declarations: [KbHomeComponent, KbDetailComponent, DeleteContentDialogComponent],
  imports: [
    CommonModule,
    FormsModule,
    KnowledgeBoardRoutingModule,
    BtnPageBackModule,
    HorizontalScrollerModule,
    WidgetResolverModule,
    CardKnowledgeModule,
    PipeLimitToModule,
    PipeCountTransformModule,
    BtnFollowModule,
    BtnContentShareModule,
    UserImageModule,
    BtnMailUserModule,
    BtnContentFeedbackV2Module,

    BtnContentFeedbackModule,

    DisplayContentTypeModule,
    PipeDurationTransformModule,
    PipePartialContentModule,
    UserContentRatingModule,

    // Material Imports
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatToolbarModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatExpansionModule,
    MatDialogModule,
    PickerContentModule,
    DragDropModule,
  ],
  entryComponents: [DeleteContentDialogComponent],
  // providers: [
  //   { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 8000 } },
  // ],
})
export class KnowledgeBoardModule { }
