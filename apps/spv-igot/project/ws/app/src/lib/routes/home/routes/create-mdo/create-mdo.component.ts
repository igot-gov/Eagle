import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core'
import { FormGroup, FormControl } from '@angular/forms'
import { MatDialog, MatSnackBar } from '@angular/material'
import { UserPopupComponent } from '../user-popup/user-popup'
import { IMAGE_MAX_SIZE, IMAGE_SUPPORT_TYPES } from './upload'
import { NOTIFICATION_TIME } from '../author-kid/constant'
import { CONTENT_BASE_STATIC } from '../author-kid/apiEndpoints'
import { IFormMeta } from '../author-kid/form'
import { NSContent } from '../author-kid/content'
import { UploadService } from '../../services/upload.service'
import { NotificationComponent } from '@ws-widget/collection/src/lib/notification/notification.component'
import { Notify } from '../author-kid/notificationMessage'
import { EditorContentService } from '../../services/editor-content.service'
import { LoaderService } from '../../services/loader.service'
import { AuthInitService } from '../../services/init.service'
import { ImageCropComponent } from '@ws-widget/utils/src/public-api'
import { ApiService } from '../../services/api.service'
import { AccessControlService } from '../../services/access-control.service'
import { EditorService } from '../../services/editor.service'

@Component({
  selector: 'ws-app-create-mdo',
  providers:[UploadService, ApiService, AccessControlService, EditorContentService, EditorService, AuthInitService, LoaderService],
  templateUrl: './create-mdo.component.html',
  styleUrls: ['./create-mdo.component.scss'],
})
export class CreateMdoComponent implements OnInit {
  @Output() data = new EventEmitter<string>()
  @Input() isSubmitPressed = false
  @Input() nextAction = 'done'
  @Input() stage = 1
  @Input() type = ''
  contentMeta!: NSContent.IContentMeta
  userMgmtData: any = []
  fracData: any = []
  contentForm!: FormGroup
  imageTypes = ['.png', '.jpg', '.jpeg', '.jtif', '.tiff']
  tabledata: any = []
  canUpdate = true
  isEditEnabled = false
  canExpiry = true
  constructor(public dialog: MatDialog,
    private uploadService: UploadService,
    private snackBar: MatSnackBar,
    private contentService: EditorContentService,
    private loader: LoaderService,
    private authInitService: AuthInitService,) { }

  ngOnInit() {

    this.tabledata = {
      columns: [
        { displayName: 'Full name', key: 'fullName' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Mobile number', key: 'position' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    this.contentForm = new FormGroup({
      name: new FormControl(),
      subTitle: new FormControl(),
    })
    this.userMgmtData = [{
      name: 'Create Users',
      key: 'Create Users',
      checked: false,
      enabled: true,
    },
    {
      name: 'Activate Users',
      key: 'Activate Users',
      checked: true,
      enabled: true,
    },
    {
      name: 'Add/Remove Users',
      key: 'Add/Remove Users',
      checked: true,
      enabled: true,
    },
    {
      name: 'Block Users',
      key: 'Block Users',
      checked: false,
      enabled: true,
    },
    {
      name: 'Approve fields',
      key: 'Approve fields',
      checked: true,
      enabled: true,
    }]

    this.fracData = [{
      name: 'Competencies',
      key: 'competencies',
      checked: true,
      enabled: true,
    },
    {
      name: 'Postions',
      key: 'postions',
      checked: false,
      enabled: true,
    },
    {
      name: 'Roles',
      key: 'roles',
      checked: true,
      enabled: true,
    },
    {
      name: 'Knowledge resources',
      key: 'knowledge resources',
      checked: false,
      enabled: true,
    },
    {
      name: 'Question bank',
      key: 'question bank',
      checked: true,
      enabled: true,
    }]
  }
  checkCondition(first: string, seconnd: string) {
    if (first && seconnd) {

    }
    return true
  }
  showError() {

  }
  uploadAppIcon(file: File) {
    const formdata = new FormData()
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (
      !(
        IMAGE_SUPPORT_TYPES.indexOf(
          `.${fileName
            .toLowerCase()
            .split('.')
            .pop()}`,
        ) > -1
      )
    ) {
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
          formdata.append('content', result, fileName)
          this.loader.changeLoad.next(true)
          this.uploadService
            .upload(formdata, {
              contentId: this.contentMeta.identifier,
              contentType: CONTENT_BASE_STATIC,
            })
            .subscribe(
              data => {
                if (data.code) {
                  this.loader.changeLoad.next(false)
                  this.canUpdate = false
                  this.contentForm.controls.appIcon.setValue(data.artifactURL)
                  this.contentForm.controls.thumbnail.setValue(data.artifactURL)
                  // this.contentForm.controls.posterImage.setValue(data.artifactURL)
                  this.canUpdate = true
                  this.storeData()
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
      },
    })
  }

  storeData() {
    try {
      const originalMeta = this.contentService.getOriginalMeta(this.contentMeta.identifier)
      if (originalMeta && this.isEditEnabled) {
        const expiryDate = this.contentForm.value.expiryDate
        const currentMeta: NSContent.IContentMeta = JSON.parse(JSON.stringify(this.contentForm.value))
        if (originalMeta.mimeType) {
          currentMeta.mimeType = originalMeta.mimeType
        }
        const meta = <any>{}
        if (this.canExpiry) {
          currentMeta.expiryDate = `${expiryDate
            .toISOString()
            .replace(/-/g, '')
            .replace(/:/g, '')
            .split('.')[0]
            }+0000`
        }
        Object.keys(currentMeta).map(v => {
          if (
            JSON.stringify(currentMeta[v as keyof NSContent.IContentMeta]) !==
            JSON.stringify(originalMeta[v as keyof NSContent.IContentMeta])
          ) {
            if (
              currentMeta[v as keyof NSContent.IContentMeta] ||
              (this.authInitService.authConfig[v as keyof IFormMeta].type === 'boolean' &&
                currentMeta[v as keyof NSContent.IContentMeta] === false)
            ) {
              meta[v as keyof NSContent.IContentMeta] = currentMeta[v as keyof NSContent.IContentMeta]
            } else {
              meta[v as keyof NSContent.IContentMeta] = JSON.parse(
                JSON.stringify(
                  this.authInitService.authConfig[v as keyof IFormMeta].defaultValue[originalMeta.contentType][0].value,
                ),
              )
            }
          }
        })
        // Quick FIX
        if (this.stage >= 1 && !this.type) {
          delete meta.artifactUrl
        }
        this.contentService.setUpdatedMeta(meta, this.contentMeta.identifier)
      }
    } catch (ex) {
      this.snackBar.open('Please Save Parent first and refresh page.')
    }
  }
  openPopup() {
    const dialogRef = this.dialog.open(UserPopupComponent, {
      maxHeight: 'auto',
      height: '70%',
      width: '80%',
      panelClass: 'remove-pad',
    })
    dialogRef.afterClosed().subscribe((response: any) => {
      if (response === 'postCreated') {
        // this.refreshData(this.currentActivePage)
      }
    })
  }
}
