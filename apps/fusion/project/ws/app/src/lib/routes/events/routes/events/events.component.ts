import { Component, OnInit } from '@angular/core'
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
    queryControl = new FormControl('')
    currentFilter = 'timestamp'
    pager = {}
    paginationData!: any
    currentActivePage!: any
    fetchNewData = false
    eventData: any = []
    todayEventsCount: any
    joinedByMeEventsCount: any
    allEventsCount: any
    department: any
    data: any = []
    subData: any = []
    currentSubFilter = 'upcoming'

    constructor(
        private router: Router,
        private eventSrvc: EventsService,
        private configSvc: ConfigurationsService,
        ) {
        this.getEventData()
        this.department = this.configSvc.userProfile && this.configSvc.userProfile.departmentName
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
            this.eventData['myMDOEvents'] = []
            Object.keys(eventList).forEach((index: any) => {
                const eventObj = eventList[index]
                const expiryDateFormat = this.customDateFormat(eventObj.expiryDate)
                // const eventUpdateDate = this.customDateFormat(eventObj.last)
                const eventDataObj = {
                    eventName: eventObj.name.replace(/http?.*?(?= |$)/g, ''),
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
                    eventSource: eventObj.sourceName,
                    expirtyDate: eventObj.expiryDate,
                    participants: eventObj.creatorContacts,
                }

                // Today's events
                if (this.isToday(eventObj.expiryDate)) {
                    this.eventData['todayEvents'].push(eventDataObj)
                    this.eventData['todayEvents'].sort((a: any, b: any) => {
                        return a.eventDate - b.eventDate
                    })
                }

                // Joined by me
                if (eventObj.creatorDetails) {
                    const myUserId = this.configSvc.userProfile && this.configSvc.userProfile.userId
                    Object.keys(eventObj.creatorDetails).forEach((key: any) => {
                        if (eventObj.creatorDetails[key].id ===  myUserId) {
                            this.eventData['joinedByMe'].push(eventDataObj)
                        }
                    })
                }

                // My MDO
                if (eventObj.sourceName !== undefined) {
                    if (this.department === eventObj.sourceName) {
                        this.eventData['myMDOEvents'].push(eventDataObj)
                    }
                }

                this.allEventsCount = this.eventData['allEvents'].length
                this.todayEventsCount = this.eventData['todayEvents'].length
                this.joinedByMeEventsCount = this.eventData['joinedByMe'].length

                this.eventData['allEvents'].push(eventDataObj)
            })
            this.filter('all')
        }
    }

    scroll(el: HTMLElement) {
      el.scrollIntoView()
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

    filter(key: string | 'timestamp' | 'best' | 'saved') {
        const allData: any[] = []
        const mdoData: any[] = []
        if (this.eventData['allEvents'] && this.eventData['allEvents'].length > 0) {
          this.eventData['allEvents'].forEach((event: any) => {
            allData.push(event)
          })
        }

        if (this.eventData['myMDOEvents'] && this.eventData['myMDOEvents'].length > 0) {
           this.eventData['myMDOEvents'].forEach((event: any) => {
             mdoData.push(event)
          })
        }

        if (key) {
            this.currentFilter = key
            switch (key) {
                case 'all':
                    this.data = allData
                break
                case 'myMDO':
                    this.data = mdoData
                break
                default:
                    this.data = allData
                break
            }
        }
        this.setEventSubFilter('upcoming')
    }

    applyFilter(filterValue: any) {
        if (filterValue !== '') {
            const data = this.data.filter((tag: any) => {
                return tag.eventName.includes(filterValue)
            })
            this.data = data
        } else {
            this.data = this.eventData['allEvents']
        }
    }

     setEventSubFilter(eventValue: any) {

        const upcomingEvents: any[] = []
        const pastEvents: any[] = []

        if (this.data && this.data.length > 0) {
            this.data.forEach((event: any) => {
                const isPast = this.compareDate(event.eventDate)
                if (isPast) {
                    pastEvents.push(event)
                } else {
                    upcomingEvents.push(event)
                }
            })
        }
        if (eventValue) {
            this.currentSubFilter = eventValue
            switch (eventValue) {
                case 'upcoming':
                    this.subData = upcomingEvents
                break
                case 'past':
                    this.subData = pastEvents
                break
                default:
                    this.subData = upcomingEvents
                break
            }
        }
        this.subData.sort((a: any, b: any) => {
            return new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime()
        })
    }
}
