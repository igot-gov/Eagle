import { Component, OnInit, ViewChild } from '@angular/core'
import { MatDialog, MatSnackBar, MatTableDataSource, MatPaginator } from '@angular/material'
import { ActivatedRoute, Router } from '@angular/router'
// import { NsAutoComplete } from '@ws-widget/collection'
import { IManageUser, IUserRoleDetail, IUsersRole } from '../system-roles-management.model'
import { SystemRolesManagementService } from '../system-roles-management.service'
import { ConfirmActionComponent } from './components/confirm-action/confirm-action.component'
import { AddUsersDialogComponent } from './components/add-users-dialog/add-users-dialog.component'
import { ValueService, ConfigurationsService } from '@ws-widget/utils'
import { FormControl } from '@angular/forms'

@Component({
  selector: 'ws-admin-roles-management-detail',
  templateUrl: './roles-management-detail.component.html',
  styleUrls: ['./roles-management-detail.component.scss'],
})
export class RolesManagementDetailComponent implements OnInit {
  @ViewChild('paginator', { static: true }) paginator: MatPaginator | null = null
  showInstruction = false
  roleList!: string[]
  role = ''
  displayedColumns: string[] = ['checkbox', 'firstName', 'lastName', 'depName', 'email', 'mat-icon']
  removeTable: IUserRoleDetail[] = []
  removeSource = new MatTableDataSource(this.removeTable)
  addUserId: string[] = []
  removeUserId: string[] = []
  userDataFetched = false
  isAdding = false
  rolesHash!: { [key: string]: string }
  width = '60vw'
  allSelected = false
  removeUserArray: IUserRoleDetail[] = []
  removeBody!: IManageUser
  pageSize = 50
  currentIndex = 0
  pageSelection: { [key: number]: boolean } = {}
  queryControl = new FormControl()
  isDark = this.configSvc.isDarkMode

  constructor(
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    public rolesSvc: SystemRolesManagementService,
    private valueSvc: ValueService,
    private configSvc: ConfigurationsService,
  ) {
    if (this.activatedRoute.parent && this.activatedRoute.parent.parent) {
      this.activatedRoute.parent.parent.data.subscribe(data => {
        const featureData = data.featureData.data
        this.rolesHash = featureData.roleList
        this.roleList = Object.keys(this.rolesHash || {})
      })
    }
  }

  async ngOnInit() {
    if (this.activatedRoute.snapshot.queryParamMap.has('role')) {
      this.role = this.activatedRoute.snapshot.queryParamMap.get('role') || ''
      if (!this.roleList.includes(this.role)) {
        this.router.navigate(['/admin/tenant/system-roles-management'])
      }
      await this.fetchUserData(this.role)
    } else {
      this.router.navigate(['/admin/tenant/system-roles-management'])
    }
    this.valueSvc.isLtMedium$.subscribe(data => {
      if (data) {
        this.width = '100vw'
      } else {
        this.width = '40vw'

      }
    })

  }
  async fetchUserData(role: string) {
    this.removeSource.paginator = null
    this.userDataFetched = false
    this.removeTable = []
    await this.rolesSvc.getAllUser(role).then((data: IUsersRole) => {
      data.users.forEach((userData: IUserRoleDetail) => {
        if (userData.wid) {
          this.removeTable.push({ ...userData, isSelected: false, isLoading: false })
        }
      })
      this.userDataFetched = true
      this.removeSource = new MatTableDataSource(this.removeTable)
      if (this.paginator) {
        this.paginator.firstPage()
      }
      this.removeSource.paginator = this.paginator
      this.pageSelection[this.currentIndex] = false
    }).catch(() => {
        this.userDataFetched = true
      this.snackBar.open('Error occured', undefined, { duration: 3000 })
    })
    this.queryControl.valueChanges.subscribe(val => {
      this.removeSource.filter = val.trim().toLowerCase()
        if (this.queryControl.value.length) {
          this.removeSource.paginator = null
        } else {
          this.removeSource.paginator = this.paginator
        }
      }
    )
  }

  handlePage(event: any) {
    this.currentIndex = Number(event.pageIndex)
    if (typeof(this.pageSelection[this.currentIndex]) === 'undefined') {
      this.pageSelection[this.currentIndex] = false
    }
  }
  removeUser(wid: string) {
    if (this.role) {
      const removeBody: IManageUser = {
          users: [wid],
          operation: 'remove',
          roles: [this.role],
        }
      this.rolesSvc.manageUser(removeBody).then(
          () => {
            this.queryControl.setValue('')
            this.removeTable = this.removeTable.filter(data => data.wid !== wid)
            this.removeSource = new MatTableDataSource(this.removeTable)
            this.removeSource.paginator = this.paginator
            this.snackBar.open(`User removed with ${this.role} role`, undefined, { duration: 3000 })
          }).catch(
          () => {
            this.removeTable.forEach(data => {
                data.isLoading = false
            })
            this.removeSource = new MatTableDataSource(this.removeTable)
            this.removeSource.paginator = this.paginator
            this.snackBar.open('Error occured', undefined, { duration: 3000 })
          }
        )
    }
  }
  selectedUser() {
    if (this.pageSelection[this.currentIndex]) {
      this.pageSelection[this.currentIndex] = false
    }
    this.removeUserId = this.removeTable.filter(data => data.isSelected).map(data => {
      return data.wid
    })
  }

  selectAll() {
    if (this.removeTable.length > 0) {
      this.removeTable.forEach((data, index) => {
        if ((index >= (this.currentIndex * this.pageSize)) && (index < (this.currentIndex + 1) * this.pageSize)) {
          data.isSelected = this.pageSelection[this.currentIndex]
        }
      })
      this.removeUserId = this.removeTable.filter(data => data.isSelected).map(data => {
        return data.wid
      })
    }
  }

  openAddUser() {
    const id: string[] = []
    this.removeTable.forEach(data => {
      id.push(data.wid)
    })
    const dialogRef = this.dialog.open(AddUsersDialogComponent, {
      width: this.width,
      data: {
        role: this.role,
        userIds: id,
      },
    })
    dialogRef.afterClosed().subscribe(() => {
       this.fetchUserData(this.role)
    })
  }

  confirmDelete() {
    const dialogRef = this.dialog.open(ConfirmActionComponent, {
      width: this.width,
      data: {
       userId: this.removeUserId,
       userData: this.removeTable.filter(data => this.removeUserId.includes(data.wid)),
       role: this.role,
      },
    })
    dialogRef.afterClosed().subscribe(data => {
      if (data && data === 'removed') {
        this.queryControl.setValue('')
        this.removeUserId = []
        this.pageSelection = {}
        this.fetchUserData(this.role)
      }
    })
  }

}
