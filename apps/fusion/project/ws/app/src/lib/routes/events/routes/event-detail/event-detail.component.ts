import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { MatDialog } from '@angular/material/dialog'
import { EventsService } from '../../services/events.service'
import * as moment from 'moment'

@Component({
    selector: 'ws-app-event-detail',
    templateUrl: './event-detail.component.html',
    styleUrls: ['./event-detail.component.scss'],
})

export class EventDetailComponent implements OnInit {
    eventId!: number
    eventDataObj: any
    currentFilter = 'overview'
    overviewData: any = []
    participantsData: any = []
    participantsCount: any
    presenters: any = []
    presentersCount: any
    isToday: any
    status: any

    constructor(
        public dialog: MatDialog,
        private route: ActivatedRoute,
        private eventSrvc: EventsService,
    ) {

    }

    ngOnInit() {
        this.route.params.subscribe(params => {
            this.eventId = params.eventId
            if (this.eventId) {
                this.getEventDetails(this.eventId)
            }
        })
    }

    getEventDetails(eventIdentifier: any) {
        const reqObj = {
            locale: [
                'en',
            ],
            pageSize: 1,
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
                        {
                            identifier: [
                                eventIdentifier,
                            ],
                        },
                    ],
                },
            ],
            includeSourceFields: [
                'creatorLogo',
                'appIcon',
            ],
        }

        this.eventSrvc.getEvents(reqObj).subscribe((res: any) => {
            this.setEventData(res)
        })
    }

    setEventData(responseObj: any) {
        if (responseObj.result !== undefined) {
            this.eventDataObj = responseObj.result[0]
            responseObj.result[0].name = responseObj.result[0].name.replace(/http?.*?(?= |$)/g, '')
            this.overviewData.push(responseObj.result[0])
            if (responseObj.result[0].creatorContacts !== undefined) {
                Object.keys(responseObj.result[0].creatorContacts).forEach((index: any) => {
                    const obj = {
                        name: responseObj.result[0].creatorContacts[index].name,
                        id: responseObj.result[0].creatorContacts[index].id,
                        type: 'Host',
                    }
                    this.presenters.push(obj)
                })
            }
            if (responseObj.result[0].creatorDetails !== undefined) {
                Object.keys(responseObj.result[0].creatorDetails).forEach((index: any) => {
                    const obj = {
                        name: responseObj.result[0].creatorDetails[index].name,
                        id: responseObj.result[0].creatorDetails[index].id,
                        type: 'Guest',
                    }
                    this.presenters.push(obj)
                })
            }
            this.presentersCount = this.presenters.length
            this.participantsCount = this.participantsData.length
            const eventDate = responseObj.result[0].allEventDate
            this.isToday = moment(eventDate).isSame(moment(), 'day')
            const startDate = responseObj.result[0].eventStartDate
            const endDate = responseObj.result[0].eventEndDate

            const now = new Date()
            const today = moment(now).format('YYYY-MM-DD hh:mm a')
            const isBetween = moment(today).isBetween(startDate, endDate)
            if (isBetween) {
                this.status = 'between'
            }
        }
    }

    filter(key: string | 'timestamp' | 'best' | 'saved') {
        if (key) {
            this.currentFilter = key
            switch (key) {
                case 'overview':
                    this.eventDataObj = this.overviewData
                break
                case 'participants':
                    this.eventDataObj = this.participantsData
                break
                default:
                    this.eventDataObj = this.overviewData
                break
            }
        }
    }
}
