import { Component, OnDestroy, OnInit } from '@angular/core'
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms'
import { MatDialog } from '@angular/material/dialog'
import { MatSnackBar } from '@angular/material/snack-bar'
import { Router } from '@angular/router'
import { NOTIFICATION_TIME } from '@ws/author/src/lib/constants/constant'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { ICreateEntity } from '@ws/author/src/lib/interface/create-entity'
import { ErrorParserComponent } from '@ws/author/src/lib/modules/shared/components/error-parser/error-parser.component'
import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { AuthInitService } from '@ws/author/src/lib/services/init.service'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { Subscription } from 'rxjs'
import { CreateService } from './create.service'

@Component({
  selector: 'ws-auth-generic',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss'],
})
export class CreateComponent implements OnInit, OnDestroy {
  entity: ICreateEntity[] = []
  resourceEntity!: ICreateEntity
  routerSubscription = <Subscription>{}
  allLanguages: any
  language = ''
  languageName = ''
  error = false
  currentContent!: ICreateEntity | null
  createCourseForm!: FormGroup
  constructor(
    private snackBar: MatSnackBar,
    private svc: CreateService,
    private router: Router,
    private loaderService: LoaderService,
    private accessControlSvc: AccessControlService,
    private authInitService: AuthInitService,
    private dialog: MatDialog,
    private formBuilder: FormBuilder,
  ) {
    this.createForm()
  }

  ngOnInit() {
    this.authInitService.creationEntity.forEach(v => {
      if (!v.parent && v.available) {
        if (v.id === 'resource') {
          this.resourceEntity = v
        } else {
          v.enabled = true
          this.entity.push(v)
        }
      }
    })
    this.entity = this.entity.reverse()
    this.loaderService.changeLoadState(false)
    this.allLanguages = this.authInitService.ordinals.subTitles || []
    this.language = this.accessControlSvc.locale

    const selectedLang = this.allLanguages.find((i: any) => i.srclang === this.language)
    if (selectedLang && selectedLang.srclang) {
      this.languageName = selectedLang.label
    }
  }

  ngOnDestroy() {
    this.loaderService.changeLoad.next(false)
  }

  contentClicked(content: ICreateEntity) {
    this.currentContent = content

  }
  createContent() {
    this.loaderService.changeLoad.next(true)
    const _name = this.createCourseForm.get('name')
    if (this.currentContent && _name && _name.value) {

      this.svc
        .createV2({
          name: _name.value,
          contentType: this.currentContent.contentType,
          mimeType: this.currentContent.mimeType,
          locale: this.language,
          primaryCategory: this.currentContent.primaryCategory,
          ...(this.currentContent.additionalMeta || {}),
        })
        .subscribe(
          (id: string) => {
            this.loaderService.changeLoad.next(false)
            this.snackBar.openFromComponent(NotificationComponent, {
              data: {
                type: Notify.CONTENT_CREATE_SUCCESS,
              },
              duration: NOTIFICATION_TIME * 1000,
            })
            this.router.navigateByUrl(`/author/editor/${id}`)
          },
          error => {
            if (error.status === 409) {
              this.dialog.open(ErrorParserComponent, {
                width: '80vw',
                height: '90vh',
                data: {
                  errorFromBackendData: error.error,
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
  }
  createForm() {
    this.createCourseForm = this.formBuilder.group({
      name: new FormControl('', [Validators.required, Validators.minLength(10)]),
    })
  }
  cancel() {
    this.createCourseForm.reset()
    this.currentContent = null
  }
  setCurrentLanguage(lang: string, label: string) {
    this.languageName = label
    this.language = lang
  }
}
