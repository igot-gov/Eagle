import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'app-discuss-related-discussion',
  templateUrl: './related-discussion.component.html',
  styleUrls: ['./related-discussion.component.scss'],
  // tslint:disable-next-line
  host: { class: 'margin-left-l' },
})
export class RelatedDiscussionComponent implements OnInit {
  discussions!: NSDiscussData.IRelatedDiscussion[]
  constructor() {

  }
  ngOnInit(): void {
    this.discussions = [
      {
        category: 'Category name',
        id: 1,
        time: '3 hours ago',
        title: 'What is Dicey’s Rule of law?',
      }, {
        category: 'Category name',
        id: 1,
        time: '3 hours ago',
        title: 'What is Dicey’s Rule of law?',
      },
      {
        category: 'Category name',
        id: 1,
        time: '3 hours ago',
        title: 'What is Dicey’s Rule of law?',
      },
      {
        category: 'Category name',
        id: 1,
        time: '3 hours ago',
        title: 'What is Dicey’s Rule of law?',
      },
    ]
  }
}
