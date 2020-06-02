import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core'
import { MatSnackBar, MatDatepicker } from '@angular/material'
import {
  IChannelHub,
  IChannelHubCard,
} from '@ws-widget/collection/src/lib/channel-hub/channel-hub.model'
import { FormGroup, FormBuilder, FormArray, AbstractControl, FormControl } from '@angular/forms'
import { debounceTime, distinctUntilChanged } from 'rxjs/operators'
import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { NOTIFICATION_TIME } from '@ws/author/src/lib/constants/constant'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { FILE_MAX_SIZE } from '@ws/author/src/lib/constants/upload'
import {
  AUTHORING_CONTENT_BASE,
  CONTENT_BASE_WEBHOST_ASSETS,
} from '@ws/author/src/lib/constants/apiEndpoints'
import { UploadService } from '@ws/author/src/lib/routing/modules/editor/shared/services/upload.service'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import * as _moment from 'moment'
// tslint:disable-next-line:no-duplicate-imports
import { default as _rollupMoment, Moment } from 'moment'
const moment = _rollupMoment || _moment

import {
  MomentDateAdapter,
  MAT_MOMENT_DATE_ADAPTER_OPTIONS,
} from '@angular/material-moment-adapter'
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core'

export const MY_FORMATS = {
  parse: {
    dateInput: 'MM/YYYY',
  },
  display: {
    dateInput: 'MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
}

@Component({
  selector: 'ws-auth-channel-hub',
  templateUrl: './channel-hub.component.html',
  styleUrls: ['./channel-hub.component.scss'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS],
    },

    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
  ],
})
export class ChannelHubComponent implements OnInit {
  @Input() identifier = ''
  @Input() content!: IChannelHub
  @Input() isSubmitPressed = false
  @Output() data = new EventEmitter<{ content: IChannelHub; isValid: boolean }>()
  form!: FormGroup
  checked = false
  date = new FormControl(moment())
  constructor(
    private formBuilder: FormBuilder,
    private uploadService: UploadService,
    private snackBar: MatSnackBar,
    public loader: LoaderService,
  ) {}

  ngOnInit() {
    this.form = this.formBuilder.group({
      label: [this.content.label || ''],
      cards: this.formBuilder.array([]),
    })
    if (this.content && this.content.cards && this.content.cards.length > 0) {
      this.content.cards.map(v => this.addChannelsToForm(v))
    } else {
      this.addChannelsToForm()
    }

    this.form.valueChanges.pipe(debounceTime(100), distinctUntilChanged()).subscribe({
      next: () => {
        this.data.emit({
          content: this.form.value,
          isValid: this.form.valid,
        })
      },
    })
  }

  get paths(): any {
    return this.form.get('cards') as FormArray
  }

  addChannelsToForm(data?: IChannelHubCard) {
    this.paths.push(
      this.formBuilder.group({
        description: [data ? data.description || '' : ''],
        endDate: [data ? data.endDate || '' : ''],
        image: [data ? data.image || '' : ''],
        name: [data ? data.name || '' : ''],
        startDate: [data ? data.startDate || '' : ''],
        url: [data ? data.url || '' : ''],
        disable: [data ? data.disable || false : false],
      }),
    )
    // if (data) {
    //   if (data.startDate) {
    //     startDate = moment(new Date(data.startDate))
    //   }
    //   if (data.endDate) {
    //     endDate = moment(new Date(data.endDate))
    //   }
    // }
  }

  remove(index: number) {
    this.paths.removeAt(index)
  }

  // toggleChange(index: number) {
  //   this.checked = !this.checked
  //   // const formControl = this.paths.at(index).get('disable') as AbstractControl
  //   // formControl.setValue(this.checked)
  // }

  upload(file: File, index: number) {
    const formControl = this.paths.at(index).get('image') as AbstractControl
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
      .subscribe(
        data => {
          if (data.code) {
            this.loader.changeLoad.next(false)
            formControl.setValue(
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

  chosenYearHandler(normalizedYear: Moment, index: number, startDate = false) {
    const ctrlValue = this.date.value
    ctrlValue.year(normalizedYear.year())
    if (startDate) {
      this.paths
        .at(index)
        .get('startDate')
        .setValue(JSON.parse(JSON.stringify(ctrlValue)))
    } else {
      this.paths
        .at(index)
        .get('endDate')
        .setValue(JSON.parse(JSON.stringify(ctrlValue)))
    }
  }

  chosenMonthHandler(
    normalizedMonth: Moment,
    datepicker: MatDatepicker<Moment>,
    index: number,
    startDate = false,
  ) {
    const ctrlValue = this.date.value
    ctrlValue.month(normalizedMonth.month())
    if (startDate) {
      this.paths
        .at(index)
        .get('startDate')
        .setValue(JSON.parse(JSON.stringify(ctrlValue)))
    } else {
      this.paths
        .at(index)
        .get('endDate')
        .setValue(JSON.parse(JSON.stringify(ctrlValue)))
    }
    datepicker.close()
  }
}
