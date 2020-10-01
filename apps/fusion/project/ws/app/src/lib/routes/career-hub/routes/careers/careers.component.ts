import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-careers',
  templateUrl: './careers.component.html',
  styleUrls: ['./careers.component.scss'],
})
export class CareersComponent implements OnInit {
  data!: NSDiscussData.IDiscussionData

  constructor(
    private route: ActivatedRoute
  ) {
    console.log('this.route.snapshot.data: ', this.route.snapshot.data)
    this.data = this.route.snapshot.data.topics.data
  }

  ngOnInit() {
    
  }

}
