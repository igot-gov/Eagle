import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { BtnFollowService, NsContent, NsContentStripMultiple, ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ConfigurationsService, TFetchStatus } from '@ws-widget/utils'
import { NSProfileData } from '../../../../models/profile.model'
import { ProfileService } from '../../../../services/profile.service'
import { IBadgeRecent, IBadgeResponse } from '../../../badges/badges.model'
// import { BadgesService } from '../../../badges/badges.service'
import { InterestService } from '../../../interest/services/interest.service'
import { NSLearningHistory } from '../../../learning/models/learning.models'
import { LearningHistoryService } from '../../../learning/services/learning-history.service'
import { NsSkills } from '../../../skills/models/skills.model'

interface ILearningHistoryContent {
  content: NSLearningHistory.ILearningHistory
  contentType: string
  pageNum: number
  loading: boolean
  isLoadingFirstTime: boolean
  fetchStatus: 'fetching' | 'done' | 'error'
}
@Component({
  selector: 'ws-app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})

export class DashboardComponent implements OnInit {
  startDate = '2018-04-01'
  endDate = '2020-03-31'
  specialDates: number[] = []
  followContent: NSProfileData.IFollowing[] = []
  knowledgeBoardFollow: NsContent.IContentMinimal[] = []
  channelsFollow: NsContent.IContentMinimal[] = []
  contentType = 'Course'
  timeEvent = new Date()
  isCompleted = 0
  userPointsEarned = 0
  orgWidePointsPercent = 0
  orgWideTimePercent = 0
  totalLearningHours = -1
  badgesData: IBadgeResponse | null = null
  badgesEarned: IBadgeRecent[] | null = null
  interests: string[] | null = null
  nsoArtifacts: NSProfileData.INsoResponse | null = null
  userName = ''
  userEmail = ''
  departmentName = ''
  skillData: any
  skillFetchStatus: TFetchStatus = 'none'
  badgeApiFetchStatus: TFetchStatus = 'none'
  interestFetchStatus: TFetchStatus = 'none'
  userDataFetchStatus: TFetchStatus = 'none'
  nsoArtifactsFetchStatus: TFetchStatus = 'none'
  followFetchStatus: TFetchStatus = 'none'
  historyFetchStatus: TFetchStatus = 'none'
  apiFetchStatus: TFetchStatus = 'none'
  pointsEarn: number | null = 0
  timeSpentData: NSProfileData.ITimeSpentResponse | null = null
  timeSpent = 0
  enabledTabs = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.dashboard
  widgetResolverData: NsWidgetResolver.IRenderConfigWithTypedData<NsContentStripMultiple.IContentStripMultiple> = {
    widgetData: {
      strips: [
        {
          key: 'continueLearning',
          title: 'Last viewed',

          stripConfig: {
            cardSubType: 'standard',
            deletedMode: 'hide',
            intranetMode: 'greyOut',
          },
          request: {
            api: {
              path: '/apis/protected/v8/user/history',
              queryParams: {
                pageSize: 20,
              },
            },
          },
        },
        // {
        //   key: 'likedContent',
        //   title: 'Liked Contents',
        //   stripConfig: {
        //     cardSubType: 'standard',
        //   },
        //   request: {
        //     api: {
        //       path: '/apis/protected/v8/user/content/like/contents',
        //     },
        //   },
        // },
      ],
    },
    widgetType: ROOT_WIDGET_CONFIG.contentStrip._type,
    widgetSubType: ROOT_WIDGET_CONFIG.contentStrip.multiStrip,
    widgetHostClass: 'block sm:-mx-10 -mx-6',
  }

  coursePending: NSLearningHistory.ILearningHistoryItem[] = []
  lhContent: ILearningHistoryContent[] = []
  selectedStatusType = 'inprogress'
  selectedTabIndex = 0
  contentTypes = ['learning path', 'course', 'collection', 'resource', 'certification']
  pageSize = 10
  loadingContent = true
  pageNum = ''
  ongoingCertifications: NSLearningHistory.ILearningHistoryItem[] = []
  passedCertifications: NSLearningHistory.ILearningHistoryItem[] = []
  constructor(
    private configSvc: ConfigurationsService,
    // private badgesSvc: BadgesService,
    private profileSvc: ProfileService,
    private learnHstSvc: LearningHistoryService,
    private interestSvc: InterestService,
    private followSvc: BtnFollowService,
    private activatedRoute: ActivatedRoute,
  ) {
    if (this.configSvc.userProfile) {
      this.userName = this.configSvc.userProfile.givenName || ''
      this.userEmail = this.configSvc.userProfile.email || ''
      this.departmentName = this.configSvc.userProfile.departmentName || ''
    }
  }

  ngOnInit() {
    if (this.configSvc.rootOrg === 'Ford' && this.widgetResolverData.widgetData.strips[0].stripConfig) {
      this.widgetResolverData = {
        ...this.widgetResolverData,
        widgetData: {
          strips: (this.widgetResolverData.widgetData.strips || []).map(u => ({
            ...u,
            stripConfig: {
              cardSubType: 'card-description-back',
            },
          })),
        },
      }
    }
    this.badgeApiFetchStatus = 'fetching'
    this.userDataFetchStatus = 'fetching'
    this.nsoArtifactsFetchStatus = 'fetching'
    this.interestFetchStatus = 'fetching'
    this.followFetchStatus = 'fetching'
    this.historyFetchStatus = 'fetching'
    this.apiFetchStatus = 'fetching'
    this.interestSvc.fetchUserInterestsV2().subscribe(
      (data: string[]) => {
        this.interests = data
        this.interestFetchStatus = 'done'
      },
      () => {
        this.interestFetchStatus = 'error'
      },
    )
    // this.badgesSvc.fetchBadges().subscribe(
    //   (data: IBadgeResponse) => {
    //     this.badgesData = data
    //     this.badgesEarned = this.badgesData.earned
    //     this.pointsEarn =
    //       this.badgesData.totalPoints[0].collaborative_points +
    //       this.badgesData.totalPoints[0].learning_points
    //     this.badgeApiFetchStatus = 'done'
    //   },
    //   () => {
    //     this.badgeApiFetchStatus = 'error'
    //   },
    // )
    if (this.enabledTabs.subFeatures.skill) {
      this.profileSvc.getSkillDashBoard().subscribe(
        data => {
          this.skillData = data
          this.skillData = (data || []).map(content => this.convertToCardData(content))
          this.skillFetchStatus = this.skillData.length ? 'done' : 'none'
        },
        () => {
          this.skillFetchStatus = 'error'
        },
      )
    }
    if (this.enabledTabs.subFeatures.knowledgeBoard) {
      this.followSvc.getFollowing().subscribe(
        data => {
          this.followContent = data
          this.followContent.forEach(content => {
            this.knowledgeBoardFollow = content['Knowledge Board']
            this.channelsFollow = content['Channel']
          })
          this.followFetchStatus = 'done'
        },
        () => {
          this.followFetchStatus = 'error'
        },
      )
    }
    if (this.enabledTabs.subFeatures.calendar) {
      this.profileSvc.timeSpent(this.startDate, this.endDate, this.contentType, this.isCompleted).subscribe(
        (timeSpentTrack: NSProfileData.ITimeSpentResponse) => {
          this.timeSpentData = timeSpentTrack
          this.apiFetchStatus = 'done'
          if (this.timeSpentData) {
            this.userPointsEarned = this.timeSpentData.points_and_ranks.user_points_earned
            this.orgWideTimePercent = Math.round(
              this.timeSpentData.timespent_user_vs_org_wide.usage_percent,
            )
            this.orgWidePointsPercent = Math.round(
              this.timeSpentData.points_and_ranks.points_user_vs_org_wide.points_percent,
            )
            this.totalLearningHours = Math.round(this.timeSpentData.time_spent_by_user)

            // this.trackWiseDataFetch(this.learningTimeData.track_wise_user_timespent)
            this.specialDatesSet()
          }
        },
        () => {
          this.apiFetchStatus = 'error'
        })
    }
    if (this.enabledTabs.subFeatures.pendingCourses) {
      this.learnHstSvc.fetchContentProgress(this.pageNum, this.pageSize, this.selectedStatusType, 'course').subscribe(
        (data: NSLearningHistory.ILearningHistory) => {
          this.coursePending = data.result.sort((a: any, b: any) => (a.timeLeft > b.timeLeft ? 1 : a.timeLeft < b.timeLeft ? -1 : 0))
          this.historyFetchStatus = 'done'
        },
        () => {
          this.historyFetchStatus = 'done'
        })
    }
  }

  specialDatesSet() {
    if (this.timeSpentData) {
      const timeSpentDateWise = this.timeSpentData.date_wise.filter(data => data.value !== 0)
      this.specialDates = timeSpentDateWise.map(data => data.key)
    }
  }
  private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): NSProfileData.ICardSkill {
    return {
      category: content.category,
      certificationCount: content.certification_count || 0,
      courseCount: content.course_count || 0,
      horizon: content.horizon,
      group: content.skill_group,
      level: content.skill_level,
      id: `${content.skill_id}`,
      imageUrl: content.image_url,
      navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
      title: content.skill_name,
      recommendedBy: content.recommended_by,
    }
  }
  calendarEvent(event: string) {
    this.timeEvent = new Date(event)
    const clickedDate = this.timeEvent.getTime() + 330 * 60000
    if (this.timeSpentData) {
      this.timeSpentData.date_wise.reverse().find((cur: NSProfileData.IProfileData) => {
        if (clickedDate === cur.key) {
          this.timeSpent = cur.value
          return
        }
      })
    }
  }
}
