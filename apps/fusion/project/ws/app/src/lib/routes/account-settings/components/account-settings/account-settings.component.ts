import { Component, OnInit, ViewEncapsulation, ViewChild, ElementRef } from '@angular/core'
import { IMAGE_SUPPORT_TYPES, IMAGE_MAX_SIZE } from 'project/ws/author/src/lib/constants/upload'

// import { NotificationComponent } from 'project/ws/app/src/lib/routes/notification/components/notification/notification.component'
import { MatSnackBar } from '@angular/material/snack-bar'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { FormBuilder, Validators, FormGroup } from '@angular/forms'
import { MatDialog } from '@angular/material/dialog'
import { ImageCropComponent } from '@ws-widget/utils/src/public-api'
// import { NOTIFICATION_TIME } from '../../../../../../../author/src/lib/constants/constant'
// import { Notify } from '../../../../../../../author/src/lib/constants/notificationMessage'
import { NsAccountSettings } from '../../models/account-settings.model'
import { ConfigurationsService } from 'library/ws-widget/utils/src/lib/services/configurations.service'
import { AccountSettingsService } from '../../services/account-settings.service'
import { ActivatedRoute } from '@angular/router'
import { NsMiniProfile } from '../../../../../../../../../library/ws-widget/collection/src/public-api'
// import { NotificationComponent } from '../../../notification/components/notification/notification.component'
// import { Notify } from '../../../../../../../author/src/lib/constants/notificationMessage'
// import { NOTIFICATION_TIME } from '../../../../../../../author/src/lib/constants/constant'
@Component({
  selector: 'ws-app-account-settings',
  templateUrl: './account-settings.component.html',
  styleUrls: ['./account-settings.component.scss'],
  // tslint:disable-next-line
  encapsulation: ViewEncapsulation.None,
})
export class AccountSettingsComponent implements OnInit {
  fetchingPasswordUrl = false
  @ViewChild('errorUpdatingEmail', { static: true })
  errorUpdatingEmailMessage!: ElementRef<any>
  @ViewChild('invalidFormat', { static: true })
  invalidFormatMessage!: ElementRef<any>
  @ViewChild('sizeGreaterAllowed', { static: true })
  sizeGreaterAllowedMessage!: ElementRef<any>
  @ViewChild('changesUpdated', { static: true })
  changesUpdatedMessage!: ElementRef<any>
  profilePicture = ''
  fileUrl = ''
  emailResponse: any
  email = ''
  userId = ''
  checkEmail = false
  newCourse = false
  replyToPost = false
  registrationOpen = false
  settingsForm: FormGroup | null = null
  roles: FormGroup | null = null
  profile: FormGroup | null = null
  notification: FormGroup | null = null
  accountsettingsObj: NsMiniProfile.IMiniProfileData | null = null
  initialEmail = ''
  uploadPhoto = false
  uploadSaveData = false

  constructor(
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private loader: LoaderService,
    public dialog: MatDialog,
    // private uploadService: UploadService,
    private configSvc: ConfigurationsService,
    private accountSvc: AccountSettingsService,
    private activatedRoute: ActivatedRoute,
  ) {
    if (this.configSvc.userProfile) {
      this.profilePicture =
        (this.accountsettingsObj && this.accountsettingsObj.profile_image) ||
        this.configSvc.userProfile.source_profile_picture ||
        ''
      this.userId = this.configSvc.userProfile.userId || ''
      this.email = this.configSvc.userProfile.email || ''
    }
  }

  states = [
    'Alaska',
    'Alabama',
    'Arkansas',
    'American Samoa',
    'Arizona',
    'California',
    'Colorado',
    'Connecticut',
    'District of Columbia',
    'Delaware',
    'Florida',
    'Georgia',
    'Guam',
    'Hawaii',
    'Iowa',
    'Idaho',
    'Illinois',
    'Indiana',
    'Kansas',
    'Kentucky',
    'Louisiana',
    'Massachusetts',
    'Maryland',
    'Maine',
    'Michigan',
    'Minnesota',
    'Missouri',
    'Northern Mariana Isl',
    'Mississippi',
    'Montana',
    'North Carolina',
    'North Dakota',
    'Nebraska',
    'New Hampshire',
    'New Jersey',
    'New Mexico',
    'Nevada',
    'New York',
    'Ohio',
    'Oklahoma',
    'Oregon',
    'Pennsylvania',
    'Puerto Rico',
    'Rhode Island',
    'South Carolina',
    'South Dakota',
    'Tennessee',
    'Texas',
    'Utah',
    'Virginia',
    'Virgin Islands',
    'Vermont',
    'Washington',
    'Wisconsin',
    'West Virginia',
    'Wyoming',
  ]
  institutes = ['Summer Institute 2018', 'Summer Institute 2019', 'Winter Institute 2020']

