import { NgModule } from '@angular/core'
import { CommonModule, DatePipe } from '@angular/common'
import { CreateEventComponent } from './routes/create-event/create-event.component'
import { ViewEventComponent } from './routes/view-event/view-event.component'
import { ListEventComponent } from './routes/list/list-event.component'
import { ParticipantsComponent } from './components/participants/participants.component'
import { SuccessComponent } from './components/success/success.component'
import { EventListViewComponent } from './components/event-list-view/event-list-view.component'
import { EventsHomeComponent } from './routes/events-home/events-home.component'
import { EventThumbnailComponent } from './components/event-thumbnail/event-thumbnail.component'
import { RouterModule } from '@angular/router'
import { EventsRoutingModule } from './events.routing.module'
import { BtnPageBackModule, ScrollspyLeftMenuModule } from '@ws-widget/collection'
import { PipeFilterModule, PipeHtmlTagRemovalModule, PipeOrderByModule, PipeRelativeTimeModule } from '@ws-widget/utils'
import { MatExpansionModule } from '@angular/material/expansion'
import { MatDividerModule } from '@angular/material/divider'
import { WidgetResolverModule } from '@ws-widget/resolver'
import {
  MatSidenavModule, MatGridListModule, MatListModule, MatCardModule, MatFormFieldModule, MatInputModule,
  MatIconModule, MatButtonModule, MatRadioModule, MatDialogModule, MatSelectModule, MatDatepickerModule,
  MatTableModule, MatCheckboxModule, MatNativeDateModule, MatSortModule, MatAutocompleteModule,
  MatMenuModule, MatPaginatorModule,
} from '@angular/material'
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'

@NgModule({
  declarations: [
    CreateEventComponent,
    ViewEventComponent,
    ParticipantsComponent,
    ListEventComponent,
    EventListViewComponent,
    EventsHomeComponent,
    EventThumbnailComponent,
    SuccessComponent,
  ],
  imports:
  [
    CommonModule,
    RouterModule,
    EventsRoutingModule,
    BtnPageBackModule,
    MatSidenavModule,
    MatListModule,
    ScrollspyLeftMenuModule,
    MatCardModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatGridListModule,
    MatRadioModule,
    MatDialogModule,
    MatSelectModule,
    MatDatepickerModule,
    ReactiveFormsModule,
    MatTableModule,
    MatCheckboxModule,
    MatNativeDateModule,
    MatSortModule,
    MatAutocompleteModule,
    MatMenuModule,
    MatPaginatorModule,
    PipeFilterModule,
    PipeHtmlTagRemovalModule,
    PipeRelativeTimeModule,
    PipeOrderByModule,
    BtnPageBackModule,
    WidgetResolverModule,
    ScrollspyLeftMenuModule,
    MatRadioModule,
    MatExpansionModule,
    MatDividerModule,
  ],
  entryComponents: [
    ParticipantsComponent,
    EventThumbnailComponent,
    SuccessComponent,
  ],
  providers: [
    { provide: MAT_DIALOG_DATA, useValue: {} },
    { provide: MatDialogRef, useValue: {} },
    MatDatepickerModule,
    MatNativeDateModule,
    DatePipe,
  ],
})
export class EventsModule { }
