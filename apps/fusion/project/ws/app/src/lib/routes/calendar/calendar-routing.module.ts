import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { PathfindersCalendarComponent } from './components/pathfinders-calendar/pathfinders-calendar.component'

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: PathfindersCalendarComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CalendarRoutingModule { }
