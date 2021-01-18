import { Component, OnInit, Input, Inject, Output, EventEmitter, OnDestroy } from '@angular/core'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import {
  IAuthoringPagination,
} from '@ws/author/src/lib/interface/authored'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { MyContentService } from '../../../../my-content/services/my-content.service'
import { Subscription } from 'rxjs'
import { FormGroup, FormBuilder } from '@angular/forms'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { CollectionStoreService } from '../../../../editor/routing/modules/collection/services/store.service'
import { CollectionResolverService } from '../../../../editor/routing/modules/collection/services/resolver.service'
import { NsContent } from '@ws-widget/collection/src/lib/_services/widget-content.model'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material'
@Component({
  selector: 'ws-auth-add-thumbnail',
  templateUrl: './add-thumbnail.component.html',
  styleUrls: ['./add-thumbnail.component.scss'],
  providers: [CollectionStoreService, CollectionResolverService, EditorContentService],
})
export class AddThumbnailComponent implements OnInit, OnDestroy {
  // toggle: NsContent.IContent[] = []
  contentMeta!: NSContent.IContentMeta
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
  public imageList!: []
  public fetchError = false
  showLoadMore!: boolean
  totalContent!: number
  routerSubscription = <Subscription>{}
  isChecked: boolean
  isEditEnabled = false
  thumbanilSelectval!: string
  @Input() stage = 1
  @Input() type = ''
  @Output() addAppIcon = new EventEmitter<string>()

  canUpdate = true
  @Input() isUpdate = false
  showMainContent: Boolean = true
  srcResult: any
  public imagePath: any
  imgURL: any
  public message: string | undefined

  constructor(private loadService: LoaderService,
              public dialogRef: MatDialogRef<AddThumbnailComponent>,
              private myContSvc: MyContentService,
              private accessService: AccessControlService,
              private formBuilder: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.userId = this.accessService.userId
    this.isChecked = false
  }

  ngOnInit() {
    this.pagination = {
      offset: 0,
      limit: 24,
    }
    this.startForm = this.formBuilder.group({
      thumbnail: [],
    })
    this.filter('myimages')
    this.imageList = []

  }

  ngOnDestroy() {

    this.loadService.changeLoad.next(false)
  }

  onFileSelected(files: any) {
    if (files.length === 0) {
      return
    }

    const mimeType = files[0].type
    if (mimeType.match(/image\/*/) == null) {
      this.message = 'Only images are supported.'
      return
    }

    const reader = new FileReader()
    this.imagePath = files[0]
    this.isChecked = true
    reader.readAsDataURL(files[0])
    reader.onload = _event => {
      this.imgURL = reader.result
    }
  }

  showHideButton() {
    this.showMainContent = this.showMainContent ? false : true
  }

  onValChange(val: NsContent.IContent | null = null) {
    this.isChecked = true
    this.thumbanilSelectval = val ? val.identifier : ''
    this.toggle = val
  }

  filter(key: string | 'myimages' | 'all') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'myimages':
          this.fetchContent(false, this.userId)
          break
        case 'all':
          this.fetchContent(false, null)
          break

        default:
          this.fetchContent(false, this.userId)
          break
      }
    }
  }

  fetchContent(loadMoreFlag: boolean, createdBy: string | null) {

    const requestData = {
      request: {
        filters: {
          createdBy,
          compatibilityLevel: { min: 1, max: 2 },
          contentType: ['Asset'],
          mediaType: ['image'],
          status: ['Live', 'Review', 'Draft'],
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
    this.dialogRef.close({ appURL: this.toggle ? this.toggle.artifactUrl : '' })
  }

  public uploadSelectedThumbnail() {
    this.dialogRef.close({ file: this.imagePath })
  }

}
