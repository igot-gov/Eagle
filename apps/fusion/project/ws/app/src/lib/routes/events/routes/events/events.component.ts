import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { Router } from '@angular/router'
import { FormControl } from '@angular/forms'
// import { DiscussService } from '../../../discuss/services/discuss.service'
import { EventsService } from '../../services/events.service'
import * as moment from 'moment'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss'],
})
export class EventsComponent implements OnInit {
  data!: NSDiscussData.IDiscussionData
  queryControl = new FormControl('')
  currentFilter = 'timestamp'
  pager = {}
  paginationData!: any
  currentActivePage!: any
  fetchNewData = false
  eventData: any = []

  constructor(
    // private route: ActivatedRoute,
    private router: Router,
    // private discussService: DiscussService,
    private eventSrvc: EventsService,
    private configSvc: ConfigurationsService,
  ) {
    this.getEventData();
    //console.log('here in app / events')
    //this.data = this.route.snapshot.data.topics.data
    //this.paginationData = this.data.pagination
    //this.categoryId = this.route.snapshot.data['eventsCategoryId'] || 1
  }

  ngOnInit() {

    // this.route.queryParams.subscribe(x => {
    //   this.currentActivePage = x.page || 1
    //   this.refreshData(this.currentActivePage)
    // })
    
  }

  // filter(key: string | 'timestamp' | 'viewcount') {
  //   if (key) {
  //     this.currentFilter = key
  //     this.refreshData(this.currentActivePage)
  //   }
  // }
  // updateQuery(key: string) {
  //   if (key) {

  //   }
  // }

  // refreshData(page: any) {
  //   if (this.fetchNewData) {
  //     if (this.currentFilter === 'timestamp') {
  //       this.discussService.fetchSingleCategoryDetails(this.categoryId, page).subscribe(
  //         (data: any) => {
  //           this.data = data
  //           this.paginationData = data.pagination
  //         },
  //         (_err: any) => {
  //         })
  //     } else {
  //       this.discussService.fetchSingleCategoryDetailsSort(this.categoryId, 'voted', page).subscribe(
  //         (data: any) => {
  //           this.data = data
  //           this.paginationData = data.pagination
  //         },
  //         (_err: any) => {
  //         })
  //     }
  //   }
  // }

  navigateWithPage(page: any) {
    if (page !== this.currentActivePage) {
      this.router.navigate([`/app/event-hub/home`], { queryParams: { page } })
      this.fetchNewData = true
    }
  }

  getEventData() {
    let reqObj = {
        "locale": [
          "en"
        ],
        "pageSize": 25,
        "query": "all",
        "didYouMean": true,
        "filters": [
            {
                "andFilters": [
                    {
                        "contentType": [
                            "Event",
                         ]
                    }
                ]
            }
        ],
        "includeSourceFields": [
            "creatorLogo", "thumbnail"
        ]
    }

    this.eventSrvc.getEvents(reqObj).subscribe((res: any) => {
      this.setEventData(res);
    })
  }

  setEventData(responseObj: any) {
    if(responseObj.result != undefined) {
      let eventList = responseObj.result;
      //console.log(eventList);
      this.eventData['todayEvents'] = []
      this.eventData['allEvents'] = []
      this.eventData['joinedByMe'] = [];
      Object.keys(eventList).forEach((index: any) => {
        let eventObj = eventList[index];
        console.log(eventObj.expiryDate);
        const expiryDateFormat = this.customDateFormat(eventObj.expiryDate)
        const eventUpdateDate = this.customDateFormat(eventObj.publishedOn)
        //console.log(expiryDateFormat);
        const eventDataObj = {
          eventName: eventObj.name,
          eventDate: expiryDateFormat,
          eventUpdatedOn: eventUpdateDate,
          eventDuration: eventObj.duration,
          eventjoined: (eventObj.creatorDetails !== undefined && eventObj.creatorDetails.length > 0) ?  ((eventObj.creatorDetails.length === 1) ?
            '1 person' :  `${eventObj.creatorDetails.length} people`) : ' --- ',
          eventThumbnail: (eventObj.thumbnail !== null || eventObj.thumbnail !== undefined) ? eventObj.thumbnail : '---',
          eventDescription: eventObj.description,
          eventStatus: eventObj.status,
          eventObjective: eventObj.learningObjective,
          eventPresenters: (eventObj.creatorContacts !== undefined && eventObj.creatorContacts.length > 0) ? eventObj.creatorContacts : '',
          identifier: eventObj.identifier,
        }

        // Today's events
        if (this.isToday(expiryDateFormat)) {
          this.eventData['todayEvents'].push(eventDataObj)
        } 
        
        // Joined by me
        if (eventObj.creatorDetails != undefined && eventObj.creatorDetails.length > 0) {
          if (this.isJoinedByme(eventObj.creatorDetails))
            this.eventData['joinedByMe'].push(eventDataObj)
        }

        // Featured Events

        // All events
        this.eventData['allEvents'].push(eventDataObj)
      })
      console.log(this.eventData);
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

  compareDate(selectedDate: any) {
    const now = new Date()
    const today = moment(now).format('DD-MM-YYYY HH:mm')
    return (selectedDate < today) ? true : false
  }

  isToday(eventDate: any) {
    eventDate = new Date(eventDate)
    const today = new Date()
    return eventDate.getDate() == today.getDate() &&
       eventDate.getMonth() == today.getMonth() &&
       eventDate.getFullYear() == today.getFullYear()
  }

  isJoinedByme(userDetails: any) {
    //console.log(userDetails);
    const myUserId = this.configSvc.userProfile && this.configSvc.userProfile.userId
    Object.keys(userDetails).forEach( (index: any) => {
      return (userDetails[index].id === myUserId) ? true : false
    })
  }

}
