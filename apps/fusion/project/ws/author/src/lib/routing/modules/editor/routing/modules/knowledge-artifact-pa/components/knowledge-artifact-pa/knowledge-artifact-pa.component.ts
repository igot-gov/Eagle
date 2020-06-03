import { Component, OnDestroy, OnInit } from '@angular/core'
import { MatDialog, MatSnackBar } from '@angular/material'
import { ActivatedRoute, Router } from '@angular/router'
import { NOTIFICATION_TIME } from '@ws/author/src/lib/constants/constant'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { ErrorParserComponent } from '@ws/author/src/lib/modules/shared/components/error-parser/error-parser.component'
import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { AuthNavBarToggleService } from '@ws/author/src/lib/services/auth-nav-bar-toggle.service'
import { mergeMap } from 'rxjs/operators'
import { NSApiRequest } from '../../../../../../../../interface/apiRequest'
import { LoaderService } from './../../../../../../../../services/loader.service'
import { EditorService } from './../../../../../services/editor.service'
import { ConfirmDialogComponent } from '@ws/author/src/lib/modules/shared/components/confirm-dialog/confirm-dialog.component'

@Component({
  selector: 'ws-auth-knowledge-artifact-pa',
  templateUrl: './knowledge-artifact-pa.component.html',
  styleUrls: ['./knowledge-artifact-pa.component.scss'],
})
export class KnowledgeArtifactPaComponent implements OnInit, OnDestroy {
  currentContent = ''
  content: NSContent.IContentMeta = {
    contentType: 'Knowledge Artifact',
  } as any
  oldContent: NSContent.IContentMeta = {} as any
  mode: 'upload' | 'editor' = 'upload'
  isPathFinders = false
  constructor(
    private activateRouter: ActivatedRoute,
    private editorService: EditorService,
    private loaderService: LoaderService,
    private snackBar: MatSnackBar,
    private accessService: AccessControlService,
    private router: Router,
    private authNavBarSvc: AuthNavBarToggleService,
    private dialog: MatDialog,
  ) {}

  ngOnInit() {
    this.isPathFinders = this.accessService.rootOrg === 'Pathfinders'
    this.activateRouter.data.subscribe(data => {
      if (data && data.content) {
        this.currentContent = data.content
      }
    })
    if (this.activateRouter.parent && this.activateRouter.parent.parent) {
      this.activateRouter.parent.parent.data.subscribe(data => {
        if (data.contents && data.contents.length) {
          this.oldContent = data.contents[0].content
          this.currentContent = this.oldContent.identifier
        }
      })
    }
    if (this.isPathFinders) {
      this.authNavBarSvc.toggle(false)
    }
  }

  ngOnDestroy() {
    this.authNavBarSvc.toggle(true)
    this.loaderService.changeLoad.next(false)
  }

  submit() {
    const requestBody: NSApiRequest.IContentUpdate = {
      hierarchy: {},
      nodesModified: {
        [this.currentContent]: {
          isNew: false,
          root: true,
          metadata: this.content,
        },
      },
    }
    const body: NSApiRequest.IForwardBackwardActionGeneral = {
      comment: 'Author published Knowledge Artifact',
      operation: 1,
    }
    this.loaderService.changeLoad.next(true)
    this.editorService
      .updateContent(requestBody)
      .pipe(mergeMap(() => this.editorService.forwardBackward(body, this.currentContent, 'Draft')))
      .subscribe(
        () => {
          this.loaderService.changeLoad.next(false)
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: Notify.PUBLISH_SUCCESS_LATE,
            },
            duration: NOTIFICATION_TIME * 1000,
          })
          if (this.isPathFinders) {
            this.router.navigateByUrl('/page/resource-hub')
          } else {
            this.router.navigateByUrl('/author/home')
          }
        },
        error => {
          if (error.status === 409) {
            const errorMap = new Map<string, NSContent.IContentMeta>()
            errorMap.set(this.currentContent, this.content)
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
              type: Notify.PUBLISH_FAIL,
            },
            duration: NOTIFICATION_TIME * 1000,
          })
        },
      )
  }

  takeAction(data: any) {
    if (data === 'next') {
      const dialogRefForPublish = this.dialog.open(ConfirmDialogComponent, {
        width: '70%',
        data: 'publishMessage',
      })
      dialogRefForPublish.afterClosed().subscribe(result => {
        if (result) {
          this.submit()
        }
      })
    } else if (data === 'back') {
      if (this.mode !== 'upload') {
        this.mode = 'upload'
      } else {
        if (this.isPathFinders) {
          this.router.navigateByUrl('/page/resource-hub')
        } else {
          this.router.navigateByUrl('/author/home')
        }
      }
    } else {
      this.content = {
        name: this.oldContent.name || '',
        description: this.oldContent.description || '',
        categoryType: this.oldContent.categoryType || '',
        complexityLevel: this.oldContent.complexityLevel || '',
        appIcon: this.oldContent.appIcon || '',
        fileType: this.oldContent.fileType || '',
        ...data,
      } as any
      this.mode = 'editor'
    }
  }
}
