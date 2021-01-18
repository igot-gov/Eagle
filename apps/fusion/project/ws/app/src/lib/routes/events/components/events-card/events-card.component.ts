import { Component, OnInit, Input } from '@angular/core'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-events-card',
  templateUrl: './events-card.component.html',
  styleUrls: ['./events-card.component.scss'],
})
export class EventsCardComponent implements OnInit {
  
  @Input('data') data: any = [];

  constructor(private router: Router) { }

  ngOnInit() {
    console.log(this.data);
  }

  getCareer() {
    this.router.navigate([`/app/event-hub/home/123`])
  }

}