  ngOnInit() {
    this.activatedRoute.data.subscribe(
      viewprofile => {
        this.accountsettingsObj = viewprofile.viewprofile.data
        if (this.accountsettingsObj) {
          this.profilePicture = this.accountsettingsObj.profile_image || ''
        }
      },
      _ => {
        this.accountsettingsObj = null
      },
    )

    this.settingsForm = this.formBuilder.group({
      name: [
        this.accountsettingsObj && this.accountsettingsObj.pid_name
          ? this.accountsettingsObj.pid_name
          : this.email,
      ],
      photo: [
        this.accountsettingsObj && this.accountsettingsObj.profile_image
          ? this.accountsettingsObj.profile_image
          : '',
      ],
      checkEmail: [
        this.accountsettingsObj && this.accountsettingsObj.emailprivacy !== null
          ? !this.accountsettingsObj.emailprivacy
          : false,
      ],
      emailId: [
        this.accountsettingsObj && this.accountsettingsObj.email
          ? this.accountsettingsObj.email
          : this.email,
      ],
    })
    this.roles = this.formBuilder.group({
      role: [
        this.accountsettingsObj && this.accountsettingsObj.role ? this.accountsettingsObj.role : '',
        [Validators.required],
      ],
      state: [
        this.accountsettingsObj && this.accountsettingsObj.teaching_state
          ? this.accountsettingsObj.teaching_state
          : '',
        [Validators.required],
      ],
      school: [
        this.accountsettingsObj && this.accountsettingsObj.organization
          ? this.accountsettingsObj.organization
          : '',
      ],
      checkRole: [
        this.accountsettingsObj && this.accountsettingsObj.role_privacy !== null
          ? !this.accountsettingsObj.role_privacy
          : false,
      ],
      checkState: [
        this.accountsettingsObj && this.accountsettingsObj.teaching_state_privacy !== null
          ? !this.accountsettingsObj.teaching_state_privacy
          : false,
      ],
      checkSchool: [
        this.accountsettingsObj && this.accountsettingsObj.organization_privacy !== null
          ? !this.accountsettingsObj.organization_privacy
          : false,
      ],
    })
    this.profile = this.formBuilder.group({
      fb: [
        this.accountsettingsObj &&
          (this.accountsettingsObj.public_profiles &&
          this.accountsettingsObj.public_profiles[0] &&
          this.accountsettingsObj.public_profiles[0].data
            ? this.accountsettingsObj.public_profiles[0].data.replace('https://', '') || ''
            : ''),
      ],
      twitter: [
        this.accountsettingsObj
          ? this.accountsettingsObj.public_profiles &&
            this.accountsettingsObj.public_profiles[1] &&
            this.accountsettingsObj.public_profiles[1].data
            ? this.accountsettingsObj.public_profiles[1].data.replace('https://', '')
            : ''
          : '',
      ],
      linkedin: [
        this.accountsettingsObj
          ? this.accountsettingsObj.public_profiles &&
            this.accountsettingsObj.public_profiles[2] &&
            this.accountsettingsObj.public_profiles[2].data
            ? this.accountsettingsObj.public_profiles[2].data.replace('https://', '')
            : ''
          : '',
      ],
      web: [
        this.accountsettingsObj
          ? this.accountsettingsObj.public_profiles &&
            this.accountsettingsObj.public_profiles[3] &&
            this.accountsettingsObj.public_profiles[3].data
            ? this.accountsettingsObj.public_profiles[3].data.replace('https://', '')
            : ''
          : '',
      ],
      // tel: [this.accountsettingsObj ? (this.accountsettingsObj.phone ? this.accountsettingsObj.phone[0] : '') : ''],
    })

    this.notification = this.formBuilder.group({
      newCourse: [false],
      replyToPost: [false],
      registrationOpen: [false],
    })
  }

  onReset() {
    if (this.settingsForm) {
      this.settingsForm.reset()
    }
  }
  onSubmit(updatedEmail: string) {
    if (this.configSvc.instanceConfig) {
      this.uploadSaveData = true
      const data: NsAccountSettings.IUserMetaTypeData = {
        metaTypeData: updatedEmail,
        rootOrg: this.configSvc.instanceConfig.rootOrg,
      }
      this.accountSvc.updateEmailId(data).subscribe(
        () => {
          this.submitChanges()
        },
        (err: any) => {
          if (err.status === 400) {
            this.submitChanges()
          } else {
            this.uploadSaveData = false
            this.snackBar.open(this.errorUpdatingEmailMessage.nativeElement.value)
          }
        },
      )
    }
  }

