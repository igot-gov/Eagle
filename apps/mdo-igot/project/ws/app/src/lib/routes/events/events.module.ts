import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CreateEventComponent } from './routes/create-event/create-event.component'
import { ViewEventComponent } from './routes/view-event/view-event.component'
import { ListEventComponent } from './routes/list/list-event.component'
import { ParticipantsComponent } from './components/participants/participants.component'
// import { EventListViewComponent } from './components/event-list-view/event-list-view.component'
import { RouterModule } from '@angular/router'
import { EventsRoutingModule } from './events.routing.module'
import { BtnPageBackModule, ScrollspyLeftMenuModule } from '@ws-widget/collection'
import {
  MatSidenavModule, MatGridListModule, MatListModule, MatCardModule, MatFormFieldModule, MatInputModule,
  MatIconModule, MatButtonModule, MatRadioModule, MatDialogModule, MatSelectModule, MatDatepickerModule,
  MatTableModule, MatCheckboxModule, MatNativeDateModule, MatSortModule, MatAutocompleteModule,
} from '@angular/material'
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'

@NgModule({
  declarations: [CreateEventComponent, ViewEventComponent, ParticipantsComponent, ListEventComponent],
  imports: [
    CommonModule, RouterModule, EventsRoutingModule, BtnPageBackModule,
    MatSidenavModule, MatListModule, ScrollspyLeftMenuModule, MatCardModule, FormsModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatGridListModule,
    MatRadioModule, MatDialogModule, MatSelectModule, MatDatepickerModule, ReactiveFormsModule,
    MatTableModule, MatCheckboxModule, MatNativeDateModule, MatSortModule, MatAutocompleteModule,
  ],
  entryComponents: [ParticipantsComponent],
  providers: [
     { provide: MAT_DIALOG_DATA, useValue: {} },
     { provide: MatDialogRef, useValue: {} },
     MatDatepickerModule, MatNativeDateModule,
 ],
})
export class EventsModule { }
