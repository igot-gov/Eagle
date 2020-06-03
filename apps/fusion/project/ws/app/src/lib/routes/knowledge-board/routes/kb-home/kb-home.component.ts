import { Component, OnInit } from '@angular/core'
import { ConfigurationsService, TFetchStatus } from '@ws-widget/utils'
import { WidgetContentService, NsContent, BtnFollowService } from '@ws-widget/collection'
import { BtnKbService } from '../../../../../../../../../library/ws-widget/collection/src/lib/btn-kb/btn-kb.service'
import { NSProfileData } from '../../../profile/models/profile.model'

@Component({
  selector: 'ws-app-kb-home',
  templateUrl: './kb-home.component.html',
  styleUrls: ['./kb-home.component.scss'],
})
export class KbHomeComponent implements OnInit {
  knowledgeBoards: NsContent.IContentMinimal[] = []
  kbSFetchStatus: TFetchStatus = 'none'
  followContent: NSProfileData.IFollowing[] = []
  following: NsContent.IContentMinimal[] = []
  followingFetchStatus: TFetchStatus = 'none'
  myBoards: NsContent.IContentMinimal[] = []
  myBoardsFetchStatus: TFetchStatus = 'none'
  showCreate = false
  redirectUrl = {
    path: '/app/search/learning',
    qParams: {
      q: 'all',
      f: JSON.stringify({
        contentType: [NsContent.EContentTypes.KNOWLEDGE_BOARD],
      }),
    },
  }
  myKBRedirectUrl = {
    path: '/app/search/learning',
    qParams: {
      q: 'all',
      f: JSON.stringify({
        contentType: [NsContent.EContentTypes.KNOWLEDGE_BOARD],
        creatorContacts: [(this.configSvc.userProfile && this.configSvc.userProfile.userId) || ''],
      }),
    },
  }
  constructor(
    private kbSvc: BtnKbService,
    private contentSvc: WidgetContentService,
    public configSvc: ConfigurationsService,
    private followSvc: BtnFollowService,
  ) { }

  ngOnInit() {
    this.showCreate = Array.from(this.configSvc.userRoles || new Set<string>()).some(role => {
      return ['kb-creator', 'editor', 'admin'].includes(role)
    })
    this.kbSFetchStatus = 'fetching'
    this.myBoardsFetchStatus = 'fetching'

    this.contentSvc
      .search({
        filters: {
          contentType: [NsContent.EContentTypes.KNOWLEDGE_BOARD],
        },
        sort: [{ lastUpdatedOn: 'desc' }],
        pageSize: 30,
        // sortBy: 'lastUpdatedOn',
      })
      .subscribe(
        async response => {
          this.kbSFetchStatus = 'done'
          this.knowledgeBoards = response.result
          await this.processContentRating(this.knowledgeBoards)
        },
        _ => {
          this.kbSFetchStatus = 'error'
        })
    this.kbSvc.getMyKnowledgeBoards().subscribe(
      async response => {
        this.myBoards = response.result
        await this.processContentRating(this.myBoards)
        if (response.totalHits > response.result.length) {
          this.myBoardsFetchStatus = 'hasMore'
        } else {
          this.myBoardsFetchStatus = 'done'
        }
      },
      _ => {
        this.myBoardsFetchStatus = 'error'
      })
    this.followed()
  }

  followed() {
    this.followingFetchStatus = 'fetching'
    this.followSvc.getFollowing().subscribe(
      async data => {
        this.followContent = data

        this.followContent.find(content => {
          this.following = content['Knowledge Board']
        })
        await this.processContentRating(this.following)
        this.followingFetchStatus = 'done'
      },
      () => {
        this.followingFetchStatus = 'error'
      },
    )
  }

  unfollowed(id: string) {
    this.following = this.following.filter(item => item.identifier !== id)
  }

  processContentRating(results: NsContent.IContentMinimal[]) {
    const contentId = {
      contentIds:
        results.map(result => result.identifier) || [],
    }
    return this.contentSvc
      .fetchContentRatings(contentId).then(ratingHash => {
        const likes: any = ratingHash
        results.forEach(result => {
          result.totalRating = (likes[result.identifier] && likes[result.identifier].averageRating) || 0
        })
      })
  }
}
