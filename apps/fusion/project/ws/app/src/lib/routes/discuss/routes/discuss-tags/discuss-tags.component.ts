
import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
/* tslint:disable */
import _ from 'lodash'
import { ActivatedRoute } from '@angular/router'
import { DiscussUtilsService } from '../../services/discuss-utils.service'
/* tslint:enable */
@Component({
  selector: 'app-discuss-tags',
  templateUrl: './discuss-tags.component.html',
  styleUrls: ['./discuss-tags.component.scss'],
})
export class DiscussTagsComponent implements OnInit {
  tag = this.route.snapshot.data.availableTags.data
  tags!: NSDiscussData.ITag[]
  filteredTags!: NSDiscussData.ITag[]
  query!: string
  constructor(
    private route: ActivatedRoute,
    private discussUtils: DiscussUtilsService
  ) {
    this.tags = this.tag.tags
  }

  ngOnInit() {
    this.filteredTags = this.tags
  }

  filter(name: string) {
    this.filteredTags = _.filter(this.tags, i => i.value === name)
  }

  public getBgColor(tagTitle: any) {
    const bgColor = this.discussUtils.stringToColor(tagTitle.toLowerCase())
    const color = this.discussUtils.getContrast(bgColor)
    return { color, 'background-color': bgColor }
  }
}
