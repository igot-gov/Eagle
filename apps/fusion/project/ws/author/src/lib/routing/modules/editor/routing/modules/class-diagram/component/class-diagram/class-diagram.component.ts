import { DeleteDialogComponent } from '@ws/author/src/lib/modules/shared/components/delete-dialog/delete-dialog.component'
import { Component, OnInit, OnDestroy } from '@angular/core'
import { MatSnackBar } from '@angular/material/snack-bar'
import { MatDialog } from '@angular/material'
import { ActivatedRoute, Router } from '@angular/router'
import { mergeMap, catchError } from 'rxjs/operators'
import { of, Observable, Subscription } from 'rxjs'

import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'

import { ClassDiagramStoreService } from '../../service/store.service'
import { ClassDiagram } from '../class-diagram.class'

import { NOTIFICATION_TIME } from '../../constants/class-diagram.constants'
import { ClassRelationEditorComponent } from '../class-relation-editor/class-relation-editor.component'

import { CommentsDialogComponent } from '@ws/author/src/lib/modules/shared/components/comments-dialog/comments-dialog.component'
import { ConfirmDialogComponent } from '@ws/author/src/lib/modules/shared/components/confirm-dialog/confirm-dialog.component'
import { ErrorParserComponent } from '@ws/author/src/lib/modules/shared/components/error-parser/error-parser.component'

import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { UploadService } from '@ws/author/src/lib/routing/modules/editor/shared/services/upload.service'
import { EditorService } from '@ws/author/src/lib/routing/modules/editor/services/editor.service'
import { AuthInitService } from '@ws/author/src/lib/services/init.service'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'

import { NSContent } from '@ws/author/src/lib/interface/content'
import { NSApiRequest } from '@ws/author/src/lib/interface/apiRequest'

// import { CONTENT_BASE_WEBHOST } from '@ws/author/src/lib/constants/apiEndpoints'
import { VIEWER_ROUTE_FROM_MIME } from '@ws-widget/collection/src/public-api'
import { FormGroup } from '@angular/forms'
import { NotificationService } from '@ws/author/src/lib/services/notification.service'

@Component({
  selector: 'ws-auth-class-diagram',
  templateUrl: './class-diagram.component.html',
  styleUrls: ['./class-diagram.component.scss'],
  providers: [ClassDiagramStoreService],
})

export class ClassDiagramComponent implements OnInit, OnDestroy {

  problemStatement = ''
  classDiagram!: ClassDiagram
  currentId = ''
  allContents: NSContent.IContentMeta[] = []
  allLanguages = []
  showDeleteForCard = ''
  currentStep = 2
  contentSubscription?: Subscription
  previewMode = false
  mimeTypeRoute: any
  submitPressed = false
  showSettingButtons = true

  constructor(
    private classDiagramStore: ClassDiagramStoreService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private contentService: EditorContentService,
    private loaderService: LoaderService,
    private uploadService: UploadService,
    private editorService: EditorService,
    private authInitService: AuthInitService,
    private accessService: AccessControlService,
    private notificationSvc: NotificationService,
  ) { }

  ngOnDestroy() {
    if (this.contentSubscription) {
      this.contentSubscription.unsubscribe()
    }
  }

  ngOnInit() {
    this.showSettingButtons = this.accessService.rootOrg === 'Siemens'
    if (this.activatedRoute.parent && this.activatedRoute.parent.parent) {
      this.activatedRoute.parent.parent.data.subscribe(v => {
        if (v.contents && v.contents.length) {
          this.allContents.push(v.contents[0].content)
          this.classDiagramStore.collectiveData[v.contents[0].content.identifier] = v.contents[0].data
            ? v.contents[0].data
            : new ClassDiagram()
          // this.classDiagramStore.collectiveData[v.contents[0].content.identifier].timeLimit = v.contents[0].content.duration || 300
          this.classDiagram =
            this.classDiagramStore.collectiveData[v.contents[0].content.identifier] || []
          // this.contentLoaded = true
        }
      })
    }
    this.allLanguages = this.authInitService.ordinals.subTitles
    this.loaderService.changeLoadState(true)
    // active lex id
    this.contentSubscription = this.contentService.changeActiveCont.subscribe(id => {
      this.currentId = id
      if (!this.classDiagramStore.collectiveData[id]) {
        this.classDiagramStore.collectiveData[id] = new ClassDiagram()
      }
      this.classDiagram = this.classDiagramStore.collectiveData[id]
      this.problemStatement = this.classDiagram.problemStatement
    })
  }

