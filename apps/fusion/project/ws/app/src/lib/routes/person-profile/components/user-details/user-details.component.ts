import {
  Component,
  OnInit,
  Input,
  SimpleChanges,
  OnChanges,
  EventEmitter,
  Output,
} from '@angular/core'
import { ConfigurationsService, TFetchStatus } from '@ws-widget/utils/src/public-api'
import { IFollowerId } from '../../person-profile.model'
import { PersonProfileService } from '../../services/person-profile.service'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
})
export class UserDetailsComponent implements OnInit, OnChanges {
  @Input() wid = ''
  @Input() name = ''
  @Input() followersCount = ''
  @Input() followingCount = ''
  @Input() followers: IFollowerId[] = []
  @Input() following: IFollowerId[] = []
  @Input() enabledFeatures: string[] = []
  @Output() interestEnabled = new EventEmitter<boolean>()

  isViewmore = true
  badgeFetchStatus: TFetchStatus = 'none'
  userPref: string[] = []
  currentUserFollowers: string[] = []
  fetchStatus: TFetchStatus = 'none'

  knowledgeBoardEnabled = true
  playlistEnabled = true
  goalsEnabled = true
  isBadgesEnabled = true
  isSkillsEnabled = false
  isBlogsEnabled = true
  isQnaEnabled = true
  isReviewEnabled = true
  isAuthorEnabled = true
  currentUserId = ''

  badgesEarnedPresent = true
  authorContentPresent = true
  reviewContentPresent = true
  qnaContentPresent = true
  blogContentPresent = true
  kbContentPresent = true
  playlistContentPresent = true

  achivementsEnabled = true
  contributionEnabled = false

  fetchingBadges = false
  kbFetchingDone = false
  playlistsFetchingDone = false
  authoredFetching = false
  reviewedFetching = false

  blogFetching = false
  isInitialized = false
  isQnaFetching = false

  constructor(
    private configSvc: ConfigurationsService,
    private personProfileSvc: PersonProfileService,
    private matSnackBar: MatSnackBar,
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    if (this.isInitialized) {
      if (changes.wid) {
        if (changes.wid.currentValue !== changes.wid.previousValue) {
          this.fetchStatus = 'fetching'
          this.wid = changes.wid.currentValue
          this.checkingDetails()
          this.fetchUserPreference(this.wid)
          this.fetchCurrentUserFollowers()
          this.fetchStatus = 'done'
        }
      }
    }
  }

  ngOnInit() {
    if (this.configSvc.userProfile) {
      this.currentUserId = this.configSvc.userProfile.userId
    }
    /*  if (this.name.split(' ').length > 1) {
      this.name = this.name.split(' ')[0]
    } */
    this.isInitialized = true
    this.fetchStatus = 'fetching'
    this.fetchUserPreference(this.wid)
    this.checkingDetails()
    this.fetchCurrentUserFollowers()

    this.fetchStatus = 'done'
    // this.isBlogsEnabled = (this.configSvc.restrictedFeatures && this.configSvc.restrictedFeatures.has('social')) || false
  }

  checkingDetails() {
    this.kbFetchingDone = false
    this.playlistsFetchingDone = false
    if (this.userPref) {
      if (this.enabledFeatures.includes('badges')) {
        this.isBadgesEnabled = true
      }
      /*  if (this.enabledFeatures.includes('skills')) {
         this.isSkillsEnabled = false
       } */
      if (this.configSvc.restrictedFeatures) {
        if (this.configSvc.restrictedFeatures.has('knowledgeBoard')) {
          this.knowledgeBoardEnabled = false
        }
        if (this.configSvc.restrictedFeatures.has('playlist')) {
          this.playlistEnabled = false
        }
        /* if (this.configSvc.restrictedFeatures.has('goals')) {
          this.goalsEnabled = false
        } */
        if (this.configSvc.restrictedFeatures.has('blogs')) {
          this.isBlogsEnabled = false
        }
        if (this.configSvc.restrictedFeatures.has('qna')) {
          this.isQnaEnabled = false
        }
        if (this.configSvc.restrictedFeatures.has('review')) {
          this.isReviewEnabled = false
        }
        if (this.configSvc.restrictedFeatures.has('create')) {
          this.isAuthorEnabled = false
        }
      }
    }
  }

