import { Component, OnInit, Input, OnChanges } from '@angular/core'
import { Router } from '@angular/router'
import * as moment from 'moment'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'

@Component({
  selector: 'ws-app-events-card',
  templateUrl: './events-card.component.html',
  styleUrls: ['./events-card.component.scss'],
})
export class EventsCardComponent implements OnInit, OnChanges {

  @Input() data?: []
  eventDetails: any
  eventTitle: any
  description: any
  eventDate: any
  presentersCount: any
  duration: any
  identifier: any
  joinUrl: any
  presenters: any = []
  avatarArr: any = []
  splitArr: any = []
  appIcon: any
  isPast: any
  participants: any = []
  splitUsersCount: any
  isToday: any
  status: any

  monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December',
  ]

  constructor(private router: Router, private accessService: AccessControlService) { }

  ngOnInit() {
    // if(this.data != undefined) {
    //   this.eventDetails = JSON.stringify(this.data)
    //   console.log(this.eventDetails.eventName)
    // }
  }

  ngOnChanges() {
   if (this.data !== undefined) {
      this.eventDetails = this.data
      this.eventTitle = this.eventDetails.eventName.replace(/http?.*?(?= |$)/g, '')
      this.description = this.eventDetails.eventName.replace(/(?:https?|ftp):\/\/[\n\S]+/g, '')
      this.eventDate = this.eventDateFormat(this.eventDetails.expirtyDate, this.eventDetails.eventDuration)
      this.presentersCount = (this.eventDetails.eventjoined.includes('---')) ? '' :  this.eventDetails.eventjoined.substr(0, 2)
      this.identifier = this.eventDetails.identifier
      this.joinUrl = this.eventDetails.eventJoinURL
      this.appIcon = this.eventDetails.eventThumbnail
      this.isPast = this.eventDetails.isPast

      // Participants logic
      this.participants = this.eventDetails.participants
      this.splitUsersCount = 0
      if (this.participants.length > 0) {
        Object.keys(this.participants).forEach((index: any) => {
          if (index < 3) {
            const dataObj = this.participants[index]
            const fullName = `${dataObj.first_name} ${dataObj.last_name}`
            const userObj = {
              shortname: this.getShortName(fullName),
            }
            this.avatarArr.push(userObj)
          }
        })
        if (this.participants.length > 3) {
          this.splitUsersCount = this.participants.length - 3
        }
      }
      // End

      // Show Live or Today badge
      const eventDate = this.eventDetails.allEventDate
      this.isToday = moment(eventDate).isSame(moment(), 'day')
      const now = new Date()
      const today = moment(now).format('YYYY-MM-DD hh:mm a')
      const isBetween = moment(today).isBetween(this.eventDetails.eventStartDate, this.eventDetails.eventEndDate)
      if (isBetween) {
          this.status = 'between'
      }
      // End
    }
  }

  getCareer() {
    this.router.navigate([`/app/event-hub/home/123`])
  }

  joinEvent(identifier: any) {
    this.router.navigate([`/app/event-hub/home/${identifier}`])
  }

  getShortName(name: any) {
    const matches = name.match(/\b(\w)/g)
    return matches.join('').toLocaleUpperCase()
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
    const readableDateMonth = moment(formatedDate).format('MMMM DD, hh:mm a')
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

  changeToDefaultImg($event: any) {
    $event.target.src = this.accessService.defaultLogo
  }

  eventStartEndDateFormat(datetime: any, duration: any) {
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
        let finalDateTimeValue = ''
        let readableDateMonth = ''
        const getTime = formatedDate.getTime()
        const futureDate = new Date(getTime + duration * 60000)
        readableDateMonth = moment(formatedDate).format('YYYY-MM-DD hh:mm a')
        const endDate = moment(futureDate).format('YYYY-MM-DD hh:mm a')
        finalDateTimeValue = `${readableDateMonth} - ${endDate}`
        return finalDateTimeValue
    }

}
