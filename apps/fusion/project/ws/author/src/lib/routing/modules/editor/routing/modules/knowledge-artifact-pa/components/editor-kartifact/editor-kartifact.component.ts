import { HttpEventType } from '@angular/common/http'
import { Component, EventEmitter, Input, OnInit, Output, OnChanges } from '@angular/core'
import { MatDialog } from '@angular/material/dialog'
import { MatSnackBar } from '@angular/material/snack-bar'
import { NOTIFICATION_TIME } from '@ws/author/src/lib/constants/constant'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { AuthInitService } from '@ws/author/src/lib/services/init.service'
import { map } from 'rxjs/operators'
import { ImageCropComponent } from '../../../../../../../../../../../../../library/ws-widget/utils/src/public-api'
import { CONTENT_BASE_STATIC } from '../../../../../../../../constants/apiEndpoints'
import { IMAGE_MAX_SIZE, IMAGE_SUPPORT_TYPES } from '../../../../../../../../constants/upload'
import { NSContent } from '../../../../../../../../interface/content'
import { UploadService } from './../../../../../shared/services/upload.service'

@Component({
  selector: 'ws-auth-editor-kartifact',
  templateUrl: './editor-kartifact.component.html',
  styleUrls: ['./editor-kartifact.component.scss'],
})
export class EditorKartifactComponent implements OnInit, OnChanges {
  @Input() content!: NSContent.IContentMeta
  @Input() currentContent!: string
  @Output() data = new EventEmitter<string>()
  canSubmit = true
  ordinals: any
  percentage = 0
  isSubmitted = false
  imageSupportedTypes = IMAGE_SUPPORT_TYPES
  maxSize = IMAGE_MAX_SIZE / (1024 * 1024)
  file!: File | null
  complexityLevelList: string[] = []
  isPathFinders = false
  isFord = false
  fileName = ''

  constructor(
    private authInitService: AuthInitService,
    private snackBar: MatSnackBar,
    private uploadService: UploadService,
    private dialog: MatDialog,
    private accessService: AccessControlService,
  ) {}

  ngOnInit() {
    this.isPathFinders = this.accessService.rootOrg === 'Pathfinders'
    this.isFord = this.accessService.rootOrg === 'Ford'
    this.ordinals = this.authInitService.ordinals
    this.complexityLevelList = this.authInitService.ordinals.complexityLevel
    this.filterOrdinals()
  }

  ngOnChanges() {
    if (this.content.appIcon) {
      this.percentage = 100
      this.fileName = this.content.appIcon.split('/').pop() as string
    }
  }

  filterOrdinals() {
    this.complexityLevelList = []
    this.ordinals.complexityLevel.map((v: any) => {
      if (v.condition) {
        let canAdd = false
        // tslint:disable-next-line: semicolon  tslint:disable-next-line: whitespace
        ;(v.condition.showFor || []).map((con: any) => {
          let innerCondition = false
          Object.keys(con).map(meta => {
            if (con[meta].indexOf(this.content[meta as keyof NSContent.IContentMeta]) > -1) {
              innerCondition = true
            }
          })
          if (innerCondition) {
            canAdd = true
          }
        })
        if (canAdd) {
          // tslint:disable-next-line: whitespace tslint:disable-next-line: semicolon
          ;(v.condition.nowShowFor || []).map((con: any) => {
            let innerCondition = false
            Object.keys(con).map(meta => {
              if (con[meta].indexOf(this.content[meta as keyof NSContent.IContentMeta]) < 0) {
                innerCondition = true
              }
            })
            if (innerCondition) {
              canAdd = false
            }
          })
        }
        if (canAdd) {
          this.complexityLevelList.push(v.value)
        }
      } else {
        if (typeof v === 'string') {
          this.complexityLevelList.push(v)
        } else {
          this.complexityLevelList.push(v.value)
        }
      }
    })
  }

  submit() {
    this.isSubmitted = true
    if (
      !this.content.name ||
      !this.content.description ||
      !this.content.categoryType ||
      !this.content.complexityLevel ||
      !this.content.appIcon
    ) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.MANDATORY_FIELD_ERROR,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
    } else {
      this.data.emit('next')
    }
  }

  back() {
    this.data.emit('back')
  }

  uploadAppIcon(file: File) {
    this.file = file
    const formdata = new FormData()
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (!(IMAGE_SUPPORT_TYPES.indexOf(`.${fileName.toLowerCase().split('.').pop()}`) > -1)) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.INVALID_FORMAT,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    if (file.size > IMAGE_MAX_SIZE) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.SIZE_ERROR,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    const dialogRef = this.dialog.open(ImageCropComponent, {
      width: '70%',
      data: {
        isRoundCrop: false,
        imageFile: file,
        width: 265,
        height: 150,
        isThumbnail: true,
        imageFileName: fileName,
      },
    })

    dialogRef.afterClosed().subscribe({
      next: (result: File) => {
        if (result) {
          this.canSubmit = false
          formdata.append('content', result, fileName)
          this.uploadService
            .upload(
              formdata,
              { contentId: this.currentContent, contentType: CONTENT_BASE_STATIC },
              {
                reportProgress: true,
                observe: 'events',
              },
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
            )
            .subscribe(
              data => {
                if (data.code) {
                  this.canSubmit = true
                  this.content.appIcon = data.artifactURL
                  this.content.thumbnail = data.artifactURL
                  this.content.posterImage = data.artifactURL
                  this.snackBar.openFromComponent(NotificationComponent, {
                    data: {
                      type: Notify.UPLOAD_SUCCESS,
                    },
                    duration: NOTIFICATION_TIME * 1000,
                  })
                }
              },
              () => {
                this.canSubmit = true
                this.snackBar.openFromComponent(NotificationComponent, {
                  data: {
                    type: Notify.UPLOAD_FAIL,
                  },
                  duration: NOTIFICATION_TIME * 1000,
                })
              },
            )
        } else {
          this.file = null
        }
      },
    })
  }
}
