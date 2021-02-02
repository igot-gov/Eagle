import { DirectoryService } from './../directory/directory.services'
import { Component, OnInit, Input } from '@angular/core'
import { FormGroup, FormControl } from '@angular/forms'
import { MatDialog, MatSnackBar } from '@angular/material'
import { UserPopupComponent } from '../user-popup/user-popup'

import { LoaderService } from '../../services/loader.service'
import { AuthInitService } from '../../services/init.service'
import { ActivatedRoute, Router } from '@angular/router'
import { CreateMDOService } from './create-mdo.services'
import { ValueService } from '../../../../../../../../../library/ws-widget/utils/src/lib/services/value.service'
import { NsWidgetResolver } from '../../../../../../../../../library/ws-widget/resolver/src/public-api'
import { ILeftMenu } from '../../../../../../../../../library/ws-widget/collection/src/public-api'
import { map } from 'rxjs/operators'
interface IUser { userId: string, fullName: string; email: string; role: string }
@Component({
  selector: 'ws-app-create-mdo',
  providers: [AuthInitService, LoaderService],
  templateUrl: './create-mdo.component.html',
  styleUrls: ['./create-mdo.component.scss'],
})
export class CreateMdoComponent implements OnInit {
  @Input() isSubmitPressed = false
  @Input() nextAction = 'done'
  @Input() stage = 1
  @Input() type = ''
  fracData: any = []
  contentForm!: FormGroup

  tabledata: any = []
  data: any = []
  canUpdate = true
  isEditEnabled = false
  canExpiry = true
  mdoId: number | undefined

  fetchTagsStatus: 'done' | 'fetching' | null = null
  complexityLevelList: any
  editorService: any
  submittedForm = true
  userId!: string
  departmentId!: string
  departmentRole!: string
  data1: any
  updateId !: number
  department!: string
  widgetData!: NsWidgetResolver.IWidgetData<ILeftMenu>
  sideNavBarOpened = true
  isFromDirectory = false
  private bannerSubscription: any
  public screenSizeIsLtMedium = false

  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  subDepartments!: any
  subMDODepartments!: any
  private defaultSideNavBarOpenedSubscription: any

