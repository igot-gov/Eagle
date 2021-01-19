import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { ViewEventComponent } from './routes/view-event/view-event.component'
import { CreateEventComponent } from './routes/create-event/create-event.component'
import { ListEventComponent } from './routes/list/list-event.component'
import { EventsHomeComponent } from './routes/events-home/events-home.component'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'list',
  },
  {
    path: '',
    component: EventsHomeComponent,
    children: [
      {
        path: 'list',
        component: ListEventComponent,
      },
      {
        path: ':eventId/details',
        component: ViewEventComponent,
      },
    ],
  },
  {
    path: 'create-event',
    component: CreateEventComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [],
})
export class EventsRoutingModule {

}
