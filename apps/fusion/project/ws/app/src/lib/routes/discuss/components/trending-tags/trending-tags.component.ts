import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import _ from 'lodash'

@Component({
  selector: 'app-discuss-trending-tags',
  templateUrl: './trending-tags.component.html',
  styleUrls: ['./trending-tags.component.scss'],
})
export class TrendingTagsComponent implements OnInit {
  tags!: NSDiscussData.tag[]
  max: number = 0
  constructor() {

  }
  ngOnInit(): void {
    this.tags = [
      {
        bgColor: '',
        color: '',
        score: 12,
        value: 'Tag 1',
        valueEscaped: 'Tag 1'
      },
      {
        bgColor: '',
        color: '',
        score: 6,
        value: 'Tag 2',
        valueEscaped: 'Tag 2'
      }
    ]

    this.max = _.get(_.maxBy(this.tags, 'score'), 'score') || 0

  }
  css() {
    return "linear - gradient(to left, #00ff00 " + 80 + " %, #ff0000 20 %)"
  }
}
