import { HttpEventType } from '@angular/common/http'
import { Component, EventEmitter, Input, OnInit, Output, OnChanges } from '@angular/core'
import { MatDialog, MatSnackBar } from '@angular/material'
import { CONTENT_BASE_STATIC, CONTENT_BASE_STREAM } from '@ws/author/src/lib/constants/apiEndpoints'
import { NOTIFICATION_TIME } from '@ws/author/src/lib/constants/constant'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { VIDEO_MAX_SIZE } from '@ws/author/src/lib/constants/upload'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { UploadService } from '@ws/author/src/lib/routing/modules/editor/shared/services/upload.service'
import { of } from 'rxjs'
import { map, mergeMap, tap } from 'rxjs/operators'
import { AccessControlService } from './../../../../../../../../modules/shared/services/access-control.service'
import { EditorService } from './../../../../../services/editor.service'
import { NSApiRequest } from '../../../../../../../../interface/apiRequest'

@Component({
  selector: 'ws-auth-upload-kartifact',
  templateUrl: './upload-kartifact.component.html',
  styleUrls: ['./upload-kartifact.component.scss'],
})
export class UploadKartifactComponent implements OnInit, OnChanges {
  @Output() data = new EventEmitter<NSContent.IContentMeta | string>()
  @Input() currentContent = ''
  file!: File
  isChecked = false
  mimeType = ''
  duration = 0
  maxSize = VIDEO_MAX_SIZE / (1024 * 1024)
  @Input() content: NSContent.IContentMeta = {} as any
  enableUpload = true
  percentage = 0
  isPathFinders = false
  isFord = false
  fileName = ''
  inputType = 'file'
  artifactUrl = ''
  isInIntranet = false
  isIframeSupported: 'Yes' | 'No' | 'MayBe' = 'No'

  constructor(
    private snackBar: MatSnackBar,
    private uploadService: UploadService,
    private matDialog: MatDialog,
    private accessService: AccessControlService,
    private editorService: EditorService,
  ) {}

  ngOnChanges() {
    if (this.content.artifactUrl) {
      this.isChecked = true
      if (this.content.artifactUrl.indexOf('content-store') > 0) {
        this.fileName = this.content.artifactUrl.split('/').pop() as string
        this.percentage = 100
      } else {
        this.inputType = 'url'
        this.artifactUrl = this.content.artifactUrl || ''
        this.isInIntranet = this.content.isInIntranet || false
        this.isIframeSupported = this.content.isIframeSupported || 'No'
      }
    }
  }
  ngOnInit() {
    this.isPathFinders = this.accessService.rootOrg === 'Pathfinders'
    this.isFord = this.accessService.rootOrg === 'Ford'
  }

