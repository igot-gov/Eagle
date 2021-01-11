import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-events-card',
  templateUrl: './events-card.component.html',
  styleUrls: ['./events-card.component.scss'],
})
export class EventsCardComponent implements OnInit {
  @Input()
  discuss!: NSDiscussData.IDiscussionData

  constructor(private router: Router) { }

  ngOnInit() {
  }

  getCareer() {
    this.router.navigate([`/app/event-hub/home/${this.discuss.tid}`])
  }

}
