import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
import { EventsService } from '../../services/events.service'
@Component({
  selector: 'ws-app-list-event',
  templateUrl: './list-event.component.html',
  styleUrls: ['./list-event.component.scss'],
})
export class ListEventComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata: any = []
  data: any = []

  constructor(private router: Router, private eventSvc: EventsService) { }

  ngOnInit() {
    this.tabledata = {
      // actions: [{ name: 'Details', label: 'Details', icon: 'remove_red_eye', type: 'link' }],
      columns: [
        { displayName: 'Title', key: 'title' },
        { displayName: 'Date and time', key: 'event_date' },
        { displayName: 'Updated at', key: 'updated_at' },
        { displayName: 'Duration', key: 'duration' },
        { displayName: 'Joined', key: 'joined' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    this.fetchEvents()
  }

  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* Click event to navigate to a particular role */
  onEventClick(event: any) {
    this.router.navigate([`/app/events/${event.id}`])
  }

  /* API call to get all roles*/
  fetchEvents() {
    this.eventSvc.getEvents().subscribe(events => {
      this.data = events.data
    })
  }

  ngOnDestroy() { }
}
