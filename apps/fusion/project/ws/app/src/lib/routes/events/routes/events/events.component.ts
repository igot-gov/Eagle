import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { Router } from '@angular/router'
import { FormControl } from '@angular/forms'
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
    todayEventsCount: any
    joinedByMeEventsCount: any

    constructor(
        private router: Router,
        private eventSrvc: EventsService,
        private configSvc: ConfigurationsService,
        ) {
        this.getEventData()
    }

    ngOnInit() {
    }

    navigateWithPage(page: any) {
        if (page !== this.currentActivePage) {
            this.router.navigate([`/app/event-hub/home`], { queryParams: { page } })
            this.fetchNewData = true
        }
    }

    getEventData() {
        const reqObj = {
            locale: [
                'en',
            ],
            pageSize: 25,
            query: 'all',
            didYouMean: true,
            filters: [
                {
                    andFilters: [
                        {
                            contentType: [
                                'Event',
                            ],
                        },
                    ],
                },
            ],
            includeSourceFields: [
                'creatorLogo',
                'thumbnail',
            ],
        }

        this.eventSrvc.getEvents(reqObj).subscribe((res: any) => {
            this.setEventData(res)
        })
    }

    setEventData(responseObj: any) {
        if (responseObj.result !== undefined) {
            const eventList = responseObj.result
            this.eventData['todayEvents'] = []
            this.eventData['allEvents'] = []
            this.eventData['joinedByMe'] = []
            Object.keys(eventList).forEach((index: any) => {
                const eventObj = eventList[index]
                const expiryDateFormat = this.customDateFormat(eventObj.expiryDate)
                // const eventUpdateDate = this.customDateFormat(eventObj.last)
                const eventDataObj = {
                    eventName: eventObj.name,
                    eventDate: expiryDateFormat,
                    eventUpdatedOn: eventObj.lastUpdatedOn,
                    eventDuration: eventObj.duration,
                    eventjoined: (eventObj.creatorDetails !== undefined && eventObj.creatorDetails.length > 0) ?
                    ((eventObj.creatorDetails.length === 1) ? '1 person' :  `${eventObj.creatorDetails.length} people`) : ' --- ',
                    eventThumbnail: (eventObj.thumbnail !== null || eventObj.thumbnail !== undefined) ? eventObj.thumbnail : '---',
                    eventDescription: eventObj.description,
                    eventStatus: eventObj.status,
                    eventObjective: eventObj.learningObjective,
                    eventPresenters: (eventObj.creatorContacts !== undefined && eventObj.creatorContacts.length > 0)
                    ? eventObj.creatorContacts : '',
                    identifier: eventObj.identifier,
                    presenters: eventObj.creatorDetails,
                    eventJoinURL: eventObj.artifactUrl,
                }

                // Today's events
                if (this.isToday(eventObj.expiryDate)) {
                    this.eventData['todayEvents'].push(eventDataObj)
                    this.eventData['todayEvents'].sort((a: any, b: any) => {
                        return a.eventDate - b.eventDate
                    })
                }

                // Joined by me
                // if (eventObj.creatorDetails) {
                //     const myUserId = this.configSvc.userProfile && this.configSvc.userProfile.userId
                //     Object.keys(eventObj.creatorDetails).forEach((key: any) => {
                //         if (eventObj.creatorDetails[key].id ===  myUserId) {
                //             this.eventData['joinedByMe'].push(eventDataObj)
                //         }
                //     })
                // }

                
                this.todayEventsCount = this.eventData['todayEvents'].length;
                this.joinedByMeEventsCount = this.eventData['joinedByMe'].length;
                
                // All events
                this.eventData['allEvents'].push(eventDataObj)
            })
        }
    }

    scroll(el: HTMLElement) {
      el.scrollIntoView();
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
        const year  = eventDate.split('T')[0].substring(0, 4)
        const month = eventDate.split('T')[0].substring(4, 6)
        const dDate  = eventDate.split('T')[0].substring(6, 8)
        const today = new Date()
        const monthVal = `0${today.getMonth() + 1}`
        return dDate === today.getDate() &&
        month === (monthVal).slice(-2) &&
        year === today.getFullYear()
    }

    isJoinedByme(userDetails: any) {
        const myUserId = this.configSvc.userProfile && this.configSvc.userProfile.userId
        Object.keys(userDetails).forEach((index: any) => {
            return (userDetails[index].id === myUserId) ? true : false
        })
    }

}
