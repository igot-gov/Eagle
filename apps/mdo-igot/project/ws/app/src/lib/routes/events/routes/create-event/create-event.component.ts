import { Component, OnInit, Input, Output, EventEmitter, ViewChild, ChangeDetectorRef } from '@angular/core'
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { EventsService } from '../../services/events.service'
import { MatSnackBar, MatPaginator } from '@angular/material'
import { MatSort } from '@angular/material/sort'
import { ITableData } from '../../interfaces/interfaces'
import { MatDialog } from '@angular/material/dialog'
import { ParticipantsComponent } from '../../components/participants/participants.component'
import { SuccessComponent } from '../../components/success/success.component'
import { Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils'
import * as moment from 'moment'

@Component({
  selector: 'ws-app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss'],
})
export class CreateEventComponent implements OnInit {

  artifactURL: any
  participantsArr: any = []
  presentersArr: any = []
  displayedColumns: string[] = ['fullname', 'email', 'type']
  @Input() tableData!: ITableData | undefined
  @Input() data?: []
  @Input() isUpload?: boolean
  @Input() isCreate?: boolean

  @Output() clicked?: EventEmitter<any>
  @Output() actionsClick?: EventEmitter<any>
  @Output() eOnRowClick = new EventEmitter<any>()
  @Output() eOnCreateClick = new EventEmitter<any>()

  createEventForm: FormGroup
  namePatern = `^[a-zA-Z\\s\\']{1,32}$`
  department: any = {}
  departmentName = ''
  toastSuccess: any
  pictureObj: any
  myreg = /(^|\s)((https?:\/\/)?[\w-]+(\.[\w-]+)+\.?(:\d+)?(\/\S*)?)/gi

  eventTypes = [
    { title: 'Webinar', desc: 'General discussion involving', border: 'rgb(0, 116, 182)', disabled: false },
    // { title: 'Ask me anything', desc: 'Session targeted at answering questions from attendees', border: '', disabled: true },
    // { title: 'Workshop', desc: 'Live learning session', border: '', disabled: true },
    // { title: 'Interview', desc: 'Interview session involving one or more guests', border: '', disabled: true },
  ]

  timeArr = [
    { value: '00:00' }, { value: '00:30' }, { value: '01:00' }, { value: '01:30' },
    { value: '02:00' }, { value: '02:30' }, { value: '03:00' }, { value: '03:30' },
    { value: '04:00' }, { value: '04:30' }, { value: '05:00' }, { value: '05:30' },
    { value: '06:00' }, { value: '06:30' }, { value: '07:00' }, { value: '07:30' },
    { value: '08:00' }, { value: '08:30' }, { value: '09:00' }, { value: '09:30' },
    { value: '10:00' }, { value: '10:30' }, { value: '11:00' }, { value: '11:30' },
    { value: '12:00' }, { value: '12:30' }, { value: '13:00' }, { value: '13:30' },
    { value: '14:00' }, { value: '14:30' }, { value: '15:00' }, { value: '15:30' },
    { value: '16:00' }, { value: '16:30' }, { value: '17:00' }, { value: '17:30' },
    { value: '18:00' }, { value: '18:30' }, { value: '19:00' }, { value: '19:30' },
    { value: '20:00' }, { value: '20:30' }, { value: '21:00' }, { value: '21:30' },
    { value: '22:00' }, { value: '22:30' }, { value: '23:00' }, { value: '23:30' },
  ]

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator
  @ViewChild(MatSort, { static: true }) sort?: MatSort

  dataSource!: any
  widgetData: any
  length!: number
  pageSize = 5
  pageSizeOptions = [5, 10, 20]
  dialogRef: any
  activeUsers: any
  imageSrc: any
  imageSrcURL: any
  tabsData!: any[]
  currentTab = 'eventInfo'
  userId: any
  username: any
  minDate: any
  maxDate: any
  todayDate: any
  todayTime: any

