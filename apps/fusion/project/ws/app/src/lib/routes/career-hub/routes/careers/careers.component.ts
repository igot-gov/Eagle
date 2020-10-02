import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { ActivatedRoute } from '@angular/router'
import { FormControl } from '@angular/forms'

@Component({
  selector: 'ws-app-careers',
  templateUrl: './careers.component.html',
  styleUrls: ['./careers.component.scss'],
})
export class CareersComponent implements OnInit {
  data!: NSDiscussData.IDiscussionData
  queryControl = new FormControl('')
  currentFilter = 'timestamp'

  constructor(
    private route: ActivatedRoute
  ) {
    this.data = this.route.snapshot.data.topics.data
  }

  ngOnInit() {
  }

  filter(key: string | 'timestamp' | 'viewcount') {
    if (key) {
      this.currentFilter = key
    }
  }

}
