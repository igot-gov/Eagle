import { Component, OnInit, Input, OnChanges } from '@angular/core'
import { Router } from '@angular/router'


@Component({
  selector: 'ws-app-events-card',
  templateUrl: './events-card.component.html',
  styleUrls: ['./events-card.component.scss'],
})
export class EventsCardComponent implements OnInit, OnChanges {
  
  @Input() data?: []
  eventDetails:any
  title: string;
  description: string;
  date: any;
  presenters: any;
  duration: any;
   
  constructor(private router: Router) { }

  ngOnInit() {
    // if(this.data != undefined) {
    //   this.eventDetails = JSON.stringify(this.data)
    //   console.log(this.eventDetails.eventName)
    // }
  }

  ngOnChanges() {
   if(this.data != undefined) {
      this.eventDetails = this.data
      console.log(this.eventDetails)
      console.log(this.eventDetails.eventName)
      this.title = this.eventDetails.eventName;
      this.description = this.eventDetails.eventName;
      this.date = this.eventDetails.eventDate;
      this.duration = this.eventDetails.eventDuration;
      this.presenters = this.eventDetails.eventjoined;
    }
  }


  // getDetails(): any {
  //   console.log(this.eventDetail);
  // }

 
  getCareer() {
    this.router.navigate([`/app/event-hub/home/123`])
  }

}
