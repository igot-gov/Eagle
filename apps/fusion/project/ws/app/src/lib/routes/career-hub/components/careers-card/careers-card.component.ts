import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-careers-card',
  templateUrl: './careers-card.component.html',
  styleUrls: ['./careers-card.component.scss'],
})
export class CareersCardComponent implements OnInit {
  @Input()
  discuss!: NSDiscussData.IDiscussionData

  constructor(private router: Router) { }

  ngOnInit() {
  }

  getCareer() {
    this.router.navigate([`/app/careers/home/${this.discuss.tid}`])
  }

}