  uploadAppIcon(file: File) {
    const formdata = new FormData()
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (!(IMAGE_SUPPORT_TYPES.indexOf(`.${fileName.toLowerCase().split('.').pop()}`) > -1)) {
      this.snackBar.open(this.invalidFormatMessage.nativeElement.value)
      return
    }

    if (file.size > IMAGE_MAX_SIZE) {
      this.snackBar.open('Size greater than allowed')
      return
    }

    const dialogRef = this.dialog.open(ImageCropComponent, {
      width: '70%',
      data: {
        isRoundCrop: true,
        imageFile: file,
      },
    })

    dialogRef.afterClosed().subscribe({
      next: (result: File) => {
        if (result) {
          formdata.append('content', result, fileName)
          this.loader.changeLoad.next(true)
          this.uploadPhoto = true
          this.accountSvc.upload(formdata, this.userId).subscribe(
            data => {
              if (data.code) {
                this.accountSvc.publish(this.userId).subscribe(
                  () => {
                    this.loader.changeLoad.next(false)
                    this.profilePicture = data.artifactURL
                    this.uploadPhoto = false
                  },
                  () => {
                    this.loader.changeLoad.next(false)
                    this.uploadPhoto = false
                  },
                )
              }
            },
            () => {
              this.loader.changeLoad.next(false)
              this.uploadPhoto = false
            },
          )
        }
      },
    })
  }
  changeToDefaultImg($event: any) {
    $event.target.src = this.fileUrl ? this.fileUrl : ''
  }

  submitChanges() {
    if (this.settingsForm && this.roles) {
      this.uploadSaveData = true
      if (
        this.settingsForm.controls.name.value !== '' &&
        this.roles.controls.role.value !== null &&
        this.roles.controls.state.value !== null
      ) {
        this.accountsettingsObj = {
          user_id: this.userId,
          user_name: this.settingsForm.controls.name.value,
          role: this.roles.controls.role.value,
          role_privacy: !this.roles.controls.checkRole.value,
          teaching_state: this.roles.controls.state.value,
          teaching_state_privacy: !this.roles.controls.checkState.value,
          organization: this.roles.controls.school.value || '',
          organization_privacy: !this.roles.controls.checkSchool.value,
          // instituteAttended: [],
          // instituteAttendedPrivacy: false,
          profile_image: this.profilePicture,
          phone: [],
          phone_privacy: false,
          email: this.settingsForm.controls.emailId.value,
          emailprivacy: !this.settingsForm.controls.checkEmail.value,
          public_profiles: [
            {
              field: 'facebook',
              data: this.profile
                ? this.profile.controls.fb.value.indexOf('https://') === 0
                  ? this.profile.controls.fb.value
                  : `https://${this.profile.controls.fb.value}`
                : null,
              privacy: false,
            },
            {
              field: 'twitter',
              data: this.profile
                ? this.profile.controls.twitter.value.indexOf('https://') === 0
                  ? this.profile.controls.twitter.value
                  : `https://${this.profile.controls.twitter.value}`
                : null,
              privacy: false,
            },
            {
              field: 'linkedin',
              data: this.profile
                ? this.profile.controls.linkedin.value.indexOf('https://') === 0
                  ? this.profile.controls.linkedin.value
                  : `https://${this.profile.controls.linkedin.value}`
                : null,
              privacy: false,
            },
            {
              field: 'personalwebsite',
              data: this.profile
                ? this.profile.controls.web.value.indexOf('https://') === 0
                  ? this.profile.controls.web.value
                  : `https://${this.profile.controls.web.value}`
                : null,
              privacy: false,
            },
          ],
        }

        this.accountSvc.accountSettings(this.accountsettingsObj).subscribe(
          () => {
            this.snackBar.open(this.changesUpdatedMessage.nativeElement.value)
            this.uploadSaveData = false
          },
          (err: any) => {
            if (err.status === 405) {
              this.snackBar.open(this.changesUpdatedMessage.nativeElement.value)
              this.uploadSaveData = false
            } else {
              this.uploadSaveData = false
              this.snackBar.open(this.errorUpdatingEmailMessage.nativeElement.value)
            }
          },
        )
      }
    }
    // this.checkEmail= this.settingsForm.controls.checkEmail,
    // this.newCourse= this.notification.controls.newCourse,
    // this.replyToPost= this.notification.controls.replyToPost,
    // this.registrationOpen= this.notification.controls.registrationOpen
  }
  async changePassword() {
    // this.fetchingPasswordUrl = true
    // let token = ''
    if (this.configSvc.instanceConfig) {
      // tslint:disable-next-line: max-line-length
      const passwordUrl = `${this.configSvc.instanceConfig.keycloak.url}/realms/${this.configSvc.instanceConfig.keycloak.realm}/login-actions/reset-credentials?client_id=portal`
      window.open(passwordUrl)
    }
    // const data = await this.accountSvc.getToken().toPromise()
    // token = data.token
    // if (this.configSvc.instanceConfig && this.configSvc.instanceConfig.keycloak.changePasswordUrl) {
    // passwordUrl = `${this.configSvc.instanceConfig.keycloak.changePasswordUrl}/pid/reset-password/${token}`
    // if (passwordUrl) {
    // this.fetchingPasswordUrl = false
    // }
    // }
  }
}
