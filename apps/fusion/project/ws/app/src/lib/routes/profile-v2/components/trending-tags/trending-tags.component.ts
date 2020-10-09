import { Component, Input, OnInit } from '@angular/core'
import { NSProfileData } from '../../models/profile-v2.model'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'app-discuss-trending-tags',
  templateUrl: './trending-tags.component.html',
  styleUrls: ['./trending-tags.component.scss'],
})
export class TrendingTagsComponent implements OnInit {
  @Input() tags!: NSProfileData.IProfile[]
  max = 0
  trandingTags!: NSProfileData.IProfile[]
  constructor() {

  }
  ngOnInit(): void {
    this.max = _.get(_.maxBy(this.tags, 'score'), 'score') || 0
    this.trandingTags = _.chain(this.tags).orderBy('score', 'desc').take(5).value()
  }
}
