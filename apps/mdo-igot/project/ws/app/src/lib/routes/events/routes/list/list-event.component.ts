import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
import { EventsService } from '../../services/events.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import * as moment from 'moment'

@Component({
  selector: 'ws-app-list-event',
  templateUrl: './list-event.component.html',
  styleUrls: ['./list-event.component.scss'],
})
export class ListEventComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata: any = []
  data: any = []
  eventData: any = []
  math: any
  currentFilter = 'upcoming'
  discussionList!: any
  discussProfileData!: any
  userDetails: any
  location!: string | null
  tabs: any
  currentUser!: string | null
  connectionRequests!: any[]
  usersData!: any

  constructor(
    private router: Router,
    private eventSvc: EventsService,
    private configSvc: ConfigurationsService,
    ) {
    this.math = Math
    this.currentUser = this.configSvc.userProfile && this.configSvc.userProfile.userId
  }

  ngOnInit() {
    this.tabledata = {
      columns: [
        { displayName: 'Thumbnail', key: 'eventThumbnail' },
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

  setEventListData(eventObj: any) {
    if (eventObj !== undefined) {
      const data = eventObj.result
      this.eventData['pastEvents'] = []
      this.eventData['upcomingEvents'] = []
      Object.keys(data).forEach((index: any) => {
        const obj = data[index]
        const expiryDateFormat = this.customDateFormat(obj.lastUpdatedOn)
        const eventUpdateDate = this.customDateFormat(obj.publishedOn)
        const floor = Math.floor
        const hours = floor(obj.duration / 60)
        const minutes = obj.duration % 60
        const duration = (hours === 0) ? ((minutes === 0) ? '---' : `${minutes} minutes`) : (minutes === 0) ? (hours === 1) ?
        `${hours} hour` : `${hours} hours` :  (hours === 1) ? `${hours} hour ${minutes} minutes` : `${hours} hours ${minutes} minutes`
        const eventDataObj = {
          eventName: obj.name.substring(0, 30),
          eventDate: expiryDateFormat,
          eventUpdatedOn: eventUpdateDate,
          eventDuration: duration,
          eventjoined: (obj.creatorDetails !== undefined && obj.creatorDetails.length > 0) ?  ((obj.creatorDetails.length === 1) ?
            '1 person' :  `${obj.creatorDetails.length} people`) : ' --- ',
          eventThumbnail: (obj.thumbnail !== null || obj.thumbnail !== undefined) ? obj.thumbnail : '---',
        }
        const isPast = this.compareDate(expiryDateFormat);
        (isPast) ? this.eventData['pastEvents'].push(eventDataObj) : this.eventData['upcomingEvents'].push(eventDataObj)
      })
      this.filter('upcoming')
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

  filter(key: string | 'timestamp' | 'best' | 'saved') {
    const upcomingEventsData: any[] = []
    const pastEventsData: any[] = []
    if (this.eventData['pastEvents'] && this.eventData['pastEvents'].length > 0) {
      this.eventData['pastEvents'].forEach((event: any) => {
        pastEventsData.push(event)
      })
    }

    if (this.eventData['upcomingEvents'] && this.eventData['upcomingEvents'].length > 0) {
       this.eventData['upcomingEvents'].forEach((event: any) => {
         upcomingEventsData.push(event)
      })
    }

    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'upcoming':
          this.data = upcomingEventsData
          break
        case 'past':
          this.data = pastEventsData
          break
        default:
          this.data = upcomingEventsData
          break
      }
    }
  }

  // getAllEvents() {
  //   this.usersService.getAllUsers().subscribe(data => {
  //     this.usersData = data
  //     this.filter('active')
  //   })
  // }

  onCreateClick() {
    this.router.navigate([`/app/users/create-user`])
  }

  onRoleClick(user: any) {
    this.router.navigate([`/app/users/${user.userId}/details`])
  }

  ngOnDestroy() {

  }

  compareDate(selectedDate: any) {
    const now = new Date()
    const today = moment(now).format('DD-MM-YYYY HH:mm')
    return (selectedDate < today) ? true : false
  }

}
