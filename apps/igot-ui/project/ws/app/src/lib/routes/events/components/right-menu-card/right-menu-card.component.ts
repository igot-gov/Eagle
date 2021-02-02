import { Component, OnInit, OnChanges, Input } from '@angular/core'
import { EventsService } from '../../services/events.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
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
    todayDateTime: any
    disableFlag: boolean
    startDate: any
    endDate: any
    btnLabel: any
    status: any
    isToday: any
    myUserId: any
    identifier: any

    constructor(
        private eventSrv: EventsService,
        private configSvc: ConfigurationsService,
    ) {
        this.disableFlag = true
        this.todayDateTime = moment(new Date()).format('MMMM DD YYYY, hh:mm a')
    }

    ngOnInit() {
        this.myUserId = this.configSvc.userProfile && this.configSvc.userProfile.userId
    }

    ngOnChanges() {
        if (this.data !== undefined) {
            if (this.data.expiryDate !== undefined) {
                this.expiryDate  = this.data.expiryDate
                this.duration  = this.data.duration
                this.identifier = this.data.identifier
                this.data.expiryDate = this.eventDateFormat(this.expiryDate, this.duration)
                const dateTimeArr = this.data.expiryDate.split(',')
                this.dateInfo =  moment(dateTimeArr[0]).format('MMMM DD, YYYY')
                this.timeInfo = dateTimeArr[1]
            }
            const now = new Date()
            const today = moment(now).format('YYYY-MM-DD hh:mm a')
            const isBetween = moment(today).isBetween(this.startDate, this.endDate)
            const isAfter = moment(this.endDate).isAfter(today)
            if (isBetween) {
                this.disableFlag = false
                this.btnLabel = 'Attend live event'
                this.status = 'between'
            } else if (isAfter) {
                this.btnLabel = 'Attend live event'
                this.disableFlag = true
                this.status = 'upcoming'
            } else {
                this.btnLabel = 'Event ended'
                this.disableFlag = true
                this.status = 'expired'
            }
            this.joiningInfo.push(this.data)
        }
    }

    startMeeting(meetingURL: any) {
        this.joinAMeeting(meetingURL)
    }

    joinAMeeting(meetingURL: any) {
        const reqObj = {
            content_type: 'Resource',
            current: ['1'],
            max_size: 1,
            mime_type: 'application/html',
            user_id_type: this.myUserId,
        }
        this.eventSrv.joinMeeting(reqObj, this.identifier).subscribe(() => {
            window.open(meetingURL, '_blank')
        })
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
        this.startDate = moment(formatedDate).format('MMMM DD YYYY, hh:mm a')
        this.endDate = moment(futureDate).format('MMMM DD YYYY, hh:mm a')
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
