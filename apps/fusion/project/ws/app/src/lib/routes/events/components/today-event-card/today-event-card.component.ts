import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-today-event-card',
  templateUrl: './today-event-card.component.html',
  styleUrls: ['./today-event-card.component.scss'],
})
export class TodayEventCardComponent implements OnInit {
  @Input()
  discuss!: NSDiscussData.IDiscussionData
  isLive = true

  constructor(private router: Router) { }

  ngOnInit() {
  }

  getCareer() {
    this.router.navigate([`/app/event-hub/home/${this.discuss.tid}`])
  }

}
