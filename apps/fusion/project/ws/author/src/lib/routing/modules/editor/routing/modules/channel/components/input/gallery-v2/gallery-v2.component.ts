import { Component, EventEmitter, Input, OnChanges, Output, ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core'
import { MatSnackBar } from '@angular/material'
import { NsGalleryView, WidgetContentService, NsContent } from '@ws-widget/collection/src/public-api'
import { NsWidgetResolver } from '@ws-widget/resolver'
import {
  AUTHORING_CONTENT_BASE,
  CONTENT_BASE_WEBHOST_ASSETS,
} from '@ws/author/src/lib/constants/apiEndpoints'
import { NOTIFICATION_TIME } from '@ws/author/src/lib/constants/constant'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { UploadService } from '@ws/author/src/lib/routing/modules/editor/shared/services/upload.service'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { WIDGET_LIBRARY } from '../../../constants/widet'
import { FILE_MAX_SIZE } from './../../../../../../../../../constants/upload'
import { ISortEvent } from '../../../../../../../../../modules/shared/directives/draggable/sortable-list.directive'
import { IDrag } from '../../v2/drag-n-sort/drag-n-sort.model'
import { DomSanitizer } from '@angular/platform-browser'

interface IApiData {
  originalIndex: number,
  identifier: any
}
@Component({
  selector: 'ws-auth-gallery-v2',
  templateUrl: './gallery-v2.component.html',
  styleUrls: ['./gallery-v2.component.scss'],
})
export class GalleryV2Component implements OnChanges, OnDestroy, OnInit {
  @Output() data = new EventEmitter<{
    content: NsGalleryView.IWidgetGalleryView
    isValid: boolean
  }>()
  @Output() change = new EventEmitter<{
    sort: boolean,
    data: ISortEvent
  }>()
  @Output() addInGallery = new EventEmitter<string>()
  @Output() updateScreens = new EventEmitter<{
    index: number
    fromGallery: boolean
  }>()
  @Input() isDesktop = false
  @Input() content!: NsGalleryView.IWidgetGalleryView
  @Input() identifier = ''
  @Input() isSubmitPressed = false
  @Input() size = 1
  @Input() isResponsive = false
  index = 0
  isCommon = true
  currentStrip!: NsGalleryView.ICardMenu
  seriesAtEnd = true
  previewData: IDrag[] = []
  checked = false
  dataAvailable = false
  constructor(
    private uploadService: UploadService,
    private loader: LoaderService,
    private snackBar: MatSnackBar,
    private domSanitizer: DomSanitizer,
    private contentSvc: WidgetContentService,
    private changeDetector: ChangeDetectorRef
  ) { }
  ngOnInit(): void {
    this.previewData.push({ imageSrc: 'blank' })

  }

  ngOnChanges() {
    this.currentStrip = this.content.cardMenu[this.index]
    if (this.content.type === 'image' && !this.content.designVal) {
      this.content.designVal = 'set2'
    }
    // if (this.content.type === 'video') {
    //   this.updatePreviewData()
    // }
  }

  onIndexChange(index: number) {
    this.index = index
    this.currentStrip = this.content.cardMenu[index]
    this.changeDetector.detectChanges()
  }

  ngOnDestroy(): void {
    this.changeDetector.detach()
  }

  removeStrip() {
    this.content.cardMenu.splice(this.index, 1)
    if (this.index >= this.content.cardMenu.length && this.index === 0) {
      this.addEnd(false)
    } else if (this.index >= this.content.cardMenu.length) {
      this.index = this.index - 1
      this.onIndexChange(this.index)
    } else {
      this.onIndexChange(this.index)
    }
  }

  addfront() {
    const strip = this.addStrip()
    this.content.cardMenu.unshift(strip)
    this.currentStrip = strip
    this.index = 0
    this.onIndexChange(this.index)
    this.addInGallery.emit('front')
    this.previewData.push({ imageSrc: 'blank' })
  }

  addEnd(increaseIndex = true) {
    const strip = this.addStrip()
    this.content.cardMenu.push(strip)
    this.currentStrip = strip
    this.index = increaseIndex ? this.content.cardMenu.length - 1 : this.index
    this.onIndexChange(this.index)
    this.addInGallery.emit('end')
    this.previewData.push({ imageSrc: 'blank' })
  }

  addStrip(): NsGalleryView.ICardMenu {
    const strip: NsGalleryView.ICardMenu = {
      cardData: {
        title: '',
        description: '',
        thumbnail: '',
      },
      widget: this.generateWidget(),
    }
    return strip
  }

  metaUpdate(key: 'title' | 'description' | 'thumbnail', value: string) {
    if (this.currentStrip.cardData) {
      this.currentStrip.cardData[key] = value
    } else {
      this.currentStrip.cardData = {
        title: '',
        description: '',
        thumbnail: '',
        [key]: value,
      }
    }
  }

  upload(file: File) {
    const formdata = new FormData()
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (!(file.type.indexOf('image/') > -1)) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.INVALID_FORMAT,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    if (file.type.indexOf('image/') > -1 && file.size > FILE_MAX_SIZE) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.SIZE_ERROR,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    formdata.append('content', file, fileName)
    this.loader.changeLoad.next(true)
    this.uploadService
      .upload(formdata, { contentId: this.identifier, contentType: CONTENT_BASE_WEBHOST_ASSETS })
      .subscribe(data => {
        if (data.code) {
          this.loader.changeLoad.next(false)
          this.metaUpdate(
            'thumbnail',
            `${AUTHORING_CONTENT_BASE}${encodeURIComponent(
              `/${data.artifactURL.split('/').slice(3).join('/')}`,
            )}`,
          )
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: Notify.UPLOAD_SUCCESS,
            },
            duration: NOTIFICATION_TIME * 1000,
          })
        }
      },
                 () => {
          this.loader.changeLoad.next(false)
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: Notify.UPLOAD_FAIL,
            },
            duration: NOTIFICATION_TIME * 1000,
          })
        },
      )
  }

  generateWidget(): NsWidgetResolver.IRenderConfigWithAnyData {
    if (this.content.type === '' || !this.content.type) {
      return this.getGalleryType()
    }
    if (this.content.type === 'video') {
      return this.getEmptyData('wrapper')
    }
    if (this.content.type === 'audio') {
      return this.getEmptyData('audio')
    }
    if (this.content.type === 'iframe') {
      return this.getEmptyData('iframe')
    }
    const widget = this.getEmptyData('image')
    widget.widgetData.type = this.content.subType
    return widget
  }

  getGalleryType(): NsWidgetResolver.IRenderConfigWithAnyData {
    if (this.content.cardMenu[0].widget.widgetType === 'wrapper') {
      this.content.type = 'video'
      return this.getEmptyData('wrapper')
    }
    return this.getEmptyData('image')
  }

  getEmptyData(type: string): any {
    const data = JSON.parse(
      JSON.stringify(WIDGET_LIBRARY[`solo_${type}` as keyof typeof WIDGET_LIBRARY]),
    )
    return data
  }

  updateWrapper(event: { content: any; isValid: boolean }) {
    if (event.isValid) {
      if (this.content.type === 'video') {
        this.currentStrip.widget.widgetData = event.content
        this.updateScreens.emit({
          index: this.index,
          fromGallery: true,
        })
      }
      this.updatePreviewData()
    }
  }

  updatePreviewData() {
    this.previewData = []
    if (this.content.type === 'video') {
      this.content.cardMenu.map((strip: NsGalleryView.ICardMenu) => {
        if (strip.widget.widgetData.externalData && strip.widget.widgetData.externalData.iframeSrc) {
          this.previewData.push(
            { iframeSrc: this.domSanitizer.bypassSecurityTrustResourceUrl(strip.widget.widgetData.externalData.iframeSrc) }
          )
        } else if (strip.widget.widgetData.videoData && strip.widget.widgetData.videoData.identifier) {
          this.previewData.push({ ids: strip.widget.widgetData.videoData.identifier })
        } else if (strip.widget.widgetData.videoData && strip.widget.widgetData.videoData.url) {
          if (strip.widget.widgetData.videoData.posterImage) {
            this.previewData.push({ imageSrc: strip.widget.widgetData.videoData.posterImage })
          } else {
            this.previewData.push({ imageSrc: 'blank' })
          }
        } else {
          this.previewData.push({ imageSrc: 'blank' })
        }
      })
    } else {
      this.content.cardMenu.map((strip: NsGalleryView.ICardMenu) => {
        if (strip.widget.widgetData.templateData && strip.widget.widgetData.templateData.imageSrc) {
          this.previewData.push({ imageSrc: strip.widget.widgetData.templateData.imageSrc })
        } else {
          this.previewData.push({ imageSrc: 'blank' })
        }
      })

    }
  }

  sort(event: { sort: boolean, data: ISortEvent }) {
    const current = this.content.cardMenu[event.data.currentIndex]
    const swapWith = this.content.cardMenu[event.data.newIndex]
    this.content.cardMenu[event.data.newIndex] = current
    this.content.cardMenu[event.data.currentIndex] = swapWith
    // this.onIndexChange(event.data.newIndex)
    this.change.emit({
      sort: true,
      data: event.data,
    })
  }

  toggleChange() {
    this.checked = !this.checked
    if (!this.checked) {
      this.onIndexChange(0)
    } else {
      this.updatePreviewData()
      this.fetchThumbnail()
    }
  }

  fetchThumbnail() {
    const tempArray: IApiData[] = []
    const ids: string[] = []
    this.content.cardMenu.map((strip: NsGalleryView.ICardMenu, index: number) => {
      if (strip.widget.widgetData.videoData && strip.widget.widgetData.videoData.identifier) {
        tempArray.push({ originalIndex: index, identifier: strip.widget.widgetData.videoData.identifier })
        ids.push(strip.widget.widgetData.videoData.identifier)
      }
    })
    if (ids.length > 0) {
      this.dataAvailable = false
      this.contentSvc.fetchMultipleContent(ids).subscribe(
        results => {
          if (results) {
            results.map((v: Partial<NsContent.IContent>, index: number) => {
              this.previewData[tempArray[index].originalIndex] = { imageSrc: v.appIcon }
              delete this.previewData[tempArray[index].originalIndex].ids

            })
          }
          this.dataAvailable = true
        },
        () => {
          tempArray.map((_, index: number) => {
            this.previewData[tempArray[index].originalIndex] = { imageSrc: 'blank' }
            delete this.previewData[tempArray[index].originalIndex].ids
          })
        },
      )
    } else {
      this.dataAvailable = true
    }
  }
}
