import { Component, Input, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'app-discuss-trending-tags',
  templateUrl: './trending-tags.component.html',
  styleUrls: ['./trending-tags.component.scss'],
})
export class TrendingTagsComponent implements OnInit {
  @Input() tags!: NSDiscussData.ITag[]
  max = 0
  trandingTags!: NSDiscussData.ITag[]
  constructor() {

  }
  ngOnInit(): void {
    // this.tags = [
    //   {
    //     bgColor: '',
    //     color: '',
    //     score: 12,
    //     value: 'Tag 1',
    //     valueEscaped: 'Tag 1',
    //   },
    //   {
    //     bgColor: '',
    //     color: '',
    //     score: 6,
    //     value: 'Tag 2',
    //     valueEscaped: 'Tag 2',
    //   },
    // ]

    this.max = _.get(_.maxBy(this.tags, 'score'), 'score') || 0
    this.trandingTags = _.chain(this.tags).orderBy('score', 'desc').take(5).value()
  }
  css() {
    // return 'linear - gradient(to left, #00ff00 " + 80 + " %, #ff0000 20 %)"
  }
}