  preferenceCheck() {
    if (this.userPref.length > 0) {
      this.isBadgesEnabled = this.isBadgesEnabled && !this.userPref.includes('badges')
      // this.isSkillsEnabled = this.isSkillsEnabled && !this.userPref.includes('skills')
      this.knowledgeBoardEnabled =
        this.knowledgeBoardEnabled && !this.userPref.includes('knowledgeBoard')
      this.playlistEnabled = this.playlistEnabled && !this.userPref.includes('playlist')
      // this.goalsEnabled = this.goalsEnabled && !this.userPref.includes('goals')
      this.isBlogsEnabled = this.isBlogsEnabled && !this.userPref.includes('blogs')
      this.isQnaEnabled = this.isQnaEnabled && !this.userPref.includes('qna')
      this.isReviewEnabled = this.isReviewEnabled && !this.userPref.includes('review')
      this.isAuthorEnabled = this.isAuthorEnabled && !this.userPref.includes('create')
      this.interestEnabled.emit(!this.userPref.includes('interests'))
    }
    // this.achivementsEnabled = ((this.isBadgesEnabled ? this.badgesEarnedPresent : false) || this.isSkillsEnabled)
    /* this.contributionEnabled = (this.isBlogsEnabled && this.blogContentPresent) || (this.isAuthorEnabled && this.authorContentPresent)
      || (this.isQnaEnabled && this.qnaContentPresent) || (this.isReviewEnabled && this.reviewContentPresent) */
  }

  fetchingKbDone(event: Boolean) {
    if (event) {
      this.kbFetchingDone = true
    }
  }

  fetchingPlaylistDone(event: Boolean) {
    if (event) {
      this.playlistsFetchingDone = true
    }
  }

  fetchingContentAuthored(event: boolean) {
    this.authoredFetching = event
  }

  fetchingContentReviewed(event: boolean) {
    this.reviewedFetching = event
  }
  fetchingQna(event: boolean) {
    this.isQnaFetching = event
  }
  fetchingBlog(event: boolean) {
    this.blogFetching = event
  }

  async fetchUserPreference(wid: string) {
    const userPrefPromise = await this.personProfileSvc.fetchUserPreference(wid)
    if (userPrefPromise.profileSettings) {
      userPrefPromise.profileSettings.forEach(pref => this.userPref.push(pref))
    }
    this.preferenceCheck()
  }

  checkBadgeEnabled(event: number) {
    // checks for badges earned - count
    if (event === 0) {
      this.badgesEarnedPresent = false
    }
  }
  checkAuthoredEnabled(event: number) {
    if (event === 0) {
      this.authorContentPresent = false
    }
  }
  checkReviewEnabled(event: number) {
    if (event === 0) {
      this.reviewContentPresent = false
    }
  }
  checkBlogEnabled(event: number) {
    if (event === 0) {
      this.blogContentPresent = false
    }
  }
  checkQnaEnabled(event: number) {
    if (event === 0) {
      this.qnaContentPresent = false
    }
  }
  checkKbEnabled(event: number) {
    if (event === 0) {
      this.kbContentPresent = false
    }
  }
  checkPlaylistEnabled(event: number) {
    if (event === 0) {
      this.playlistContentPresent = false
    }
  }
  fetchCurrentUserFollowers() {
    this.personProfileSvc.getFollowingv3(this.currentUserId, true, true, ['person']).subscribe(
      (data: any) => {
        if (data && data.person) {
          data.person.data.forEach((person: IFollowerId) => {
            this.currentUserFollowers.push(person.identifier)
          })
        } else {
          this.currentUserFollowers = []
        }
      },
      () => {
        this.openSnackBar('Error while fetching followers.')
      },
    )
  }
  private openSnackBar(message: string) {
    this.matSnackBar.open(message)
  }
}
