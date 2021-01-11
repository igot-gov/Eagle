import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { EventsHomeComponent } from './routes/events-home/events-home.component'
import { EventsComponent } from './routes/events/events.component'
import { EventDetailComponent } from './routes/event-detail/event-detail.component'
import { EventRecentResolve } from './resolvers/event-resolve'
import { EventDetailResolve } from './resolvers/event-detail-resolve'

const routes: Routes = [
  {
    path: '',
    component: EventsHomeComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: 'home',
        component: EventsComponent,
        resolve: {
          topics: EventRecentResolve,
        },
      },
      {
        path: 'home/:eventId',
        component: EventDetailComponent,
        resolve: {
          topic: EventDetailResolve,
        },
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    EventRecentResolve,
    EventDetailResolve,
  ],
})
export class EventsRoutingModule { }
