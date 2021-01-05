import { Component, OnInit } from '@angular/core';
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { ActivatedRoute } from '@angular/router'
import {
  IAuthoringPagination
} from '@ws/author/src/lib/interface/authored'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { MyContentService } from '../../../../my-content/services/my-content.service'
// import { map } from 'rxjs/operators'
import { Subscription } from 'rxjs'


@Component({
  selector: 'ws-utils-add-thumbnail',
  templateUrl: './add-thumbnail.component.html',
  styleUrls: ['./add-thumbnail.component.scss']
})
export class AddThumbnailComponent implements OnInit {

  public status = 'draft'
  userId!: string
  searchLanguage = ''
  queryFilter = ''
  currentFilter = 'myimages'
  public pagination!: IAuthoringPagination
  isAdmin = false
  newDesign = true
  public imageList!: any[]
  public fetchError = false
  showLoadMore!: boolean
  totalContent!: number
  routerSubscription = <Subscription>{}

  constructor(private loadService: LoaderService,
    private myContSvc: MyContentService,
    private accessService: AccessControlService,
    private activatedRoute: ActivatedRoute,

  ) {
    this.userId = this.accessService.userId

  }

  ngOnDestroy() {
    if (this.routerSubscription.unsubscribe) {
      this.routerSubscription.unsubscribe()
    }
    this.loadService.changeLoad.next(false)
  }

  ngOnInit() {
    this.pagination = {
      offset: 0,
      limit: 24,
    }
    // console.log('-------------userid-----------', this.userId)
    this.imageList = []

    this.activatedRoute.queryParams.subscribe(params => {
      this.status = params.status
      this.fetchContent(false, this.userId)
    })
  }

  filter(key: string | 'myimages' | 'all') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'myimages':
          this.fetchContent(false, this.userId)
          break
        case 'all':
          console.log('------------', this.imageList)
          this.fetchContent(false, null)
          break

        default:
          this.imageList = []
          break
      }
    }
  }

  fetchContent(loadMoreFlag: boolean, createdBy: string | null) {

    const requestData = {
      request: {
        filters: {
          compatibilityLevel: { min: 1, max: 2 },
          contentType: ["Asset"],
          createdBy: createdBy,
          mediaType: ["image"],
          status: ["Live", "Review", "Draft"]
        },
        query: this.queryFilter,
        // pageNo: loadMoreFlag ? this.pagination.offset : 0,
        sort_by: { lastUpdatedOn: 'desc' },
        // pageSize: this.pagination.limit

      },
    }


    this.loadService.changeLoad.next(true)
    const observable =
      this.myContSvc.fetchContent(requestData)
    this.loadService.changeLoad.next(true)
    observable.subscribe(
      data => {
        this.loadService.changeLoad.next(false)

        this.imageList =
          loadMoreFlag && !this.queryFilter
            ? (this.imageList || []).concat(
              data && data.result && data.result.content ? data.result.content : [],
            )
            : data && data.result.content
              ? data.result.content
              : []
        this.totalContent = data && data.result.response ? data.result.response.totalHits : 0
        // this.showLoadMore =
        //   this.pagination.offset * this.pagination.limit + this.pagination.limit < this.totalContent
        //     ? true
        //     : false
        this.fetchError = false
      },
      () => {
        this.fetchError = true
        this.imageList = []
        this.showLoadMore = false
        this.loadService.changeLoad.next(false)
      },
    )
  }

}