  onDrop(file: File) {
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (
      !fileName.toLowerCase().endsWith('.pdf') &&
      !fileName.toLowerCase().endsWith('.mp4') &&
      !fileName.toLowerCase().endsWith('.mp3')
    ) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.INVALID_FORMAT,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      // } else if (
      //   fileName.toLowerCase().endsWith('.mp3')
      // ) {
      //   this.snackBar.openFromComponent(NotificationComponent, {
      //     data: {
      //       type: Notify.NOT_READY,
      //     },
      //     duration: NOTIFICATION_TIME * 1000,
      //   })
    } else if (file.size > VIDEO_MAX_SIZE) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.SIZE_ERROR,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
    } else {
      this.file = file
      this.mimeType = fileName.toLowerCase().endsWith('.pdf')
        ? 'application/pdf'
        : fileName.toLowerCase().endsWith('.mp4')
        ? 'application/x-mpegURL'
        : 'audio/mpeg'
      if (this.mimeType === 'application/x-mpegURL' || this.mimeType === 'audio/mpeg') {
        this.getDuration()
      } else {
        this.duration = 3600
        this.upload()
      }
    }
  }

  upload() {
    const formdata = new FormData()
    formdata.append(
      'content',
      this.file as Blob,
      (this.file as File).name.replace(/[^A-Za-z0-9.]/g, ''),
    )
    this.enableUpload = false
    this.uploadService
      .upload(
        formdata,
        {
          contentId: this.currentContent,
          contentType:
            this.mimeType === 'application/pdf' ? CONTENT_BASE_STATIC : CONTENT_BASE_STREAM,
        },
        { reportProgress: true, observe: 'events' },
      )
      .pipe(
        map((event: any) => {
          switch (event.type) {
            case HttpEventType.UploadProgress:
              this.percentage = Math.round((100 * event.loaded) / event.total)
              return { status: 'progress', message: this.percentage }

            case HttpEventType.Response:
              return event.body
            default:
              return `Unhandled event: ${event.type}`
          }
        }),
        tap(v => {
          if (v.code) {
            const url = (v.authArtifactURL || v.artifactURL).replace(/%2F/g, '/')
            this.content.artifactUrl = url
            this.content.downloadUrl = v.downloadURL
            this.content.transcoding = {
              lastTranscodedOn: null,
              retryCount: 0,
              status: 'STARTED',
            }
            this.content.mimeType = this.mimeType
            this.content.duration = this.duration
            this.content.size = this.file.size
          }
        }),
        mergeMap(v => {
          if (this.mimeType === 'application/x-mpegURL' && v.code) {
            return this.uploadService
              .startEncoding(v.authArtifactURL || v.artifactURL, this.currentContent)
              .pipe(map(() => v))
          }
          return of(v)
        }),
        mergeMap(v => {
          if (v.code) {
            const requestBody: NSApiRequest.IContentUpdate = {
              hierarchy: {},
              nodesModified: {
                [this.currentContent]: {
                  isNew: false,
                  root: true,
                  metadata: {
                    artifactUrl: this.content.artifactUrl,
                    downloadUrl: this.content.downloadUrl,
                    transcoding: {
                      lastTranscodedOn: null,
                      retryCount: 0,
                      status: 'STARTED',
                    },
                    mimeType: this.content.mimeType,
                    duration: this.content.duration,
                    size: this.content.size,
                    isExternal: false,
                    isInIntranet: false,
                    isIframeSupported: 'Yes',
                  } as any,
                },
              },
            }
            if (this.isFord) {
              const type =
                this.mimeType === 'application/pdf'
                  ? 'PDF'
                  : this.mimeType === 'application/x-mpegURL'
                  ? 'Video'
                  : 'Podcast/Audio'
              requestBody.nodesModified[this.currentContent].metadata.categoryType = type
              requestBody.nodesModified[this.currentContent].metadata.resourceType = type
            }
            return this.editorService.updateContent(requestBody).pipe(map(_ => v))
          }
          return of(v)
        }),
      )
      .subscribe(
        v => {
          if (v.code) {
            this.enableUpload = true
            this.snackBar.openFromComponent(NotificationComponent, {
              data: {
                type: Notify.UPLOAD_SUCCESS,
              },
              duration: NOTIFICATION_TIME * 1000,
            })
          }
        },
        () => {
          this.enableUpload = true
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: Notify.UPLOAD_FAIL,
            },
            duration: NOTIFICATION_TIME * 1000,
          })
        },
      )
  }

  storeData() {
    if (!this.content.artifactUrl && !this.artifactUrl && this.inputType === 'file') {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.UPLOAD_FILE,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
    } else if (!this.isChecked) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.IPR_DECLARATION_PF,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
    } else {
      if (this.inputType === 'url') {
        this.data.emit({
          artifactUrl: this.artifactUrl,
          isInIntranet: this.isInIntranet,
          isIframeSupported: this.isIframeSupported,
          mimeType: 'application/html',
          isExternal: true,
          categoryType: 'Web Page',
          resourceType: 'Web Page',
        } as any)
      } else {
        this.data.emit({} as any)
      }
    }
  }

  getDuration() {
    const content = document.createElement(
      this.mimeType === 'application/x-mpegURL' ? 'video' : 'audio',
    )
    content.preload = 'metadata'
    this.enableUpload = false
    content.onloadedmetadata = () => {
      window.URL.revokeObjectURL(content.src)
      this.duration = Math.round(content.duration)
      this.enableUpload = true
      this.upload()
    }
    content.onerror = () => {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.UPLOAD_FAIL,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
    }
    content.src = URL.createObjectURL(this.file)
  }

  back() {
    this.data.emit('back')
  }

  showIpr(template: any) {
    this.matDialog.open(template, {
      width: 'auto',
    })
  }

  closeIpr() {
    this.matDialog.closeAll()
  }
}
