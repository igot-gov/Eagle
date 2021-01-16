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

  constructor(private router: Router, private eventSvc: EventsService) {
  }

  ngOnInit() {
    this.tabledata = {
      columns: [
        { displayName: 'Title', key: 'eventName' },
        { displayName: 'Date and time', key: 'eventDate' },
        { displayName: 'Updated at', key: 'eventUpdatedOn' },
        { displayName: 'Duration', key: 'eventDuration' },
        { displayName: 'Joined', key: 'eventjoined' },
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
    const requestObj = {
        locale: ['en'],
        pageSize: 12,
        query: 'all',
        didYouMean: true,
        filters: [
          {
            andFilters: [
              { lastUpdatedOn: ['month'] },
              { contentType: ['Event'] },
            ],
          },
        ],
        includeSourceFields: ['creatorLogo', 'thumbnail'],
    }
    this.eventSvc.searchEvent(requestObj).subscribe(events => {
      this.setEventListData(events)
    })
  }

  ngOnDestroy() { }

  setEventListData(eventObj: any) {
    if (eventObj !== undefined) {
      const data = eventObj.result
      Object.keys(data).forEach((index: any) => {
        const obj = data[index]
        const expiryDateFormat = this.customDateFormat(obj.lastUpdatedOn)
        const eventUpdateDate = this.customDateFormat(obj.publishedOn)
        const hours = Math.floor((obj.duration / 3600) / 60)
        const minutes = obj.duration % 60
        const duration = (hours === 0) ? ((minutes === 0) ? '---' : `${minutes} minutes`) : `${hours} hours ${minutes} minutes`
        const eventDataObj = {
          eventName: obj.name.substring(0, 30),
          eventDate: expiryDateFormat,
          eventUpdatedOn: eventUpdateDate,
          eventDuration: duration,
          eventjoined: (obj.creatorDetails !== undefined && obj.creatorDetails.length > 0) ?  ((obj.creatorDetails.length === 1) ?
            '1 person' :  `${obj.creatorDetails.length} people`) : ' --- ',
        }
        this.data.push(eventDataObj)
      })
    }
  }

  customDateFormat(date: any) {
    const year  = date.split('T')[0].substring(0, 4)
    const month = date.split('T')[0].substring(4, 6)
    const dDate  = date.split('T')[0].substring(6, 8)
    const hour  = date.split('T')[1].substring(0, 2)
    const min  = date.split('T')[1].substring(2, 4)
    return `${dDate}-${month}-${year} ${hour}:${min}`
  }

}
