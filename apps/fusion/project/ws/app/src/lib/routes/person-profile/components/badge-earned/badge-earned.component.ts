import { Component, OnInit, Input, SimpleChanges, OnChanges, OnDestroy, EventEmitter, Output } from '@angular/core'
import { IBadgeRecent, IBadgeResponse } from '../../../profile/routes/badges/badges.model'
import { PersonProfileService } from '../../services/person-profile.service'
import { TFetchStatus, ValueService } from '@ws-widget/utils/src/public-api'
import { Subscription } from 'rxjs'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-app-badge-earned',
  templateUrl: './badge-earned.component.html',
  styleUrls: ['./badge-earned.component.scss'],
})
export class BadgeEarnedComponent implements OnInit, OnChanges, OnDestroy {
  @Input() wid = ''
  @Input() name = ''
  @Output() badgeCount = new EventEmitter<number>()

  badgesFetchStatus: TFetchStatus = 'none'
  earned: IBadgeRecent[] = []
  totalBadgesEarned: IBadgeRecent[] = []
  badgeCurrentDisplay: IBadgeRecent[] = []

  // paginator
  nextFollowersDisable = false
  previousFollowersDisable = false
  nextFollowingDisable = false
  previousFollowingDisable = false
  smallScreenSize = 4
  largeScreenSize = 7
  pageDisplaySize = this.largeScreenSize
  lastIndexFollowersArray = this.pageDisplaySize
  startIndexFollowersArray = 0
  lastIndexFollowingArray = this.pageDisplaySize
  startIndexFollowingArray = 0
  isInitialized = false

  isLtMedium = false
  isLtMediumSubscription: Subscription | null = null

  suggestionsBadgeLimit = 4
  private badgeSubscription: Subscription | null = null
  constructor(
    private personprofileSvc: PersonProfileService,
    private valueSvc: ValueService,
    private matSnackBar: MatSnackBar,
  ) {

  }

  ngOnChanges(changes: SimpleChanges) {
    if ((changes.wid.currentValue !== changes.wid.previousValue) && (this.isInitialized)) {
      this.wid = changes.wid.currentValue
      this.badgeCurrentDisplay = []
      this.fetchBadges()
    }
  }

  ngOnInit() {
    this.isLtMediumSubscription = this.valueSvc.isLtMedium$.subscribe(isLtMedium => {
      if (isLtMedium) {
        this.pageDisplaySize = this.smallScreenSize
      } else {
        this.pageDisplaySize = this.largeScreenSize
      }
      this.lastIndexFollowersArray = this.pageDisplaySize
      this.startIndexFollowersArray = 0
      this.lastIndexFollowingArray = this.pageDisplaySize
      this.startIndexFollowingArray = 0
    })
    if (this.wid && !this.isInitialized) {
      this.fetchBadges()
    }
    this.isInitialized = true
  }
  ngOnDestroy() {
    if (this.badgeSubscription) {
      this.badgeSubscription.unsubscribe()
    }
    if (this.isLtMediumSubscription) {
      this.isLtMediumSubscription.unsubscribe()
    }

  }

  fetchBadges() {
    this.badgesFetchStatus = 'fetching'
    this.badgeSubscription = this.personprofileSvc.getUserBadges(this.wid).subscribe(
      (result: IBadgeResponse) => {
        if (result) {
          this.totalBadgesEarned = result.earned
          this.badgeCount.emit(this.totalBadgesEarned.length)
          if (this.totalBadgesEarned.length > this.pageDisplaySize) {
            this.badgeCurrentDisplay = this.totalBadgesEarned.slice(this.startIndexFollowersArray, this.lastIndexFollowersArray)
            this.previousFollowersDisable = true

          } else { this.badgeCurrentDisplay = this.totalBadgesEarned }
        }
        this.badgesFetchStatus = 'done'
      },
      () => {
        this.badgesFetchStatus = 'error'
        this.openSnackBar('Error while fetching badges.')
      })
  }

  private openSnackBar(message: string) {
    this.matSnackBar.open(message)
  }

  // viewAllBadge() {
  //   this.dialog.open(UserdetailallComponent, {
  //     width: '70%',
  //     data: {
  //       name: 'Badges Earned',
  //       tag: 'badgesearned',
  //       content: this.totalBadgesEarned,
  //       userName: this.name,
  //     },
  //   })
  // }

  fetchNextBadge() {
    if (this.previousFollowersDisable) {
      this.previousFollowersDisable = false
    }
    this.badgeCurrentDisplay = []
    this.startIndexFollowersArray += this.pageDisplaySize
    this.lastIndexFollowersArray += this.pageDisplaySize
    if (this.lastIndexFollowersArray >= this.totalBadgesEarned.length) {
      this.nextFollowersDisable = true

    }
    this.badgeCurrentDisplay = this.totalBadgesEarned.slice(this.startIndexFollowersArray, this.lastIndexFollowersArray)
  }

  fetchPreviousBadge() {
    if (this.nextFollowersDisable) {
      this.nextFollowersDisable = false
    }

    this.badgeCurrentDisplay = []
    this.startIndexFollowersArray -= this.pageDisplaySize
    this.lastIndexFollowersArray -= this.pageDisplaySize
    if (this.startIndexFollowersArray <= 0) {
      this.startIndexFollowersArray = 0
      this.previousFollowersDisable = true
    }
    this.badgeCurrentDisplay = this.totalBadgesEarned.slice(this.startIndexFollowersArray, this.lastIndexFollowersArray)
  }

}
