
import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { FormGroup, FormBuilder } from '@angular/forms'

@Component({
  selector: 'app-discuss-discussion',
  templateUrl: './discussion.component.html',
  styleUrls: ['./discussion.component.scss'],
  // tslint:disable-next-line
  host: { class: 'flex flex-1 margin-top-l' }
})
export class DiscussionComponent implements OnInit {
  postAnswerForm!: FormGroup
  data!: NSDiscussData.IDiscussionData

  constructor(private formBuilder: FormBuilder) {

  }
  ngOnInit(): void {
    this.assignData()
    this.postAnswerForm = this.formBuilder.group({
      answer: [],
    })
  }
  assignData() {
    // tslint:disable
    this.data = {
      "cid": 2,
      "lastposttime": 1600059162772,
      "mainPid": 7,
      "postcount": 3,
      "slug": "5/new-topic",
      "tid": 5,
      "timestamp": 1600058707867,
      "title": "What is the meaning of Project?",
      "uid": 2,
      "viewcount": 6,
      "teaserPid": 10,
      "thumb": "",
      "deleted": 0,
      "locked": 0,
      "pinned": 0,
      "upvotes": 0,
      "downvotes": 0,
      "deleterUid": 0,
      "titleRaw": "What is the meaning of Project?",
      "timestampISO": "2020-09-14T04:45:07.867Z",
      "lastposttimeISO": "2020-09-14T04:52:42.772Z",
      "votes": 0,
      "tags": [
        {
          "value": "topic",
          "valueEscaped": "topic",
          "color": "",
          "bgColor": "",
          "score": 2
        },
        {
          "value": "welcome",
          "valueEscaped": "welcome",
          "color": "",
          "bgColor": "",
          "score": 2
        },
        {
          "value": "new",
          "valueEscaped": "new",
          "color": "",
          "bgColor": "",
          "score": 1
        }
      ],
      "posts": [
        {
          "content": "<p dir=\"auto\">new topic new topic new topic new topic new topic new topic</p>\n",
          "pid": 7,
          "tid": 5,
          "timestamp": 1600058707867,
          "uid": 2,
          "bookmarks": 1,
          "deleted": 0,
          "deleterUid": 0,
          "downvotes": 0,
          "edited": 1600365438917,
          "editedISO": "2020-09-17T17:57:18.917Z",
          "editor": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin"
          },
          "timestampISO": "2020-09-14T04:45:07.867Z",
          "upvotes": 0,
          "votes": 0,
          "index": 0,
          "user": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin",
            "reputation": 3,
            "postcount": 13,
            "topiccount": 4,
            "picture": null,
            "signature": "",
            "banned": 0,
            "banned:expire": 0,
            "status": "online",
            "lastonline": 1600365178879,
            "groupTitle": "[\"administrators\"]",
            "groupTitleArray": [
              "administrators"
            ],
            "icon:text": "A",
            "icon:bgColor": "#673ab7",
            "lastonlineISO": "2020-09-17T17:52:58.879Z",
            "banned_until": 0,
            "banned_until_readable": "Not Banned",
            "selectedGroups": [],
            "custom_profile_info": []
          },
          "bookmarked": true,
          "upvoted": false,
          "downvoted": false,
          "replies": {
            "hasMore": false,
            "users": [],
            "text": "[[topic:one_reply_to_this_post]]",
            "count": 0
          },
          "selfPost": true,
          "display_edit_tools": true,
          "display_delete_tools": true,
          "display_moderator_tools": true,
          "display_move_tools": false,
          "display_post_menu": true
        },
        {
          "content": "<p dir=\"auto\">do something in this.</p>\n",
          "pid": 9,
          "tid": 5,
          "timestamp": 1600059125295,
          "uid": 2,
          "deleted": 0,
          "upvotes": 0,
          "downvotes": 0,
          "deleterUid": 0,
          "edited": 0,
          "votes": 0,
          "timestampISO": "2020-09-14T04:52:05.295Z",
          "editedISO": "",
          "index": 1,
          "user": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin",
            "reputation": 3,
            "postcount": 13,
            "topiccount": 4,
            "picture": null,
            "signature": "",
            "banned": 0,
            "banned:expire": 0,
            "status": "online",
            "lastonline": 1600365178879,
            "groupTitle": "[\"administrators\"]",
            "groupTitleArray": [
              "administrators"
            ],
            "icon:text": "A",
            "icon:bgColor": "#673ab7",
            "lastonlineISO": "2020-09-17T17:52:58.879Z",
            "banned_until": 0,
            "banned_until_readable": "Not Banned",
            "selectedGroups": [],
            "custom_profile_info": []
          },
          "editor": null,
          "bookmarked": false,
          "upvoted": false,
          "downvoted": false,
          "replies": {
            "hasMore": false,
            "users": [],
            "text": "[[topic:one_reply_to_this_post]]",
            "count": 0
          },
          "selfPost": true,
          "display_edit_tools": true,
          "display_delete_tools": true,
          "display_moderator_tools": true,
          "display_move_tools": true,
          "display_post_menu": true
        },
        {
          "content": "<p dir=\"auto\">Don't do something in this.</p>\n",
          "pid": 10,
          "tid": 5,
          "timestamp": 1600059162772,
          "uid": 2,
          "deleted": 0,
          "upvotes": 0,
          "downvotes": 0,
          "deleterUid": 0,
          "edited": 0,
          "votes": 0,
          "timestampISO": "2020-09-14T04:52:42.772Z",
          "editedISO": "",
          "index": 2,
          "user": {
            "uid": 2,
            "username": "admin",
            "userslug": "admin",
            "reputation": 3,
            "postcount": 13,
            "topiccount": 4,
            "picture": null,
            "signature": "",
            "banned": 0,
            "banned:expire": 0,
            "status": "online",
            "lastonline": 1600365178879,
            "groupTitle": "[\"administrators\"]",
            "groupTitleArray": [
              "administrators"
            ],
            "icon:text": "A",
            "icon:bgColor": "#673ab7",
            "lastonlineISO": "2020-09-17T17:52:58.879Z",
            "banned_until": 0,
            "banned_until_readable": "Not Banned",
            "selectedGroups": [],
            "custom_profile_info": []
          },
          "editor": null,
          "bookmarked": false,
          "upvoted": false,
          "downvoted": false,
          "replies": {
            "hasMore": false,
            "users": [],
            "text": "[[topic:one_reply_to_this_post]]",
            "count": 0
          },
          "selfPost": true,
          "display_edit_tools": true,
          "display_delete_tools": true,
          "display_moderator_tools": true,
          "display_move_tools": true,
          "display_post_menu": true
        }
      ],
      "category": {
        "bgColor": "#59b3d0",
        "cid": 2,
        "class": "col-md-3 col-xs-6",
        "color": "#fff",
        "description": "A place to talk about whatever you want",
        "descriptionParsed": "<p>A place to talk about whatever you want</p>\n",
        "disabled": 0,
        "icon": "fa-comments-o",
        "imageClass": "cover",
        "isSection": 0,
        "link": "",
        "name": "General Discussion",
        "numRecentReplies": 1,
        "order": 2,
        "parentCid": 0,
        "post_count": 15,
        "slug": "2/general-discussion",
        "topic_count": 6,
        "minTags": 0,
        "maxTags": 5,
        "totalPostCount": 15,
        "totalTopicCount": 6
      },
      "tagWhitelist": [],
      "minTags": 0,
      "maxTags": 5,
      "thread_tools": [],
      "isFollowing": true,
      "isNotFollowing": false,
      "isIgnoring": false,
      "bookmark": null,
      "postSharing": [],
      "deleter": null,
      "merger": null,
      "related": [],
      "unreplied": false,
      "icons": [],
      "privileges": {
        "topics:reply": true,
        "topics:read": true,
        "topics:tag": true,
        "topics:delete": true,
        "posts:edit": true,
        "posts:history": true,
        "posts:delete": true,
        "posts:view_deleted": true,
        "read": true,
        "purge": true,
        "view_thread_tools": true,
        "editable": true,
        "deletable": true,
        "view_deleted": true,
        "isAdminOrMod": true,
        "disabled": 0,
        "tid": "5",
        "uid": 2
      },
      "topicStaleDays": 60,
      // "reputation:disabled": 0,
      // "downvote:disabled": 0,
      // "feeds:disableRSS": 0,
      // "bookmarkThreshold": 5,
      // "necroThreshold": 7,
      // "postEditDuration": 0,
      // "postDeleteDuration": 0,
      // "scrollToMyPost": true,
      // "allowMultipleBadges": false,
      // "privateUploads": false,
      // "rssFeedUrl": "/topic/5.rss?uid=2&token=d1f9ead5-2191-4fef-a8d6-8a6a80482ca6",
      "postIndex": 1,
      "pagination": {
        "prev": {
          "page": 1,
          "active": false
        },
        "next": {
          "page": 1,
          "active": false
        },
        "first": {
          "page": 1,
          "active": true
        },
        "last": {
          "page": 1,
          "active": true
        },
        "rel": [],
        "pages": [],
        "currentPage": 1,
        "pageCount": 1
      }
    }
    // tslint:enable
  }
  upvote(discuss: NSDiscussData.IDiscussionData) {
    // console.log(discuss)
    if (discuss) {

    }

  }
  downvote(discuss: NSDiscussData.IDiscussionData) {
    // console.log(discuss)
    if (discuss) {

    }
  }
  showError(meta: string) {
    if (meta) {
      return true
    }
    return false
  }
}
