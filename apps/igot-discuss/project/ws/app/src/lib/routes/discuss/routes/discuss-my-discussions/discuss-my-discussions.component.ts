
import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { NSDiscussData } from '../../models/discuss.model'
import { DiscussService } from '../../services/discuss.service'
import { ConfigurationsService } from '@ws-widget/utils'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'app-discuss-my-discussions',
  templateUrl: './discuss-my-discussions.component.html',
  styleUrls: ['./discuss-my-discussions.component.scss'],
  // tslint:disable-next-line
  host: { class: 'flex flex-1 margin-top-l' },
})
export class DiscussMyDiscussionsComponent implements OnInit {
  data!: NSDiscussData.IProfile // this is for user
  discussionList!: NSDiscussData.IPosts[] // this is for posts
  currentFilter = 'timestamp'
  department!: string | null
  location!: string | null
  profilePhoto!: string
  constructor(private route: ActivatedRoute, private discussService: DiscussService, private configSvc: ConfigurationsService) {
    this.fetchNetworkProfile()
  }
  fetchNetworkProfile() {
    this.discussService.fetchNetworkProfile().subscribe(response => {
      this.profilePhoto = _.get(_.first(response), 'photo')
      if (this.configSvc.userProfile) {
        localStorage.setItem(this.configSvc.userProfile.userId, this.profilePhoto)
      }
    },
      /* tslint:disable */
      () => {
        this.profilePhoto = ''
      })
    /* tslint:enable */
  }

  ngOnInit() {
    // this.fillDummyData()
    this.data = this.route.snapshot.data.profile.data
    this.discussionList = _.uniqBy(_.filter(this.data.posts, p => _.get(p, 'isMainPost') === true), 'tid')
    this.department = this.discussService.getUserProfile.departmentName || null
    this.location = this.discussService.getUserProfile.country || null
  }
  filter(key: string | 'timestamp' | 'best' | 'saved' | 'watched' | 'upvoted' | 'downvoted') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'timestamp':
          this.discussionList = _.uniqBy(_.filter(this.data.posts, p => _.get(p, 'isMainPost') === true), 'tid')
          break
        case 'best':
          this.discussionList = _.uniqBy(this.data.bestPosts, 'tid')
          break
        case 'saved':
          this.discussService.fetchSaved().subscribe(response => {
            if (response) {
              this.discussionList = _.uniqBy(response.posts, 'tid')
            } else {
              this.discussionList = []
            }
          },
            // tslint:disable-next-line
            () => {
              this.discussionList = []
            })
          break
        case 'watched':
          this.discussionList = []
          break
        case 'upvoted':
          this.discussService.fetchUpvoted().subscribe(response => {
            if (response) {
              this.discussionList = _.uniqBy(response.posts, 'tid')
            } else {
              this.discussionList = []
            }
          },
            // tslint:disable-next-line
            () => {
              this.discussionList = []
            })

          break
        case 'downvoted':
          this.discussService.fetchDownvoted().subscribe(response => {
            if (response) {
              this.discussionList = _.uniqBy(response.posts, 'tid')
            } else {
              this.discussionList = []
            }
          },
            // tslint:disable-next-line
            () => {
              this.discussionList = []
            })
          break
        default:
          this.discussionList = _.uniqBy(this.data.latestPosts, 'tid')
          break
      }
    }
  }
}
