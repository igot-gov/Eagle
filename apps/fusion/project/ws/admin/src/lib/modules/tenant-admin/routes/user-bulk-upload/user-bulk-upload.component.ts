import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'
import { FormBuilder, Validators } from '@angular/forms'
import { FileService } from '../../upload.service'
import { Observable } from 'rxjs'
import { MatSnackBar } from '@angular/material'
@Component({
  selector: 'ws-admin-user-bulk-upload',
  templateUrl: './user-bulk-upload.component.html',
  styleUrls: ['./user-bulk-upload.component.scss'],
})
export class UserBulkUploadComponent implements OnInit {
  private fileName: any
  public displayLoader!: Observable<boolean>
  public formGroup = this.fb.group({
    file: ['', Validators.required],
  })
  showFileError = false
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>

  constructor(
    private fb: FormBuilder,
    private fileService: FileService,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit() {
    this.displayLoader = this.fileService.isLoading()
  }

  public onFileChange(event: any) {
    this.showFileError = false
    const reader = new FileReader()
    if (event.target.files && event.target.files.length) {
      this.fileName = event.target.files[0].name
      const [file] = event.target.files
      reader.readAsDataURL(file)

      reader.onload = () => {
        this.formGroup.patchValue({
          file: reader.result,
        })
      }
    }
  }

  public onSubmit(): void {
    // Validate File type before uploading
    if (this.fileService.validateFile(this.fileName)) {
      if (this.formGroup && this.formGroup.get('file')) {
        // tslint:disable-next-line: no-non-null-assertion
        this.fileService.upload(this.fileName, this.formGroup!.get('file')!.value)
      }
      this.openSnackbar(this.toastSuccess.nativeElement.value)
    } else {
      this.showFileError = true
      this.openSnackbar(this.toastError.nativeElement.value)
    }
  }

  public downloadFile(): void {
    this.fileService.download()
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, undefined, {
      duration,
    })
  }

}