  getAttributesCount(className: string) {
    return this.classDiagram.options.classes.filter(e => e.belongsTo === className).length
  }

  customStepper(step: number) {
    if (step === 1) {
    } else if (step === 3 && this.currentStep === 2) {
      if (this.classDiagramStore.collectiveData[this.currentId].options.classes.length) {
        this.currentStep = step
      } else {
        this.showNotification(Notify.NO_CONTENT)
      }
    } else {
      this.currentStep = step
    }
  }

  changeContent(data: NSContent.IContentMeta) {
    this.currentId = data.identifier
    this.contentService.changeActiveCont.next(data.identifier)
  }

  createInAnotherLanguage(lang: string) {
    this.loaderService.changeLoad.next(true)
    this.contentService
      .createInAnotherLanguage(lang, { artifactURL: '', downloadUrl: '' })
      .subscribe(
        data => {
          this.loaderService.changeLoad.next(false)
          if (data !== true) {
            this.allContents.push(data as NSContent.IContentMeta)
            this.changeContent(data as NSContent.IContentMeta)
            this.showNotification(Notify.CONTENT_CREATE_SUCCESS)
          } else {
            this.showNotification(Notify.DATA_PRESENT)
          }
        },
        error => {
          if (error.status === 409) {
            const errorMap = new Map<string, NSContent.IContentMeta>()
            errorMap.set(this.currentId, this.contentService.getUpdatedMeta(this.currentId))
            this.dialog.open(ErrorParserComponent, {
              width: '750px',
              height: '450px',
              data: {
                errorFromBackendData: error.error,
                dataMapping: errorMap,
              },
            })
          }
          this.loaderService.changeLoad.next(false)
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: Notify.CONTENT_FAIL,
            },
            duration: NOTIFICATION_TIME * 1000,
          })
        },
    )
  }

  triggerSave(meta: NSContent.IContentMeta, id: string): Observable<boolean> {
    const requestBody: NSApiRequest.IContentUpdate = {
      hierarchy: {},
      nodesModified: {
        [id]: {
          isNew: false,
          root: true,
          metadata: meta,
        },
      },
    }
    return this.editorService
      .updateContent(requestBody)
      .pipe(mergeMap(() => {
        this.contentService.resetOriginalMeta(meta, id)
        return of(true)
      }))
  }

  triggerUpload() {
    this.classDiagram.timeLimit = this.contentService.getUpdatedMeta(this.currentId).duration
    this.classDiagramStore.collectiveData[this.currentId].timeLimit = this.contentService.getUpdatedMeta(this.currentId).duration
    const dataWithKey = JSON.parse(JSON.stringify(this.classDiagramStore.collectiveData[this.currentId]))
    // tslint:disable-next-line:max-line-length
    const jsonPath = `${this.accessService.rootOrg}/${this.accessService.org.replace(/ /g, '_')}/Public/${this.currentId}/web-hosted`
    return this.uploadService.multipleJsonUpload(
      dataWithKey as ClassDiagram,
      this.contentService.getUpdatedMeta(this.currentId),
      jsonPath
    )
  }

  wrapperForTriggerSave(): Observable<boolean> {
    this.loaderService.changeLoad.next(true)
    if (!(this.contentService.getUpdatedMeta(this.currentId) || {}).duration) {
      this.contentService.setUpdatedMeta({ duration: this.classDiagram.timeLimit } as any, this.currentId)
    }
    const hasTimeChanged =
      (this.contentService.upDatedContent[this.currentId] || {}).duration &&
      this.classDiagram.timeLimit !== this.contentService.upDatedContent[this.currentId].duration
    const doUploadJson = this.classDiagramStore.hasChanged || hasTimeChanged
    return (doUploadJson
      ? this.triggerUpload()
      : of({} as any)
    ).pipe(
      mergeMap(v => {
        const updatedMeta = JSON.parse(
          JSON.stringify(this.contentService.upDatedContent[this.currentId] || {}),
        )
        if (v && v[0] && !v[0].error) {
          updatedMeta.artifactUrl = (v[0].artifactUrl || v[0].downloadUrl).replace(/%2F/g, '/')
          this.classDiagram.timeLimit = this.contentService.getUpdatedMeta(this.currentId).duration
          updatedMeta.downloadUrl = v[0].downloadUrl ? v[0].downloadUrl.replace(/%2F/g, '/') : ''
          this.classDiagramStore.hasChanged = false
          return this.triggerSave(updatedMeta, this.currentId)
        }
        return of(false)

      }),
    )
  }

  updateData($event: string) {
    this.problemStatement = $event
    this.classDiagramStore.updateData(this.currentId, { problemStatement: $event })
    this.classDiagramStore.hasChanged = true
  }

  addObject(objectType: 'classes' | 'relations') {
    if (!this.classDiagram.options[objectType].length) {
      this.classDiagram.options[objectType] = []
    }
    if (objectType === 'classes') {
      this.openClassDiagramEditor(objectType, this.classDiagram.options[objectType].length, 'add')
    } else {
      const classLen = this.classDiagram.options.classes.filter(e => e.type === 'class').length
      if (classLen > 1) {
        this.openClassDiagramEditor(objectType, this.classDiagram.options[objectType].length, 'add')
      } else {
        this.showNotification(Notify.RELATION_MIN_CLASS_REQUIRED)
      }
    }
  }

  deleteObject(objectType: 'classes' | 'relations', index: number) {
    if (objectType === 'classes') {
      const className = this.classDiagram.options.classes[index].name
      const classHasRelation = this.classDiagram.options.relations.filter(e => e.source === className || e.target === className)
      if (classHasRelation.length) {
        this.snackBar.openFromComponent(NotificationComponent, {
          data: {
            type: Notify.CLASS_USED_IN_RELATION,
          },
          duration: NOTIFICATION_TIME * 1000,
        })
      } else {
        this.classDiagram.options.classes.splice(index, 1)
        this.classDiagram.options.classes = this.classDiagram.options.classes.filter(e => e.belongsTo !== className)
      }
    } else {
      this.classDiagram.options.relations.splice(index, 1)
    }
  }

  openClassDiagramEditor(objectType: 'classes' | 'relations', index: number, action: string = 'edit') {
    const dialogRef = this.dialog.open(ClassRelationEditorComponent, {
      data: {
        index,
        action,
        type: objectType,
        options: JSON.parse(JSON.stringify(this.classDiagram.options)),
      },
      width: objectType === 'classes' ? '1000px' : 'auto',
    })
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.classDiagramStore.hasChanged = true
        if (action === 'edit') {
          const originalClassName = this.classDiagram.options.classes[index].name
          const updatedClassName = res.classes[index].name
          if (objectType === 'classes' && action === 'edit' && originalClassName !== updatedClassName) {
            this.classDiagram.options.relations.map(e => {
              if (e.source === originalClassName) {
                e.source = updatedClassName
              } else if (e.target === originalClassName) {
                e.target = updatedClassName
              }
            })
          }
        }
        this.classDiagram.options[objectType] = res[objectType]
      }
    })
  }

  action(actionType: string) {
    switch (actionType) {
      case 'next':
        this.currentStep += 1
        break
      case 'preview':
        this.preview()
        break
      case 'save':
        this.save()
        break
      case 'push':
        this.takeAction()
        break
      case 'delete':
        const dialog = this.dialog.open(DeleteDialogComponent, {
          width: '600px',
          height: 'auto',
          data: this.contentService.getUpdatedMeta(this.currentId),
        })
        dialog.afterClosed().subscribe(confirm => {
          if (confirm) {
            this.allContents = this.allContents.filter(v => v.identifier !== this.currentId)
            if (this.allContents.length) {
              this.contentService.changeActiveCont.next(this.allContents[0].identifier)
            } else {
              this.router.navigateByUrl('/author/home')
            }
          }
        })
        break
      case 'close':
        this.router.navigateByUrl('/author/home')
        break
    }
  }

  /**
   * Checks the validity of selected data collection
   * @returns True if the data is valid else false
   */
  checkValidity(): boolean {
    const updatedClassDiagram = this.classDiagramStore.collectiveData[this.currentId]
    let returnVal = true
    if (!updatedClassDiagram.problemStatement) {
      returnVal = false
      this.showNotification(Notify.EMPTY_PROBLEM_STATEMENT)
    } else if (!updatedClassDiagram.options.classes.length) {
      returnVal = false
      this.showNotification(Notify.CLASS_DIAGRAM_NO_CLASS)
    }
    return returnVal
  }

  save() {
    const hasMinLen = this.classDiagram.options.classes.length
    const needSave = Object.keys(this.contentService.upDatedContent[this.currentId] || {}).length
      || this.classDiagramStore.hasChanged
    if (hasMinLen && needSave) {
      if (this.checkValidity()) {
        // if any change in data, then upload json
        this.wrapperForTriggerSave().subscribe(flag => {
          if (flag) {
            this.loaderService.changeLoad.next(false)
            this.showNotification(Notify.SAVE_SUCCESS)
          } else {
            this.loaderService.changeLoad.next(false)
            this.showNotification(Notify.SAVE_FAIL)
          }
        },
                                               () => {
            this.loaderService.changeLoad.next(false)
            this.showNotification(Notify.SAVE_FAIL)
          },
        )
      } else {
        this.currentStep = 2
      }
    } else {
      // enters if the data does not have min len or no changes has been made in meta or content
      if (!this.classDiagram.options.classes.length) {
        this.showNotification(Notify.CLASS_DIAGRAM_NO_CLASS)
        this.currentStep = 2
      } else {
        this.showNotification(Notify.UP_TO_DATE)
      }
    }
  }

  delete() {
    const confirmDelete = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: 'delete',
    })
    confirmDelete.afterClosed().subscribe(confirm => {
      if (confirm) {
        this.loaderService.changeLoad.next(true)
        this.editorService.deleteContent(this.currentId).subscribe(
          () => {
            this.loaderService.changeLoad.next(false)
            this.showNotification(Notify.SUCCESS)
            this.allContents = this.allContents.filter(v => v.identifier !== this.currentId)
            if (this.allContents.length) {
              this.contentService.changeActiveCont.next(this.allContents[0].identifier)
            } else {
              this.router.navigateByUrl('/author/home')
            }
          },
          () => {
            this.loaderService.changeLoad.next(false)
            this.showNotification(Notify.CONTENT_FAIL)
          },
        )
      }
    })
  }

  preview() {
    if (!this.classDiagram.options.classes.length) {
      this.showNotification(Notify.CLASS_DIAGRAM_NO_CLASS)
    } else {
      const needSave =
        this.classDiagramStore.hasChanged ||
        Object.keys(this.contentService.upDatedContent[this.currentId] || {}).length
      if (needSave && this.checkValidity()) {
        this.wrapperForTriggerSave().subscribe(flag => {
          if (flag) {
            this.loaderService.changeLoad.next(false)
            this.previewMode = true
            this.mimeTypeRoute = VIEWER_ROUTE_FROM_MIME(
              this.contentService.getUpdatedMeta(this.currentId).mimeType as any,
            )
          } else {
            this.loaderService.changeLoad.next(false)
            this.showNotification(Notify.SAVE_FAIL)
          }
        },
                                               () => {
            this.loaderService.changeLoad.next(false)
            this.showNotification(Notify.SAVE_FAIL)
          },
        )
      } else {
        if (this.checkValidity()) {
          this.previewMode = true
          this.mimeTypeRoute = VIEWER_ROUTE_FROM_MIME(
            this.contentService.getUpdatedMeta(this.currentId).mimeType as any,
          )
        }
      }
    }
  }

  validationCheck(): Observable<boolean> {
    let returnValue = true
    if (!this.classDiagram.options.classes.length) {
      returnValue = false
      this.currentStep = 2
      this.showNotification(Notify.CLASS_DIAGRAM_NO_CLASS)
    } else if (!this.contentService.isValid(this.currentId) || !this.contentService.isValid(this.currentId)
      && !this.contentService.getUpdatedMeta(this.currentId).artifactUrl) {
      this.submitPressed = true
      this.showNotification(Notify.MANDATORY_FIELD_ERROR)
      returnValue = false
      this.currentStep = 3
    } else if (this.classDiagramStore.hasChanged) {
      if (this.checkValidity()) {
        return this.wrapperForTriggerSave()
      }
      returnValue = false
    }
    return of(returnValue)
  }

  takeAction() {
    const needSave =
      Object.keys(this.contentService.upDatedContent[this.currentId] || {}).length ||
      this.classDiagramStore.hasChanged
    if (!needSave && this.contentService.getUpdatedMeta(this.currentId).status === 'Live') {
      this.showNotification(Notify.UP_TO_DATE)
      return
    }
    this.validationCheck().subscribe(
      valid => {
        if (valid) {
          const dialogRef = this.dialog.open(CommentsDialogComponent, {
            width: '750px',
            height: '450px',
            data: this.contentService.getOriginalMeta(this.currentId),
          })
          dialogRef.afterClosed().subscribe((commentsForm: FormGroup) => {
            this.finalCall(commentsForm)
          })
        }
      },
      () => {
        this.showNotification(Notify.SAVE_FAIL)
      },
    )
  }
  isPublisherSame(): boolean {
    const publisherDetails =
      this.contentService.getUpdatedMeta(this.currentId).publisherDetails || []
    return publisherDetails.find(v => v.id === this.accessService.userId) ? true : false
  }

  isDirectPublish(): boolean {
    return (
      ['Draft', 'Live'].includes(this.contentService.originalContent[this.currentId].status) &&
      this.isPublisherSame()
    )
  }

  finalCall(commentsForm: FormGroup) {
    if (commentsForm) {
      const body: NSApiRequest.IForwardBackwardActionGeneral = {
        comment: commentsForm.controls.comments.value,
        operation:
          commentsForm.controls.action.value === 'accept' ||
            ['Draft', 'Live'].includes(
              this.contentService.originalContent[this.currentId].status,
            )
            ? ((this.accessService.authoringConfig.isMultiStepFlow && this.isDirectPublish()) ||
              !this.accessService.authoringConfig.isMultiStepFlow) &&
              this.accessService.rootOrg.toLowerCase() === 'siemens'
              ? 100000
              : 1
            : 0,
      }

      const updatedContent = this.contentService.upDatedContent[this.currentId] || {}
      const updatedMeta = this.contentService.getUpdatedMeta(this.currentId)
      const needSave = Object.keys(this.contentService.upDatedContent[this.currentId] || {})
        .length
      const saveCall = (needSave
        ? this.triggerSave(updatedContent, this.currentId)
        : of({} as any)
      ).pipe(
        mergeMap(() =>
          this.editorService
            .forwardBackward(
              body,
              this.currentId,
              this.contentService.originalContent[this.currentId].status,
          )
            .pipe(
              mergeMap(() =>
                this.notificationSvc
                  .triggerPushPullNotification(
                    updatedMeta,
                    body.comment,
                    body.operation ? true : false,
                )
                  .pipe(
                    catchError(() => {
                      return of({} as any)
                    }),
                ),
              ),
          ),
        ),
      )
      this.loaderService.changeLoad.next(true)
      saveCall.subscribe(
        () => {
          this.loaderService.changeLoad.next(false)
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: this.getMessage('success'),
            },
            duration: NOTIFICATION_TIME * 1000,
          })
          this.allContents = this.allContents.filter(v => v.identifier !== this.currentId)
          if (this.allContents.length) {
            this.contentService.changeActiveCont.next(this.allContents[0].identifier)
          } else {
            this.router.navigateByUrl('/author/home')
          }
        },
        error => {
          if (error.status === 409) {
            const errorMap = new Map<string, NSContent.IContentMeta>()
            errorMap.set(
              this.currentId,
              this.contentService.getUpdatedMeta(this.currentId),
            )
            this.dialog.open(ErrorParserComponent, {
              width: '80vw',
              height: '90vh',
              data: {
                errorFromBackendData: error.error,
                dataMapping: errorMap,
              },
            })
          }
          this.loaderService.changeLoad.next(false)
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: this.getMessage('failure'),
            },
            duration: NOTIFICATION_TIME * 1000,
          })
        },
      )
    }
  }

  getMessage(type: 'success' | 'failure') {
    if (type === 'success') {
      switch (this.contentService.originalContent[this.currentId].status) {
        case 'Draft':
        case 'Live':
          return Notify.SEND_FOR_REVIEW_SUCCESS
        case 'InReview':
          return Notify.REVIEW_SUCCESS
        case 'Reviewed':
          return Notify.PUBLISH_SUCCESS
        default:
          return ''
      }
    }
    switch (this.contentService.originalContent[this.currentId].status) {
      case 'Draft':
      case 'Live':
        return Notify.SEND_FOR_REVIEW_FAIL
      case 'InReview':
        return Notify.REVIEW_FAIL
      case 'Reviewed':
        return Notify.PUBLISH_FAIL
      default:
        return ''
    }
  }

  getAction(): string {
    switch (this.contentService.originalContent[this.currentId].status) {
      case 'Draft':
      case 'Live':
        return 'sendForReview'
      case 'InReview':
      case 'QualityReview':
        return 'review'
      case 'Reviewed':
        return 'publish'
      default:
        return 'sendForReview'
    }
  }

  closePreview() {
    this.previewMode = false
  }

  toggleSettingButtons() {
    this.showSettingButtons = !this.showSettingButtons
  }

  // fullScreenToggle() { }

  showNotification(message: string) {
    this.snackBar.openFromComponent(NotificationComponent, {
      data: {
        type: message,
      },
      duration: NOTIFICATION_TIME * 1000,
    })
  }

  canDelete() {
    return this.accessService.hasRole(['editor', 'admin']) ||
      (['Draft', 'Live'].includes(this.contentService.originalContent[this.currentId].status) &&
        this.contentService.originalContent[this.currentId].creatorContacts.find(v => v.id === this.accessService.userId)
      )
  }
}
