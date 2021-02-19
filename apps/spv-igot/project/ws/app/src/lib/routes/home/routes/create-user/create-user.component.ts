import { Component, OnInit } from '@angular/core'
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { UsersService } from '../../services/users.service'
import { MatSnackBar } from '@angular/material'
import { ActivatedRoute, Router } from '@angular/router'

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
  departmentoptions: any = []
  dropdownSettings = {}
  receivedDept: any
  selectedDept: any
  public userRoles: Set<string> = new Set()
  queryParam: any
  currentDept: any

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private usersSvc: UsersService) {
    this.route.queryParams.subscribe(params => {
      this.queryParam = params['id']
      this.currentDept = params['currentDept']
      // tslint:disable-next-line:radix
      this.queryParam = parseInt(this.queryParam)
    })
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

      if (this.queryParam) {
        this.departmentoptions.forEach((dept: any) => {
          if (dept.id === this.queryParam) {
            this.rolesList = dept.rolesInfo
            const item = {
              deptName: dept.deptName,
              id: dept.id,
            }
            this.receivedDept = item
            this.departmentName = this.receivedDept.deptName
          }
        })
      }
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
    form.value.department = this.selectedDept ? this.selectedDept.deptName : this.receivedDept.deptName
    const userreq = {
      personalDetails: {
          email: form.value.email,
          userName : form.value.fname,
          firstName: form.value.fname,
          lastName: form.value.lname,
      },
    }
this.usersSvc.createUser(userreq).subscribe(userdata => {
            if (userdata) {
              const dreq = {
                userId: userdata.userId,
                deptId: this.selectedDept ? this.selectedDept.id : this.receivedDept.id,
                roles: form.value.roles,
                isActive: true,
                isBlocked: false,
              }
              this.usersSvc.addUserToDepartment(dreq).subscribe(dres => {
                if (dres) {
                  this.createUserForm.reset({ fname: '', lname: '', email: '', department: '', roles: '' })
                  if (this.selectedDept) {
                    this.router.navigate(['/app/home/users'])
                  } else {
                    this.router.navigate([`/app/roles/${this.receivedDept.id}/users`,
                    { currentDept: this.currentDept, roleId: this.receivedDept.id }])
                  }
                }
              })
            }
          },                                (err: { error: string }) => {
            this.openSnackbar(err.error)
          })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }
}
