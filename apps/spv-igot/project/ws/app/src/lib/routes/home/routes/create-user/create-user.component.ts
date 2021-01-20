import { Component, OnInit } from '@angular/core'
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { UsersService } from '../../services/users.service'
import { MatSnackBar } from '@angular/material'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss'],
})
export class CreateUserComponent implements OnInit {
  createUserForm: FormGroup
  namePatern = `^[a-zA-Z\\s\\']{1,32}$`
  rolesList: any = []
  departmentName = ''
  toastSuccess: any
  departmentoptions:  any = []
  dropdownSettings = {}
  selectedDept: any
  public userRoles: Set<string> = new Set()

  constructor(private router: Router,
              private snackBar: MatSnackBar,
              private usersSvc: UsersService) {
    this.createUserForm = new FormGroup({
      fname: new FormControl('', [Validators.required]),
      lname: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      department: new FormControl('', [Validators.required]),
      roles: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit() {
    this.getAllDept()

    this.dropdownSettings = {
      singleSelection: true,
      idField: 'id',
      textField: 'deptName',
      enableCheckAll: false,
      itemsShowLimit: 10000,
      allowSearchFilter: true,
    }
  }

  getAllDept() {
    this.usersSvc.getAllDepartments().subscribe(res => {
      this.departmentoptions = res
    })
  }

  /** methods related to Dropdown */
  onItemSelect(item: any[]) {
    // if (this.selectedDept.indexOf(item.id) === -1) {
    //   this.selectedDept.push(item)
    //   this.departmentoptions.forEach(dept => {
    //     if (dept.id === item.id) {
    //       this.rolesList = dept.rolesInfo
    //     }
    //   })
    // }
    this.selectedDept = item
    this.departmentoptions.forEach((dept: any) => {
      if (dept.id === this.selectedDept.id) {
        this.rolesList = dept.rolesInfo
      }
    })
  }

  /**On unselecting the option */
  onItemDeSelect() {
    // const index = this.selectedDept.map((x: any) => {
    //   return x.id
    // }).indexOf(item.item_id)
    // this.selectedDept.splice(index, 1)
    this.selectedDept = ''
    this.createUserForm.value.department = ''
  }

  modifyUserRoles(role: string) {
    if (this.userRoles.has(role)) {
      this.userRoles.delete(role)
    } else {
      this.userRoles.add(role)
    }
  }
  onSubmit(form: any) {
    form.value.department = this.selectedDept.deptName

    this.usersSvc.createUser(form.value).subscribe(res => {
      let user
      this.openSnackbar(res.data)
      if (res) {
        const req = { departments: [] }
        req.departments = this.selectedDept.deptName
        this.usersSvc.onSearchUserByEmail(form.value.email, req).subscribe(data => {
          user = data[0]
          const dreq = {
            userId: user ? user.wid : null,
            deptId: this.selectedDept ? this.selectedDept.id : null,
            roles: form.value.roles,
            isActive: true,
            isBlocked: false,
          }
          this.usersSvc.addUserToDepartment(dreq).subscribe(dres => {
            if (dres) {
              this.createUserForm.reset({ fname: '', lname: '', email: '', department: '', roles: '' })
              this.router.navigate(['/app/home/users'])
            }
          })
        })
      }
    },                                             (err: { error: string }) => {
      this.openSnackbar(err.error.split(':')[1])
    })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }
}
