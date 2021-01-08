import { Component, OnInit, Input } from '@angular/core'
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
import { startWith, debounceTime, distinctUntilChanged, filter, switchMap } from 'rxjs/operators'
import { of } from 'rxjs'
import { ActivatedRoute } from '@angular/router'
import { CreateMDOService } from './create-mdo.services'
interface IUser { userId: string, fullName: string; email: string; role: string }
@Component({
  selector: 'ws-app-create-mdo',
  providers: [UploadService, ApiService, AccessControlService, EditorContentService, EditorService, AuthInitService, LoaderService],
  templateUrl: './create-mdo.component.html',
  styleUrls: ['./create-mdo.component.scss'],
})
export class CreateMdoComponent implements OnInit {
  @Input() isSubmitPressed = false
  @Input() nextAction = 'done'
  @Input() stage = 1
  @Input() type = ''
  contentMeta!: NSContent.IContentMeta
  fracData: any = []
  contentForm!: FormGroup
  imageTypes = ['.png', '.jpg', '.jpeg', '.jtif', '.tiff']
  tabledata: any = []
  data: any = []
  canUpdate = true
  isEditEnabled = false
  canExpiry = true
  creatorContactsCtrl!: FormControl
  trackContactsCtrl!: FormControl
  publisherDetailsCtrl!: FormControl
  editorsCtrl!: FormControl
  creatorDetailsCtrl!: FormControl
  audienceCtrl!: FormControl
  jobProfileCtrl!: FormControl
  regionCtrl!: FormControl
  accessPathsCtrl!: FormControl
  keywordsCtrl!: FormControl
  competencyCtrl!: FormControl
  selectedSkills: string[] = []
  ordinals!: any
  resourceTypes: string[] = []
  employeeList: any[] = []
  audienceList: any[] = []
  jobProfileList: any[] = []
  regionList: any[] = []
  accessPathList: any[] = []
  competencyValue: any[] = []
  fetchTagsStatus: 'done' | 'fetching' | null = null
  complexityLevelList: any
  editorService: any
  submittedForm = true
  content!: NSContent.IContentMeta
  filteredOptions$: any
  interestSvc: any
  competencyOptions$: any
  allLanguages: any
  userId!: string
  departmentId!: string
  departmentRoleId!: string
  data1: any
  updateId !: number
  department!: string
  subDepartments!: any
  isUpdate = false
  workFlow = [{ isActive: true, isCompleted: false, name: 'Basic Details', step: 0 },
  { isActive: false, isCompleted: false, name: 'Classification', step: 1 },
  { isActive: false, isCompleded: false, name: 'Intended for', step: 2 }]
  constructor(public dialog: MatDialog,
    private uploadService: UploadService,
    private snackBar: MatSnackBar,
    private contentService: EditorContentService,
    private loader: LoaderService,
    private authInitService: AuthInitService,
    private createMdoService: CreateMDOService,
    // private router: Router,
    private activatedRoute: ActivatedRoute) {
    this.contentForm = new FormGroup({
      name: new FormControl(),
      head: new FormControl(),
      deptSubTypeId: new FormControl(),
      fileUpload: new FormControl(),
    })
    this.activatedRoute.params.subscribe(params => {
      let data = params['data']
      this.department = params['department']
      data = JSON.parse(data)
      if (this.data !== undefined || this.data !== null) {
        this.isUpdate = true
        this.updateId = data.row.id
      }
      this.contentForm = new FormGroup({
        name: new FormControl(data.row.mdo),
        head: new FormControl(data.row.head),
        deptSubTypeId: new FormControl(data.row.typeid),
        fileUpload: new FormControl(),

      })
    })
  }
  ngOnInit() {
    this.typeCheck()
    this.creatorContactsCtrl = new FormControl()
    this.trackContactsCtrl = new FormControl()
    this.publisherDetailsCtrl = new FormControl()
    this.editorsCtrl = new FormControl()
    this.creatorDetailsCtrl = new FormControl()
    this.keywordsCtrl = new FormControl('')
    this.competencyCtrl = new FormControl('')

    this.audienceCtrl = new FormControl()
    this.jobProfileCtrl = new FormControl()
    this.regionCtrl = new FormControl()
    this.accessPathsCtrl = new FormControl()
    this.accessPathsCtrl.disable()

    this.tabledata = {
      columns: [
        { displayName: 'Full name', key: 'fullName' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Role', key: 'role' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }

    this.getAllDepartmentsAPI()
    this.creatorContactsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
    // .subscribe(
    //   users => {
    //     // this.employeeList = users || <string[]>[]
    //     this.fetchTagsStatus = 'done'
    //   },
    //   () => {
    //     this.fetchTagsStatus = 'done'
    //   },
    // )

    this.trackContactsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
    // .subscribe(
    //   users => {
    //     // this.employeeList = users || <string[]>[]
    //     this.fetchTagsStatus = 'done'
    //   },
    //   () => {
    //     this.fetchTagsStatus = 'done'
    //   },
    // )

    this.publisherDetailsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
    // .subscribe(
    //   users => {
    //     // this.employeeList = users || <string[]>[]
    //     this.fetchTagsStatus = 'done'
    //   },
    //   () => {
    //     this.fetchTagsStatus = 'done'
    //   },
    // )

    this.editorsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
    // .subscribe(
    //   users => {
    //     this.employeeList = users || <string[]>[]
    //     this.fetchTagsStatus = 'done'
    //   },
    //   () => {
    //     this.fetchTagsStatus = 'done'
    //   },
    // )

    this.creatorDetailsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
    // .subscribe(
    //   users => {
    //     this.employeeList = users || <string[]>[]
    //     this.fetchTagsStatus = 'done'
    //   },
    //   () => {
    //     this.fetchTagsStatus = 'done'
    //   },
    // )

    this.audienceCtrl.valueChanges.subscribe(() => this.fetchAudience())

    this.jobProfileCtrl.valueChanges.subscribe(() => this.fetchJobProfile())

    this.regionCtrl.valueChanges
      .pipe(
        debounceTime(400),
        filter(v => v),
      )
      .subscribe(() => this.fetchRegion())

    this.accessPathsCtrl.valueChanges.pipe(
      debounceTime(400),
      filter(v => v),
    ).subscribe(() => this.fetchAccessRestrictions())

    this.contentService.changeActiveCont.subscribe(data => {
      if (this.contentMeta && this.canUpdate) {
        this.storeData()
      }
      this.content = this.contentService.getUpdatedMeta(data)
    })

    this.filteredOptions$ = this.keywordsCtrl.valueChanges.pipe(
      startWith(this.keywordsCtrl.value),
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(value => this.interestSvc.fetchAutocompleteInterestsV2(value)),
    )
    this.competencyOptions$ = this.competencyCtrl.valueChanges.pipe(
      startWith(this.competencyCtrl.value),
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(value => this.interestSvc.fetchAutocompleteCompetencyV2(value)),
    )

    // this.allLanguages = this.data1.languages
  }
  getAllDepartmentsAPI() {
    this.createMdoService.getAllSubDepartments(this.department).subscribe(res => {
      this.subDepartments = res
    })
  }
  typeCheck() {
    if (this.type) {
      let dataName = ''
      switch (this.type) {
        case 'URL':
          dataName = 'Attach Link'
          break
        case 'UPLOAD':
          dataName = 'Upload'
          break
        case 'ASSE':
          dataName = 'Assessment'
          break
        case 'WEB':
          dataName = 'Web Pages'
          break

        default:
          break
      }
      if (dataName) {
        this.workFlow.push({
          isActive: false,
          isCompleted: true,
          name: dataName,
          step: -1,
        })
      }
    }
  }
  fetchAudience(): void {
    if ((this.audienceCtrl.value || '').trim()) {
      this.audienceList = this.ordinals.audience.filter(
        (v: any) => v.toLowerCase().indexOf(this.audienceCtrl.value.toLowerCase()) > -1,
      )
    } else {
      this.audienceList = this.ordinals.audience.slice()
    }
  }
  checkCondition(first: string, seconnd: string) {
    if (first && seconnd) {

    }
    return true
  }
  showError(error: string) {
    if (error) {

    }
    return true
  }
  private fetchJobProfile() {
    if ((this.jobProfileCtrl.value || '').trim()) {
      this.jobProfileList = this.ordinals.jobProfile.filter(
        (v: any) => v.toLowerCase().indexOf(this.jobProfileCtrl.value.toLowerCase()) > -1,
      )
    } else {
      this.jobProfileList = this.ordinals.jobProfile.slice()
    }
  }

  private fetchRegion() {
    if ((this.regionCtrl.value || '').trim()) {
      this.regionList = this.ordinals.region.filter(
        (v: any) => v.toLowerCase().indexOf(this.regionCtrl.value.toLowerCase()) > -1,
      )
    } else {
      this.regionList = []
    }
  }

  private fetchAccessRestrictions() {
    if (this.accessPathsCtrl.value.trim()) {
      this.accessPathList = this.ordinals.accessPaths.filter((v: any) => v.toLowerCase().
        indexOf(this.accessPathsCtrl.value.toLowerCase()) === 0)
    } else {
      this.accessPathList = this.ordinals.accessPaths.slice()
    }
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
      this.data = this.getAllResponse(response)
      console.log(this.departmentRoleId)
      console.log(this.departmentId)
      this.data.forEach((element: { userId: string }) => {
        console.log(element)
        this.createMdoService.assignAdminToDepartment(element.userId, this.departmentId, this.departmentRoleId).subscribe(res => {
          this.departmentId = res.id
          this.departmentRoleId = res.rolesInfo[0].deptRoleId
        })
      })
    })
  }
  getAllResponse(response: any) {
    const tempArray: IUser[] = []
    if (response && response !== null && response !== undefined) {
      this.data = response.data.forEach((users: any) => {
        const obj: IUser = {
          userId: users.userId,
          fullName: `${users.fullname}`,
          email: users.email,
          role: "ADMIN"
        }
        tempArray.push(obj)
      })
      return tempArray
    }
    return []
  }
  textOnly(event: any) {
    console.log(event.key)
  }
  onSubmit() {
    if (!this.isUpdate) {
      if (this.contentForm.value.name !== null && this.contentForm.value.head !== null
        && this.contentForm.value.deptSubTypeId !== null) {
        this.createMdoService.createDepartment(this.contentForm.value).subscribe(res => {
          this.departmentId = res.id
          this.departmentRoleId = res.rolesInfo[0].deptRoleId
        })
        this.submittedForm = false
      }
    } else {
      if (this.contentForm.value.name !== null && this.contentForm.value.head !== null
        && this.contentForm.value.deptSubTypeId !== null) {
        this.createMdoService.updateDepartment(this.contentForm.value, this.updateId).subscribe(res => {
          this.departmentId = res.id
          this.departmentRoleId = res.rolesInfo[0].deptRoleId
        })
        this.submittedForm = false
      }
    }

  }

}
