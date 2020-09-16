
import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'

@Component({
  selector: 'app-discuss-groups',
  templateUrl: './discuss-groups.component.html',
  styleUrls: ['./discuss-groups.component.scss'],
})
export class DiscussGroupsComponent implements OnInit {
  categories!: NSDiscussData.ICategorie[]
  constructor() { }

  ngOnInit(): void {
    /* tslint:disable */
    this.categories = [
      {
        'bgColor': '#fda34b',
        'cid': 1,
        'class': 'col-md-3 col-xs-6',
        'color': '#fff',
        'description': 'Announcements regarding our community',
        'descriptionParsed': '<p>Announcements regarding our community</p>\n',
        'disabled': 0,
        'icon': 'fa-bullhorn',
        'imageClass': 'cover',
        'isSection': 0,
        'link': '',
        'name': 'Announcements',
        'numRecentReplies': 1,
        'order': 1,
        'parentCid': 0,
        'post_count': 1,
        'slug': '1/announcements',
        'topic_count': 1,
        'minTags': 0,
        'maxTags': 5,
        'totalPostCount': 1,
        'totalTopicCount': 1,
        'tagWhitelist': [],
        'unread-class': '',
        'children': [],
        'posts': [
          {
            'pid': 2,
            'timestamp': 1599662916238,
            'content': '',
            'timestampISO': '2020-09-09T14:48:36.238Z',
            'user': {
              'uid': 1,
              'username': 'amit',
              'userslug': 'amit',
              'picture': null,
              'icon:text': 'A',
              'icon:bgColor': '#1b5e20'
            },
            'index': 1,
            'cid': 1,
            'parentCid': 0,
            'topic': {
              'slug': '2/title',
              'title': '&#x27;TITLE&#x27;'
            }
          }
        ],
        'teaser': {
          'url': '/post/2',
          'timestampISO': '2020-09-09T14:48:36.238Z',
          'pid': 2,
          'topic': {
            'slug': '2/title',
            'title': '&#x27;TITLE&#x27;'
          }
        }
      },
      {
        'bgColor': '#59b3d0',
        'cid': 2,
        'class': 'col-md-3 col-xs-6',
        'color': '#fff',
        'description': 'A place to talk about whatever you want',
        'descriptionParsed': '<p>A place to talk about whatever you want</p>\n',
        'disabled': 0,
        'icon': 'fa-comments-o',
        'imageClass': 'cover',
        'isSection': 0,
        'link': '',
        'name': 'General Discussion',
        'numRecentReplies': 1,
        'order': 2,
        'parentCid': 0,
        'post_count': 13,
        'slug': '2/general-discussion',
        'topic_count': 6,
        'minTags': 0,
        'maxTags': 5,
        'totalPostCount': 13,
        'totalTopicCount': 6,
        'tagWhitelist': [],
        'unread-class': '',
        'children': [],
        'posts': [
          {
            'pid': 14,
            'timestamp': 1600196723963,
            'content': '2 Welcome to your brand new NodeBB forum! This is what a topic and post looks like. As an administrator, you can edit the posts title and content.To customise your forum, go to the<a href =\'../../admin\'>Administrator Control Panel</a>. You can modify all aspects of your forum there, including installation of third-party plugins. ## Additional Resources * <a href=\'https://docs.nodebb.org\' rel=\'nofollow\'>NodeBB Documentation</a> * <a href=\'https://community.nodebb.org\' rel=\'nofollow\'>Community Support Forum</a>* <a href=\'https://github.com/nodebb/nodebb\' rel=\'nofollow\'>Project repository</a>\n',
            'timestampISO': '2020-09-15T19:05:23.963Z',
            'user': {
              'uid': 2,
              'username': 'admin',
              'userslug': 'admin',
              'picture': null,
              'icon:text': 'A',
              'icon:bgColor': '#673ab7'
            },
            'index': 1,
            'cid': 2,
            'parentCid': 0,
            'topic': {
              'slug': '7/topic-2',
              'title': 'Topic 2'
            }
          }
        ],
        'teaser': {
          'url': '/post/14',
          'timestampISO': '2020-09-15T19:05:23.963Z',
          'pid': 14,
          'topic': {
            'slug': '7/topic-2',
            'title': 'Topic 2'
          }
        }
      },
      {
        'bgColor': '#e95c5a',
        'cid': 4,
        'class': 'col-md-3 col-xs-6',
        'color': '#fff',
        'description': 'Got a question? Ask away!',
        'descriptionParsed': '<p>Got a question? Ask away!</p>\n',
        'disabled': 0,
        'icon': 'fa-question',
        'imageClass': 'cover',
        'isSection': 0,
        'link': '',
        'name': 'Comments &amp; Feedback',
        'numRecentReplies': 1,
        'order': 3,
        'parentCid': 0,
        'post_count': 0,
        'slug': '4/comments-feedback',
        'topic_count': 0,
        'minTags': 0,
        'maxTags': 5,
        'totalPostCount': 0,
        'totalTopicCount': 0,
        'tagWhitelist': [],
        'unread-class': '',
        'children': [],
        'posts': []
      },
      {
        'bgColor': '#86ba4b',
        'cid': 3,
        'class': 'col-md-3 col-xs-6',
        'color': '#fff',
        'description': 'Blog posts from individual members',
        'descriptionParsed': '<p>Blog posts from individual members</p>\n',
        'disabled': 0,
        'icon': 'fa-newspaper-o',
        'imageClass': 'cover',
        'isSection': 0,
        'link': '',
        'name': 'Blogs',
        'numRecentReplies': 1,
        'order': 4,
        'parentCid': 0,
        'post_count': 0,
        'slug': '3/blogs',
        'topic_count': 0,
        'minTags': 0,
        'maxTags': 5,
        'totalPostCount': 0,
        'totalTopicCount': 0,
        'tagWhitelist': [],
        'unread-class': '',
        'children': [],
        'posts': []
      }
    ]
    /* tslint:enable */
  }
}
