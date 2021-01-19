import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { EventsModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    EventsModule,
  ],
  exports: [
    EventsModule,
  ],
})
export class RouteEventsAppModule { }
