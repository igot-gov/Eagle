import { Component, OnInit, Input, OnChanges } from '@angular/core'
import { Router } from '@angular/router'
import * as moment from 'moment'

@Component({
    selector: 'ws-app-today-event-card',
    templateUrl: './today-event-card.component.html',
    styleUrls: ['./today-event-card.component.scss'],
})
export class TodayEventCardComponent implements OnInit, OnChanges {

    @Input() data?: []
    isLive = false
    eventDetails: any
    eventTitle: any
    description: any
    eventDate: any
    presentersCount: any
    duration: any
    identifier: any

    constructor(private router: Router) { }

    ngOnInit() {
    }

    ngOnChanges() {
        if (this.data !== undefined) {
            this.eventDetails = this.data
            this.eventTitle = this.eventDetails.eventName
            this.eventDate = this.eventDetails.todayEventDateStr
            const timeArr = this.eventDate.split(' - ')
            const startTime = timeArr[0]
            const endTime = timeArr[1]
            const d = new Date()
            const hours = (d.getHours() === 0) ? 12 : d.getHours()
            const currentTime = `${hours}:${d.getMinutes()}`
            const startDate = new Date(`1970/01/01 ${startTime}`)
            const endDate = new Date(`1970/01/01 ${endTime}`)
            const currentDate = new Date(`1970/01/01 ${currentTime}`)
            if (moment(currentDate).isBetween(startDate, endDate)) {
               this.isLive = true
            }
            this.identifier = this.eventDetails.identifier
        }
    }

    getDetails() {
        this.router.navigate([`/app/event-hub/home/${this.identifier}`])
    }
}
