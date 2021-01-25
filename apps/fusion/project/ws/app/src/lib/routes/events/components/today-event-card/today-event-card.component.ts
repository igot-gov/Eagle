import { Component, OnInit, Input, OnChanges } from '@angular/core'
import { Router } from '@angular/router'

@Component({
    selector: 'ws-app-today-event-card',
    templateUrl: './today-event-card.component.html',
    styleUrls: ['./today-event-card.component.scss'],
})
export class TodayEventCardComponent implements OnInit, OnChanges {

    @Input() data?: []
    isLive = true
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
            this.identifier = this.eventDetails.identifier
            this.isLive = this.eventDetails.status
        }
    }

    getDetails() {
        this.router.navigate([`/app/event-hub/home/${this.identifier}`])
    }
}
