import { Component, OnInit, Input, OnChanges } from '@angular/core'
import { Router } from '@angular/router'

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
      this.eventTitle = this.eventDetails.eventName
      this.description = this.eventDetails.eventName.replace(/(?:https?|ftp):\/\/[\n\S]+/g, '')
      this.eventDate = this.eventDateFormat(this.eventDetails.eventDate, this.eventDetails.eventDuration)
      this.presentersCount = (this.eventDetails.eventjoined.includes('---')) ? '' :  this.eventDetails.eventjoined.substr(0, 2)
      this.identifier = this.eventDetails.identifier
      this.joinUrl = this.eventDetails.eventJoinURL
    }
  }

  getCareer() {
    this.router.navigate([`/app/event-hub/home/123`])
  }

  eventDateFormat(date: any, duration: any) {
    const dateArr = date.split('-')
    const timeArr = date.split(' ')
    const mediumArr = timeArr[1].split(':')
    const floor = Math.floor
    const hours = floor(duration / 60)
    const minutes = duration % 60
    const hoursEnd = parseInt(mediumArr[0], 10) + hours
    const toHours = (hoursEnd < 10) ? `0${hoursEnd}` : hoursEnd
    const minutesEnd = parseInt(mediumArr[1], 10) + minutes
    const monthName = this.monthNames[parseInt(dateArr[1], 10) - 1]

    return `${monthName} ${dateArr[0]}, ${timeArr[1]} - ${toHours}:${minutesEnd}`

  }

  joinEvent(meetingURL: any) {
    window.open(meetingURL, '_blank')
  }

}
