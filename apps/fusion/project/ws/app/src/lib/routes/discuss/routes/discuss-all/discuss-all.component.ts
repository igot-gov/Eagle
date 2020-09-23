
import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { MatDialog } from '@angular/material/dialog'
import { DiscussStartComponent } from '../../components/discuss-start/discuss-start.component'
import { ActivatedRoute } from '@angular/router'
import { DiscussService } from '../../services/discuss.service'

@Component({
  selector: 'app-discuss-all',
  templateUrl: './discuss-all.component.html',
  styleUrls: ['./discuss-all.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class DiscussAllComponent implements OnInit {
  currentFilter = 'recent'
  trendingTags!: NSDiscussData.ITag[]
  discussionList!: NSDiscussData.IDiscussionData[]
  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private discussService: DiscussService
  ) {
    this.trendingTags = this.route.snapshot.data.availableTags.data.tags
    this.discussionList = this.route.snapshot.data.recent.data
  }
  ngOnInit() {
    // this.fillDummyData()
    // console.log(this.discussionList)
  }
  start() {
    // const dialogRef =
    this.dialog.open(DiscussStartComponent, {
      minHeight: 'auto',
      width: '80%',
      panelClass: 'remove-pad',
    })
    // dialogRef.afterClosed().subscribe((response: any) => {
    //   console.log(response)

    // })
  }
  filter(key: string | 'recent' | 'popular' | 'watched') {
    if (key) {
      this.currentFilter = key
    }
    switch (key) {
      case 'recent':
        this.fillrecent()
        break
      case 'popular':
        this.fillPopular()
        break
      default:
        break
    }
  }
  fillrecent() {
    this.discussionList = this.route.snapshot.data.recent.data
  }
  fillPopular() {
    this.discussionList = []
    this.discussService.fetchPopularD().subscribe(response => {
      this.discussionList = response
    })
    // , () => {
    //   // IN TROUBL
    // })
  }
  fillDummyData() {
    /* tslint:disable */
    this.discussionList = [
      {
        "cid": 2,
        "lastposttime": 1600146633396,
        "mainPid": 11,
        "postcount": 1,
        "slug": "6/what-is-the-meaning-of-project",
        "tid": 6,
        "timestamp": 1600146633396,
        "title": "What is the meaning of Project?",
        "uid": 2,
        "viewcount": 1,
        "deleted": 0,
        "locked": 0,
        "pinned": 0,
        "upvotes": 0,
        "downvotes": 0,
        "deleterUid": 0,
        "titleRaw": "What is the meaning of Project?",
        "timestampISO": "2020-09-15T05:10:33.396Z",
        "lastposttimeISO": "2020-09-15T05:10:33.396Z",
        "votes": 0,
        "teaserPid": null,
        "category": {
          "cid": 2,
          "name": "General Discussion",
          "slug": "2/general-discussion",
          "icon": "fa-comments-o",
          "backgroundImage": null,
          "imageClass": "cover",
          "bgColor": "#59b3d0",
          "color": "#fff",
          "disabled": 0
        },
        "user": {
          "uid": 2,
          "username": "admin",
          "userslug": "admin",
          "reputation": 2,
          "postcount": 8,
          "picture": null,
          "signature": null,
          "banned": 0,
          "status": "online",
          "icon:text": "A",
          "icon:bgColor": "#673ab7",
          "banned_until_readable": "Not Banned"
        },
        "teaser": {
          "pid": 13,
          "uid": 2,
          "timestamp": 1600153208232,
          "tid": 6,
          "content": "<p dir=\"auto\">gain project meaning is project.</p>\n",
          "timestampISO": "2020-09-15T07:00:08.232Z",
          "user": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin",
            "picture": null,
            "icon:text": "A",
            "icon:bgColor": "#673ab7"
          },
          "index": 3
        },
        "tags": [{
          "value": "tag 2",
          "valueEscaped": "tag 2",
          "color": "",
          "bgColor": "",
          "score": 1
        },
        {
          "value": "tag 3",
          "valueEscaped": "tag 3",
          "color": "",
          "bgColor": "",
          "score": 1
        }],
        "isOwner": true,
        "ignored": false,
        "unread": false,
        "bookmark": null,
        "unreplied": true,
        "icons": [],
        "index": 0
      }, {
        "cid": 2,
        "lastposttime": 1600146633396,
        "mainPid": 11,
        "postcount": 1,
        "slug": "6/what-is-the-meaning-of-project",
        "tid": 6,
        "timestamp": 1600146633396,
        "title": "What is the meaning of Project?",
        "uid": 2,
        "viewcount": 1,
        "deleted": 0,
        "locked": 0,
        "pinned": 0,
        "upvotes": 0,
        "downvotes": 0,
        "deleterUid": 0,
        "titleRaw": "What is the meaning of Project?",
        "timestampISO": "2020-09-15T05:10:33.396Z",
        "lastposttimeISO": "2020-09-15T05:10:33.396Z",
        "votes": 0,
        "teaserPid": null,
        "category": {
          "cid": 2,
          "name": "General Discussion",
          "slug": "2/general-discussion",
          "icon": "fa-comments-o",
          "backgroundImage": null,
          "imageClass": "cover",
          "bgColor": "#59b3d0",
          "color": "#fff",
          "disabled": 0
        },
        "user": {
          "uid": 2,
          "username": "admin",
          "userslug": "admin",
          "reputation": 2,
          "postcount": 8,
          "picture": null,
          "signature": null,
          "banned": 0,
          "status": "online",
          "icon:text": "A",
          "icon:bgColor": "#673ab7",
          "banned_until_readable": "Not Banned"
        },
        "teaser": {
          "pid": 13,
          "uid": 2,
          "timestamp": 1600153208232,
          "tid": 6,
          "content": "<p dir=\"auto\">gain project meaning is project.</p>\n",
          "timestampISO": "2020-09-15T07:00:08.232Z",
          "user": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin",
            "picture": null,
            "icon:text": "A",
            "icon:bgColor": "#673ab7"
          },
          "index": 3
        },
        "tags": [{
          "value": "tag 2",
          "valueEscaped": "tag 2",
          "color": "",
          "bgColor": "",
          "score": 1
        },
        {
          "value": "tag 3",
          "valueEscaped": "tag 3",
          "color": "",
          "bgColor": "",
          "score": 1
        }],
        "isOwner": true,
        "ignored": false,
        "unread": false,
        "bookmark": null,
        "unreplied": true,
        "icons": [],
        "index": 0
      }, {
        "cid": 2,
        "lastposttime": 1600146633396,
        "mainPid": 11,
        "postcount": 1,
        "slug": "6/what-is-the-meaning-of-project",
        "tid": 6,
        "timestamp": 1600146633396,
        "title": "What is the meaning of Project?",
        "uid": 2,
        "viewcount": 1,
        "deleted": 0,
        "locked": 0,
        "pinned": 0,
        "upvotes": 0,
        "downvotes": 0,
        "deleterUid": 0,
        "titleRaw": "What is the meaning of Project?",
        "timestampISO": "2020-09-15T05:10:33.396Z",
        "lastposttimeISO": "2020-09-15T05:10:33.396Z",
        "votes": 0,
        "teaserPid": null,
        "category": {
          "cid": 2,
          "name": "General Discussion",
          "slug": "2/general-discussion",
          "icon": "fa-comments-o",
          "backgroundImage": null,
          "imageClass": "cover",
          "bgColor": "#59b3d0",
          "color": "#fff",
          "disabled": 0
        },
        "user": {
          "uid": 2,
          "username": "admin",
          "userslug": "admin",
          "reputation": 2,
          "postcount": 8,
          "picture": null,
          "signature": null,
          "banned": 0,
          "status": "online",
          "icon:text": "A",
          "icon:bgColor": "#673ab7",
          "banned_until_readable": "Not Banned"
        },
        "teaser": {
          "pid": 13,
          "uid": 2,
          "timestamp": 1600153208232,
          "tid": 6,
          "content": "<p dir=\"auto\">gain project meaning is project.</p>\n",
          "timestampISO": "2020-09-15T07:00:08.232Z",
          "user": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin",
            "picture": null,
            "icon:text": "A",
            "icon:bgColor": "#673ab7"
          },
          "index": 3
        },
        "tags": [{
          "value": "tag 2",
          "valueEscaped": "tag 2",
          "color": "",
          "bgColor": "",
          "score": 1
        },
        {
          "value": "tag 3",
          "valueEscaped": "tag 3",
          "color": "",
          "bgColor": "",
          "score": 1
        }],
        "isOwner": true,
        "ignored": false,
        "unread": false,
        "bookmark": null,
        "unreplied": true,
        "icons": [],
        "index": 0
      }, {
        "cid": 2,
        "lastposttime": 1600146633396,
        "mainPid": 11,
        "postcount": 1,
        "slug": "6/what-is-the-meaning-of-project",
        "tid": 6,
        "timestamp": 1600146633396,
        "title": "What is the meaning of Project?",
        "uid": 2,
        "viewcount": 1,
        "deleted": 0,
        "locked": 0,
        "pinned": 0,
        "upvotes": 0,
        "downvotes": 0,
        "deleterUid": 0,
        "titleRaw": "What is the meaning of Project?",
        "timestampISO": "2020-09-15T05:10:33.396Z",
        "lastposttimeISO": "2020-09-15T05:10:33.396Z",
        "votes": 0,
        "teaserPid": null,
        "category": {
          "cid": 2,
          "name": "General Discussion",
          "slug": "2/general-discussion",
          "icon": "fa-comments-o",
          "backgroundImage": null,
          "imageClass": "cover",
          "bgColor": "#59b3d0",
          "color": "#fff",
          "disabled": 0
        },
        "user": {
          "uid": 2,
          "username": "admin",
          "userslug": "admin",
          "reputation": 2,
          "postcount": 8,
          "picture": null,
          "signature": null,
          "banned": 0,
          "status": "online",
          "icon:text": "A",
          "icon:bgColor": "#673ab7",
          "banned_until_readable": "Not Banned"
        },
        "teaser": {
          "pid": 13,
          "uid": 2,
          "timestamp": 1600153208232,
          "tid": 6,
          "content": "<p dir=\"auto\">gain project meaning is project.</p>\n",
          "timestampISO": "2020-09-15T07:00:08.232Z",
          "user": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin",
            "picture": null,
            "icon:text": "A",
            "icon:bgColor": "#673ab7"
          },
          "index": 3
        },
        "tags": [{
          "value": "tag 2",
          "valueEscaped": "tag 2",
          "color": "",
          "bgColor": "",
          "score": 1
        },
        {
          "value": "tag 3",
          "valueEscaped": "tag 3",
          "color": "",
          "bgColor": "",
          "score": 1
        }],
        "isOwner": true,
        "ignored": false,
        "unread": false,
        "bookmark": null,
        "unreplied": true,
        "icons": [],
        "index": 0
      }
    ]
    /* tslint:enable */
  }
}