  isUpdate = false
  isAddAdmin = false
  workFlow = [{ isActive: true, isCompleted: false, name: 'Basic Details', step: 0 },
  { isActive: false, isCompleted: false, name: 'Classification', step: 1 },
  { isActive: false, isCompleded: false, name: 'Intended for', step: 2 }]
  constructor(public dialog: MatDialog,
              private snackBar: MatSnackBar,
              private createMdoService: CreateMDOService,
              private router: Router,
              private directoryService: DirectoryService,
              private valueSvc: ValueService,
              private activatedRoute: ActivatedRoute) {
    {

      this.contentForm = new FormGroup({
        name: new FormControl(),
        head: new FormControl(),
        deptSubTypeId: new FormControl(),
        deptMdoSubTypeId: new FormControl(),
      })
      this.activatedRoute.params.subscribe(params => {
        let data = params['data']
        this.department = params['department']
        this.isFromDirectory = params['isFromDirectory']
        this.isAddAdmin = params['addAdmin']
        if (this.isAddAdmin) {
          this.submittedForm = false
          this.isUpdate = true
          this.department = params['currentDept']
          this.departmentId = params['department']
          // this.departmentRole = `${params['currentDept']} ADMIN`
          this.departmentRole = `MDO ADMIN`
        }
        data = JSON.parse(data)

        if (this.data !== undefined || this.data !== null) {
          this.isUpdate = true
          this.updateId = data.row.id
          this.getMdoSubDepartment(this.updateId)
        }
        this.contentForm = new FormGroup({
          name: new FormControl(data.row.mdo),
          head: new FormControl(data.row.head),
          deptSubTypeId: new FormControl(data.row.typeid),
          deptMdoSubTypeId: new FormControl(),
        })
      })
    }
  }
  getMdoSubDepartment(updateId: number) {
    this.directoryService.getAllDepartments().subscribe(res => {
      res.forEach((dept: { id: number, deptTypeInfos: any }) => {
        if (updateId === dept.id) {
          dept.deptTypeInfos.forEach((subId: any) => {
            if (subId.deptType === 'MDO') {

              this.contentForm.controls.deptMdoSubTypeId.patchValue(subId.id)
            }
          })
        }
      })
    })

  }
  ngOnInit() {
    this.getAllDepartmentsAPI()
    this.getAllMDODepartmentsAPI()
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
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe(isLtMedium => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })
  }
  getAllDepartmentsAPI() {
    this.createMdoService.getAllSubDepartments(this.department).subscribe(res => {
      this.subDepartments = res
    })
  }
  getAllMDODepartmentsAPI() {
    this.createMdoService.getAllSubDepartments('MDO').subscribe(res => {
      this.subMDODepartments = res
    })
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
  openPopup() {
    const dialogRef = this.dialog.open(UserPopupComponent, {
      maxHeight: 'auto',
      height: '65%',
      width: '80%',
      panelClass: 'remove-pad',
    })
    dialogRef.afterClosed().subscribe((response: any) => {
      this.data = this.getAllResponse(response)
      this.data.forEach((element: { userId: string }) => {
        this.createMdoService.assignAdminToDepartment(element.userId, this.departmentId, this.departmentRole).subscribe(res => {
          if (res) {
            this.snackBar.open('Admin assigned Successfully')
            this.router.navigate(['/app/home/directory', { department: this.department }])
          }
        },                                                                                                              (err: { error: any }) => {
          this.openSnackbar(err.error.errors[0].message)
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
          role: 'ADMIN',
        }
        tempArray.push(obj)
      })
      return tempArray
    }
    return []
  }
  onSubmit() {
    if (!this.isUpdate) {

      if (this.contentForm.value.name !== null && this.contentForm.value.head !== null
        && this.contentForm.value.deptSubTypeId !== null) {
        const deptArr = []
        const subdepartment = this.getSubDepartmennt(this.contentForm.value.deptSubTypeId)
        deptArr.push(subdepartment)
        if (this.department !== 'MDO') {
          const subMDOdepartment = this.getMdoSubDepartmennt(this.contentForm.value.deptMdoSubTypeId)
          deptArr.push(subMDOdepartment)
        }
        this.createMdoService.createDepartment(this.contentForm.value, deptArr).subscribe(res => {
          this.departmentId = res.id
          this.departmentRole = this.getRole()
          if (this.departmentId && this.departmentRole) {
            this.submittedForm = false
            this.openSnackbar('Success')
          }
        },                                                                                (err: { error: any }) => {
          this.openSnackbar(err.error.errors[0].message)
        })

      }
    } else {
      if (this.contentForm.value.name !== null && this.contentForm.value.head !== null
        && this.contentForm.value.deptSubTypeId !== null) {
        const deptArr = []
        const subdepartment = this.getSubDepartmennt(this.contentForm.value.deptSubTypeId)
        deptArr.push(subdepartment)
        if (this.department !== 'MDO') {
          const subMDOdepartment = this.getMdoSubDepartmennt(this.contentForm.value.deptMdoSubTypeId)
          deptArr.push(subMDOdepartment)
        }
        this.createMdoService.updateDepartment(this.contentForm.value, this.updateId, deptArr).subscribe(res => {
          this.departmentId = res.id
          this.departmentRole = this.getRole()
          if (this.departmentId && this.departmentRole) {
            this.openSnackbar('Success')
            this.router.navigate(['/app/home/directory', { department: this.department }])

          }
        },                                                                                               (err: { error: any }) => {
          this.openSnackbar(err.error.errors[0].message)
        })

      }
    }

  }
  getSubDepartmennt(id: number) {
    let obj
    this.subDepartments.forEach((element: any) => {
      if (element.id === id) {
        if (this.isUpdate) {
          obj = element.id
        } else {
          obj = element
        }

      }
    })
    return obj
  }
  getMdoSubDepartmennt(id: number) {
    let obj
    this.subMDODepartments.forEach((element: any) => {
      if (element.id === id) {
        if (this.isUpdate) {
          obj = element.id
        } else {
          obj = element
        }
      }
    })
    return obj
  }
  onCancel() {
    this.router.navigate(['/app/home/directory', { department: this.department }])
  }
  getRole() {
    return 'MDO ADMIN'
  }
  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  capitalizeFirstLetter(uppercaseString: string) {
    return uppercaseString.charAt(0).toUpperCase() + uppercaseString.slice(1)
  }
  onDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
    if (this.bannerSubscription) {
      this.bannerSubscription.unsubscribe()
    }
  }

}
