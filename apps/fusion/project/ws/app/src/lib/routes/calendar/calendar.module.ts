import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CalendarRoutingModule } from './calendar-routing.module'
import { BtnPageBackModule } from '@ws-widget/collection'
import {
  MatToolbarModule,
  MatDialogModule,
  MatExpansionModule,
  MatProgressSpinnerModule,
  MatButtonModule,
} from '@angular/material'
import { PathfindersCalendarComponent } from './components/pathfinders-calendar/pathfinders-calendar.component'
import { ViewEventsComponent } from './components/view-events/view-events.component'
@NgModule({
  entryComponents: [ViewEventsComponent],
  declarations: [PathfindersCalendarComponent, ViewEventsComponent],
  imports: [
    CommonModule,
    CalendarRoutingModule,
    MatToolbarModule,
    BtnPageBackModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatButtonModule,
  ],
})
export class CalendarModule { }
