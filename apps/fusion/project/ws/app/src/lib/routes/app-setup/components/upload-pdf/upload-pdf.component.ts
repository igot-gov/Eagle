import { VIDEO_MAX_SIZE } from '@ws/author/src/lib/constants/upload'
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'
import { UploadPdfService } from './upload-pdf.service'
import { mergeMap } from 'rxjs/operators'
import { Router, ActivatedRoute } from '@angular/router'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-app-upload-pdf',
  templateUrl: './upload-pdf.component.html',
  styleUrls: ['./upload-pdf.component.scss'],
  providers: [UploadPdfService],
})
export class UploadPdfComponent implements OnInit {
  @ViewChild('errorUpdating', { static: true })
  errorUpdatingEmailMessage!: ElementRef<any>
  @ViewChild('invalidFormat', { static: true })
  invalidFormatMessage!: ElementRef<any>
  @ViewChild('sizeGreaterAllowed', { static: true })
  sizeGreaterAllowedMessage!: ElementRef<any>
  @ViewChild('changesUpdated', { static: true })
  changesUpdatedMessage!: ElementRef<any>
  isRequesting = false
  file!: File
  constructor(
    private uploadSvc: UploadPdfService,
    private router: Router,
    private activateRoute: ActivatedRoute,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit() {}

  validation(file: File) {
    if (!file.name.toLowerCase().endsWith('.pdf')) {
      this.snackBar.open(this.invalidFormatMessage.nativeElement.value || 'Invalid Format')
      return
    }

    if (file.size > VIDEO_MAX_SIZE) {
      this.snackBar.open(
        this.invalidFormatMessage.nativeElement.value || 'Size greater than allowed',
      )
      return
    }
    this.file = file
  }
  upload() {
    const formData = new FormData()
    const fileName = this.file.name
      .toLowerCase()
      .replace(/\.[^/.]+$/, '')
      .replace('.pdf', `${new Date().getTime()}.pdf`)
    formData.append('content', this.file, fileName)
    this.isRequesting = true
    this.uploadSvc
      .upload(formData)
      .pipe(
        mergeMap(() =>
          this.uploadSvc.publish().pipe(mergeMap(() => this.uploadSvc.updateEmailId({}))),
        ),
      )
      .subscribe(
        () => {
          this.isRequesting = false
          this.snackBar.open(
            this.changesUpdatedMessage.nativeElement.value || 'Uploaded Successfully',
          )
          const paramsMap = this.activateRoute.snapshot.queryParamMap
          this.router.navigateByUrl(paramsMap.get('ref') || '/page/home')
        },
        () => {
          this.isRequesting = false
          this.snackBar.open(
            this.errorUpdatingEmailMessage.nativeElement.value ||
              'Some error has occurred! Please try after sometime.',
          )
        },
      )
  }
}
