import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core'
import { FormBuilder, Validators, FormControl } from '@angular/forms'
import { FileService } from '../../upload.service'
import { Observable } from 'rxjs'
import { MatSnackBar, MatSort } from '@angular/material'
import { TenantAdminService } from '../../tenant-admin.service'
import { MatPaginator } from '@angular/material/paginator'
import { MatTableDataSource } from '@angular/material/table'
@Component({
  selector: 'ws-admin-user-bulk-upload',
  templateUrl: './user-bulk-upload.component.html',
  styleUrls: ['./user-bulk-upload.component.scss'],
})
export class UserBulkUploadComponent implements OnInit, AfterViewInit {
  private fileName: any
  public displayLoader!: Observable<boolean>
  public formGroup = this.fb.group({
    file: ['', Validators.required],
    department: new FormControl('', [Validators.required]),
  })
  fetching = false
  showFileError = false
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  @ViewChild(MatSort, { static: true }) sort!: MatSort
  bulkUploadData: any
  uplaodSuccessMsg!: string
  dataSource: MatTableDataSource<any>
  displayedColumns: string[] = ['id', 'name', 'status', 'report']
  departments: string[] = []

  objDataSource = new MatTableDataSource<any>()
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator | null = null

  @ViewChild(MatPaginator, { static: false }) set matPaginator(paginator: MatPaginator) {
    this.paginator = paginator
    this.setDataSourceAttributes()
  }
  setDataSourceAttributes() {
    this.dataSource.paginator = this.paginator
  }

  constructor(
    private fb: FormBuilder,
    private fileService: FileService,
    private snackBar: MatSnackBar,
    private tenantAdminSvc: TenantAdminService,
  ) {
    this.dataSource = new MatTableDataSource(this.bulkUploadData)
    this.dataSource.paginator = this.paginator
    this.dataSource.sort = this.sort
  }

  ngOnInit() {
    this.displayLoader = this.fileService.isLoading()
    this.getBulkUploadData()
    this.getUserDepartments()
  }

  ngAfterViewInit() {
      this.dataSource.paginator = this.paginator
  }
  getBulkUploadData() {
    this.fetching = true
    this.tenantAdminSvc.getBulkUploadData().then((res: any) => {
      this.fetching = false
      this.bulkUploadData = res
      this.dataSource = new MatTableDataSource(this.bulkUploadData)
      setTimeout(() => this.dataSource.paginator = this.paginator)
    })
      .catch(() => { })
      .finally(() => {
        this.fetching = false
      })
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
      if (this.formGroup && this.formGroup.get('file') && this.formGroup.get('department')) {
        // tslint:disable-next-line: no-non-null-assertion
        this.fileService.upload(this.fileName, this.formGroup!.get('file')!.value, this.formGroup!.get('department')!.value)
          .subscribe(res => {
            // this.uplaodSuccessMsg = res
            this.openSnackbar(res)
            this.getBulkUploadData()
          })
      }
    } else {
      this.showFileError = true
      this.openSnackbar(this.toastError.nativeElement.value)
    }
  }

  public refreshTable() {
    this.getBulkUploadData()
  }
  public downloadFile(): void {
    this.fileService.download()
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  public downloadReport(row: any) {
    this.fileService.downloadReport(row.id, row.name)
  }

  getUserDepartments() {
    this.tenantAdminSvc.getUserDepartments().then((res: any) => {
      this.departments = res
    })
      .catch(() => {
       })
      .finally(() => {
      })
  }

}
