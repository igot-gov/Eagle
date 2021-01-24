import { Component, OnInit, OnChanges, Input } from '@angular/core'
import * as moment from 'moment'

@Component({
    selector: 'app-right-menu-card',
    templateUrl: './right-menu-card.component.html',
    styleUrls: ['./right-menu-card.component.scss'],
})
export class RightMenuCardComponent implements OnInit, OnChanges {

    @Input() data?: any = []
    expiryDate: any
    duration: any
    joiningInfo: any = []
    dateInfo: any
    timeInfo: any

    constructor() {
    }

    ngOnInit() {
    }

    ngOnChanges() {
        if (this.data !== undefined) {
            if(this.data.expiryDate != undefined) {
                this.expiryDate  = this.data.expiryDate
                this.duration  = this.data.duration
                this.data.expiryDate = this.eventDateFormat(this.expiryDate, this.duration)
                let dateTimeArr = this.data.expiryDate.split(',')
                this.dateInfo = dateTimeArr[0]
                this.timeInfo = dateTimeArr[1]
            }
            this.joiningInfo.push(this.data)
        }
    }

    startMeeting(meetingURL: any) {
        window.open(meetingURL, '_blank')
    }

    eventDateFormat(datetime: any, duration: any) {
        const dateTimeArr = datetime.split('T')
        const date = dateTimeArr[0]
        const year = date.substr(0, 4)
        const month = date.substr(4, 2)
        const day = date.substr(6, 2)
        const time = dateTimeArr[1]
        const hours = time.substr(0, 2)
        const minutes = time.substr(2, 2)
        const seconds = time.substr(4, 2)
        const formatedDate = new Date(year, month - 1, day, hours, minutes, seconds, 0)
        const getTime = formatedDate.getTime()
        const futureDate = new Date(getTime + duration * 60000)
        const formatedHoursMin = this.formatTimeAmPm(futureDate)
        const readableDateMonth = moment(formatedDate).format('MMMM DD YYYY, hh:mm a')
        const finalDateTimeValue = `${readableDateMonth} - ${formatedHoursMin}`
        return finalDateTimeValue
    }

    formatTimeAmPm(futureDate: any) {
      let hours = futureDate.getHours()
      let minutes = futureDate.getMinutes()
      const ampm = hours >= 12 ? 'pm' : 'am'
      hours = hours % 12
      hours = hours ? hours : 12
      minutes = minutes < 10 ? `0${minutes}` : minutes
      const strTime = `${hours}:${minutes} ${ampm}`
      return strTime
    }

}
