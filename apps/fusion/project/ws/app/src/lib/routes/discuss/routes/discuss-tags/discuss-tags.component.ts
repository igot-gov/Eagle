
import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
/* tslint:disable */
import _ from 'lodash'
import { ActivatedRoute } from '@angular/router'
/* tslint:enable */
@Component({
  selector: 'app-discuss-tags',
  templateUrl: './discuss-tags.component.html',
  styleUrls: ['./discuss-tags.component.scss'],
})
export class DiscussTagsComponent implements OnInit {
  tag = this.route.snapshot.data.availableTags.data;
  tags!: NSDiscussData.ITag[]
  filteredTags!: NSDiscussData.ITag[]
  constructor(private route: ActivatedRoute) {
    // this.assignTags()
    this.tags = this.tag.tags
  }

  ngOnInit() {
    this.filteredTags = this.tags
  }

  filter(name: string) {
    this.filteredTags = _.filter(this.tags, i => i.value === name)
  }

  assignTags() {
    /* tslint:disable */
    this.tags = [
      {
        'value': 'topic',
        'score': 1,
        'valueEscaped': 'topic',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'welcome',
        'score': 6,
        'valueEscaped': 'welcome',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'tag1',
        'score': 156,
        'valueEscaped': 'tag1',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'tag 2',
        'score': 1,
        'valueEscaped': 'tag 2',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'tag 3',
        'score': 1,
        'valueEscaped': 'tag 3',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'tag 3',
        'score': 20,
        'valueEscaped': 'Tag welcome',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'Tag welcome',
        'score': 100,
        'valueEscaped': 'Tag welcome',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'Tag welcome',
        'score': 1,
        'valueEscaped': 'Tag welcome',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'Tag welcome',
        'score': 1,
        'valueEscaped': 'Tag welcome',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'Tag welcome',
        'score': 1,
        'valueEscaped': 'Tag welcome',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'Tag welcome',
        'score': 1,
        'valueEscaped': 'Tag welcome',
        'color': '',
        'bgColor': ''
      },
      {
        'value': 'Tag welcome',
        'score': 1,
        'valueEscaped': 'Tag welcome',
        'color': '',
        'bgColor': ''
      }
    ]
    /* tslint:enable */

  }
}
