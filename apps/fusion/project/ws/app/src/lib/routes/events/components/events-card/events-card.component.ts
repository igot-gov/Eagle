import { Component, OnInit, Input, OnChanges } from '@angular/core'
import { Router } from '@angular/router'
import * as moment from 'moment'

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

  monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December',
  ]

  constructor(private router: Router) { }

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
      if (this.eventDetails.presenters && this.eventDetails.presenters.length > 0) {
        this.presenters = this.eventDetails.presenters
        // this.userCountArray()
      }

    }
  }

  getCareer() {
    this.router.navigate([`/app/event-hub/home/123`])
  }

  joinEvent(meetingURL: any) {
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

  // userCountArray(){
  //     for (let i = 0; i < this.presenters.length; i++) {
  //         const firstname = this.presenters[i].name.charAt(0)
  //         const lastname = this.presenters[i].name.split('')[1]
  //         const userObj = {
  //             name: `${firstname} ${lastname}`
  //         }
  //         if(i <= 2){
  //             this.avatarArr.push(userObj)
  //         }else{
  //             this.splitArr.push(userObj)
  //         }
  //     }
  // }

}