  constructor(private snackBar: MatSnackBar,
              private eventsSvc: EventsService,
              private matDialog: MatDialog,
              private router: Router,
              private configSvc: ConfigurationsService,
              private changeDetectorRefs: ChangeDetectorRef
              ) {
    if (this.configSvc.userProfile) {
      this.userId = this.configSvc.userProfile.userId
      this.username = this.configSvc.userProfile.userName
      this.department = this.configSvc.userProfile.departmentName
    }
    this.createEventForm = new FormGroup({
      eventPicture: new FormControl('', [Validators.required]),
      eventTitle: new FormControl('', [Validators.required]),
      summary: new FormControl('', [Validators.required]),
      description: new FormControl('', [Validators.required]),
      agenda: new FormControl('', [Validators.required]),
      eventType: new FormControl('', [Validators.required]),
      eventDate: new FormControl('', [Validators.required]),
      eventTime: new FormControl('', [Validators.required]),
      eventDurationHours: new FormControl('', [Validators.required]),
      eventDurationMinutes: new FormControl('', [Validators.required]),
      conferenceLink: new FormControl('', [Validators.required, Validators.pattern(this.myreg)]),
      presenters: new FormControl('', [Validators.required]),
    })

    this.createEventForm.controls['eventDurationHours'].setValue(0)
    this.createEventForm.controls['eventDurationMinutes'].setValue(30)
    this.createEventForm.controls['eventType'].setValue('Webinar')
    const minCurrentDate = new Date()
    const maxNewDate = new Date()
    this.minDate = minCurrentDate
    this.maxDate  = maxNewDate.setMonth(maxNewDate.getMonth() + 1)
    this.todayDate = new Date((new Date().getTime()))
    this.todayTime = '00:00'
  }

  ngOnInit() {

    this.tabsData = [
      {
        name: 'Event details',
        key: 'eventInfo',
        render: true,
        enabled: true,
      },
      {
        name: 'Date and time',
        key: 'datetime',
        render: true,
        enabled: true,
      },
      {
        name: 'Video conferencing',
        key: 'videoinfo',
        render: true,
        enabled: true,
      },
      {
        name: 'Presenters',
        key: 'presenter',
        render: true,
        enabled: true,
      }]
  }

