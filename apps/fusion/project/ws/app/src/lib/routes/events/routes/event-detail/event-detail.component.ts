import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { MatDialog } from '@angular/material/dialog'
import { EventsService } from '../../services/events.service'

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
                'thumbnail',
            ],
        }

        this.eventSrvc.getEvents(reqObj).subscribe((res: any) => {
            this.setEventData(res)
        })
    }

    setEventData(responseObj: any) {
        if (responseObj.result !== undefined) {
            this.eventDataObj = responseObj.result[0]
            this.overviewData.push(responseObj.result[0])
            Object.keys(responseObj.result[0].creatorDetails).forEach((index: any) => {
                const obj = {
                    name: responseObj.result[0].creatorDetails[index].name,
                    id: responseObj.result[0].creatorDetails[index].id,
                }
                this.participantsData.push(obj)
            })
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
