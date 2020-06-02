import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { CalendarModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    CalendarModule,
  ],
  exports: [
    CalendarModule,
  ],
})
export class RouteCalendarModule { }
