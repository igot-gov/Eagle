import { Component, OnInit } from '@angular/core';
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { ActivatedRoute } from '@angular/router'
import {
  IAuthoringPagination
} from '@ws/author/src/lib/interface/authored'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { MyContentService } from '../../../../my-content/services/my-content.service'
import { tap } from 'rxjs/operators'
import { Subscription } from 'rxjs'
import { FormGroup, FormBuilder } from '@angular/forms'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { EditorService } from '@ws/author/src/lib/routing/modules/editor/services/editor.service'
import { CollectionStoreService } from '../../../../editor/routing/modules/collection/services/store.service'
import { NSApiRequest } from '@ws/author/src/lib/interface/apiRequest'
import { CollectionResolverService } from '../../../../editor/routing/modules/collection/services/resolver.service'
import { NsContent } from '@ws-widget/collection/src/lib/_services/widget-content.model'

@Component({
  selector: 'ws-utils-add-thumbnail',
  templateUrl: './add-thumbnail.component.html',
  styleUrls: ['./add-thumbnail.component.scss'],
  providers: [CollectionStoreService, CollectionResolverService, EditorContentService],
})
export class AddThumbnailComponent implements OnInit {
  // toggle: NsContent.IContent[] = []
  toggle: NsContent.IContent | null = null
  currentParentId!: string
  startForm!: FormGroup
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
  IsChecked: boolean
  isEditEnabled = false
  thumbanilSelectval!: string

  constructor(private loadService: LoaderService,
    private myContSvc: MyContentService,
    private accessService: AccessControlService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private contentService: EditorContentService,
    private editorService: EditorService,
    private storeService: CollectionStoreService,
  ) {
    this.userId = this.accessService.userId
    this.IsChecked = false
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
    this.startForm = this.formBuilder.group({
      thumbnail: []
    })
    this.imageList = []

    this.activatedRoute.queryParams.subscribe(params => {
      this.status = params.status
      this.fetchContent(false, this.userId)
    })
  }


  onValChange(val: NsContent.IContent | null = null) {
    console.log('-----------clicking', val)
    this.IsChecked = true
    this.thumbanilSelectval = val ? val.identifier : '';
    this.toggle = val;
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

  public uploadThumbnail() {
    console.log('----------------------------', this.contentService)
    console.log('----------------------------', this.toggle)

    const nodesModified: any = {}
    let isRootPresent = false
    Object.keys(this.contentService.upDatedContent).forEach(v => {
      if (!isRootPresent) {
        isRootPresent = this.storeService.parentNode.includes(v)
      }
      nodesModified[v] = {
        isNew: false,
        root: this.storeService.parentNode.includes(v),
        metadata: this.contentService.upDatedContent[v],
      }
    })
    if (!isRootPresent) {
      nodesModified[this.currentParentId] = {
        isNew: false,
        root: true,
        metadata: {},
      }
    }

    console.log('----------------------------',Object.keys(this.contentService.upDatedContent)[0], nodesModified[Object.keys(this.contentService.upDatedContent)[0]].metadata)
    let requestBody: NSApiRequest.IThumbnailUpdateV3 = {
      request: {
        content: {
          appIcon: this.toggle ? this.toggle.downloadUrl : ''
        }
      }
    }

    return this.editorService.updateThumbnailV3(requestBody, this.toggle ? this.toggle.identifier : '').pipe(
      tap(() => {
        console.log('----------------------')
        this.storeService.changedHierarchy = {}
        Object.keys(this.contentService.upDatedContent).forEach(id => {
          this.contentService.resetOriginalMeta(this.contentService.upDatedContent[id], id)
          this.editorService.readContentV2(id).subscribe(resData => {
            this.contentService.resetVersionKey(resData.versionKey, resData.identifier)
          })
        })
        this.contentService.upDatedContent = {}
      }),
    )

  }

}