  onSideNavTabClick(id: string) {
    this.currentTab = id
    const el = document.getElementById(id)
    if (el != null) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'start' })
    }
  }

  openDialog() {
    this.dialogRef = this.matDialog.open(ParticipantsComponent, {
      width: '850px',
      height: '600px',
    })
    this.dialogRef.afterClosed().subscribe((response: any) => {
        if (response) {
          this.addPresenters(response)
        }
    })
  }

  addPresenters(responseObj: any) {
      Object.keys(responseObj.data).forEach((index: any) => {
      const obj = responseObj.data[index]
        const setSelectedPresentersObj = {
          firstname: obj.firstname,
          lastname: obj.lastname,
          email: obj.email,
          type: 'Karmayogi User',
        }
        const contactsObj = {
          id: obj.id,
          name: `${obj.firstname} ${obj.lastname}`,
        }
        this.presentersArr.push(contactsObj)
        this.participantsArr.push(setSelectedPresentersObj)
        this.changeDetectorRefs.detectChanges()
        this.createEventForm.controls['presenters'].setValue(this.presentersArr)
      })
  }

  close() {
    this.dialogRef.close()
  }

  selectCover() {
    this.pictureObj = document.getElementById('coverPicture')
    this.pictureObj.click()
  }

  onFileSelect(event: any) {
    if (event.target.files.length > 0) {
      const reader = new FileReader()
      const file = event.target.files[0]
      reader.onload = () => this.imageSrcURL = reader.result
      reader.readAsDataURL(file)
      this.imageSrc = file
      this.createEventForm.controls['eventPicture'].setValue(this.imageSrc)
    }
  }

  removeSelectedFile() {
    this.imageSrcURL = ''
    this.createEventForm.controls['eventPicture'].setValue('')
  }

    fileSubmit(identifier: string) {
      const formData = new FormData()
      formData.append('file', this.imageSrc)
      this.eventsSvc.uploadCoverImage(formData, identifier).subscribe(
        res => {
          this.artifactURL = res.artifactURL
          this.updateContent(identifier)
        },
        (err: any) => {
          this.openSnackbar(err.error.split(':')[1])
        }
      )
    }

    changeEventType(event: any) {
     this.createEventForm.controls['eventType'].setValue(event.target.value)
    }

    updateContent(identifier: any) {
      const contentObj = {
        nodesModified: {
          [identifier]: {
              isNew: false,
              root: true,
              metadata: {
                appIcon: this.artifactURL,
              },
          },
        },
        hierarchy: {
        },
      }
      const formJson = this.encodeToBase64(contentObj)
      this.eventsSvc.updateEvent(formJson).subscribe(
        res => {
          if (res || !res) {
            this.publishEvent(identifier)
          }
        },
        (err: any) => {
          this.openSnackbar(err.error.split(':')[1])
        }
      )
    }

    onSubmit() {
      const eventDurationMinutes = this.addMinutes(
         this.createEventForm.controls['eventDurationHours'].value,
         this.createEventForm.controls['eventDurationMinutes'].value
      )
      const timeArr = this.createEventForm.controls['eventTime'].value.split(':')
      const expiryDateTime = moment(this.createEventForm.controls['eventDate'].value)
        .set('hour', timeArr[0])
        .set('minute', timeArr[1]).format('YYYYMMDDTHHmmss+0000')
      const form = {
        content: {
          contentType: 'Event',
          mimeType: 'application/html',
          locale: 'en',
          isExternal: true,
          name: this.createEventForm.controls['eventTitle'].value,
          description: this.createEventForm.controls['summary'].value,
          category: 'Event',
          createdBy: this.userId,
          authoringDisabled: false,
          isContentEditingDisabled: false,
          isMetaEditingDisabled: false,
          learningObjective: this.createEventForm.controls['agenda'].value,
          expiryDate: expiryDateTime,
          duration: eventDurationMinutes,
          artifactUrl: this.createEventForm.controls['conferenceLink'].value,
          resourceType: this.createEventForm.controls['eventType'].value,
          categoryType: 'Article',
          creatorDetails: this.createEventForm.controls['presenters'].value,
          sourceName: this.department,
        },
      }
      const formJson = this.encodeToBase64(form)
      this.eventsSvc.createEvent(formJson).subscribe(
        res => {
          const identifier = res.identifier
          this.fileSubmit(identifier)
        },
        (err: any) => {
          this.openSnackbar(err.error.split(':')[1])
        }
      )
    }

    encodeToBase64 (body: any) {
      const sString = JSON.stringify(body)
      const aUTF16CodeUnits = new Uint16Array(sString.length)
      Array.prototype.forEach.call(aUTF16CodeUnits, (_el, idx, arr) => arr[idx] = sString.charCodeAt(idx))
      return { data: btoa(new Uint8Array(aUTF16CodeUnits.buffer).reduce((data, byte) => data + String.fromCharCode(byte), '')) }
    }

    private openSnackbar(primaryMsg: string, duration: number = 5000) {
      this.snackBar.open(primaryMsg, 'X', {
        duration,
      })
    }

    addMinutes(hrs: number, mins: number) {
      const minutes = (hrs * 60) + mins
      return minutes
    }

    publishEvent(identifierkey: any) {
        const requestObj = {
          actor: this.userId,
          comment: 'done',
          operation: 1,
          org: 'dopt',
          rootOrg: 'igot',
          appName: 'iGoT',
          appUrl: 'https://d136953gtttd92.cloudfront.net',
          actorName: this.username,
          action: 'publisherApproved',
        }
        const formJson = this.encodeToBase64(requestObj)
        this.eventsSvc.publishEvent(formJson, identifierkey).subscribe(
          res => {
            this.showSuccess(res)
          },
          (err: any) => {
            this.openSnackbar(err.error.split(':')[1])
          }
        )
      }

    goToList() {
      this.router.navigate([`/app/events`])
    }

    showSuccess(res: any) {
      this.dialogRef = this.matDialog.open(SuccessComponent, {
        width: '630px',
        height: '520px',
        data: res,
      })
      this.dialogRef.afterClosed().subscribe(() => {
        this.router.navigate([`/app/events`])
      })
    }

  omit_special_char(event: any) {
    const k = event.charCode
    return((k > 64 && k < 91) || (k > 96 && k < 123) || k === 8 || k === 32 || (k >= 48 && k <= 57))
  }
}
