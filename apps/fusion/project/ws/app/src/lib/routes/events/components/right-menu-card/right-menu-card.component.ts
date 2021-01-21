import { Component, OnInit, OnChanges, Input } from '@angular/core'

@Component({
    selector: 'app-right-menu-card',
    templateUrl: './right-menu-card.component.html',
    styleUrls: ['./right-menu-card.component.scss'],
})
export class RightMenuCardComponent implements OnInit, OnChanges {

    @Input() data?: []
    joiningInfo: any = []

    constructor() {
    }

    ngOnInit() {
    }

    ngOnChanges() {
        if (this.data !== undefined) {
            this.joiningInfo.push(this.data)
        }
    }

    startMeeting(meetingURL: any) {
        window.open(meetingURL, '_blank')
    }

}
