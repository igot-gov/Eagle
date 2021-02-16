import { Component, OnInit, OnDestroy, HostListener, ViewChild, ElementRef } from '@angular/core'
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { ActivatedRoute, Router, Event, NavigationEnd } from '@angular/router'
import { UsersService } from '../../services/users.service'
import { MatSnackBar } from '@angular/material'
import { ILeftMenu } from '@ws-widget/collection'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'
import { ConfigurationsService, ValueService } from '@ws-widget/utils/src/public-api'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'ws-app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss'],
})
export class CreateUserComponent implements OnInit, OnDestroy {
  sideNavBarOpened = true
  currentRoute = 'users'
  myRoles!: Set<string>
  widgetData!: NsWidgetResolver.IWidgetData<ILeftMenu>
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  elementPosition: any
  sticky = false
  private defaultSideNavBarOpenedSubscription: any
  public screenSizeIsLtMedium = false
  isLtMedium$ = this.valueSvc.isLtMedium$
  createUserForm: FormGroup
  namePatern = `^[a-zA-Z\\s\\']{1,32}$`
  department: any = {}
  departmentName = ''
  toastSuccess: any
  rolesList: any = []
  public userRoles: Set<string> = new Set()

  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = true
    } else {
      this.sticky = false
    }
  }

  constructor(private router: Router, private activeRoute: ActivatedRoute,
              private snackBar: MatSnackBar,
              private usersSvc: UsersService,
              private configService: ConfigurationsService,
              private valueSvc: ValueService) {
    if (this.configService.userRoles) {
      this.myRoles = this.configService.userRoles
    }
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.bindUrl(event.urlAfterRedirects.replace('/app/home/', ''))
        if (this.activeRoute.snapshot.data.department.data) {
          const leftData = this.activeRoute.snapshot.data.pageData.data.menus
          _.set(leftData, 'widgetData.logo', true)
          _.set(leftData, 'widgetData.logoPath', _.get(this.activeRoute, 'snapshot.data.department.data.logo'))
          _.set(leftData, 'widgetData.name', _.get(this.activeRoute, 'snapshot.data.department.data.deptName')
            || _.get(this.activeRoute, 'snapshot.data.department.data.description'))
          _.set(leftData, 'widgetData.userRoles', this.myRoles)
          this.widgetData = leftData
        } else {
          this.widgetData = this.activeRoute.snapshot.data.pageData.data.menus
        }
      }
    })
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.department = this.activeRoute.snapshot.data.department.data
        this.departmentName = this.department ? this.department.deptName : ''
        this.rolesList = this.department.rolesInfo
        this.configService.departName = this.departmentName
      }
    })
    this.createUserForm = new FormGroup({
      fname: new FormControl('', [Validators.required]),
      lname: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      department: new FormControl(''),
      roles: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe(isLtMedium => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })
  }

  onSubmit(form: any) {
    const newobj = {
      personalDetails: {
          email: form.value.email,
          userName : form.value.fname,
          firstName: form.value.fname,
          lastName: form.value.lname,
      },
    }

    this.usersSvc.createUser(newobj).subscribe(res => {
      // let user
      // const deptRole = this.department.rolesInfo.filter((role: { roleName: string }) => role.roleName === 'MEMBER')[0]
      this.openSnackbar(res.data)
      if (res) {
        const req = {
          departments: [
            'igot',
            'istm',
            'iGOT',
            'NPA',
            'NACIN',
            'LSNAA',
            'ISTM',
          ],
        }
        if (req.departments.indexOf(this.department.deptName) === -1) {
          req.departments.push(this.department.deptName)
        }

        const dreq = {
          userId: res.userId,
          deptId: this.department ? this.department.id : null,
          roles: form.value.roles,
          isActive: true,
          isBlocked: false,
        }

        this.usersSvc.addUserToDepartment(dreq).subscribe(dres => {
          if (dres) {
            this.createUserForm.reset({ fname: '', lname: '', email: '', department: this.departmentName, roles: '' })
            this.router.navigate(['/app/home/users'])
          }
        })

        // this.usersSvc.onSearchUserByEmail(form.value.email, req).subscribe(data => {
        //   const userreq = {
        //     personalDetails: {
        //         firstname: data[0].first_name,
        //         surname: data[0].last_name,
        //         primaryEmail: data[0].email,
        //     },
        //     professionalDetails: [
        //         {
        //             name: data[0].department_name,
        //         },
        //     ],
        //     academics: [
        //       {
        //         nameOfQualification: '',
        //         yearOfPassing: '',
        //         nameOfInstitute: '',
        //         type: 'X_STANDARD',
        //       },
        //       {
        //         nameOfQualification: '',
        //         yearOfPassing: '',
        //         nameOfInstitute: '',
        //         type: 'XII_STANDARD',
        //       },
        //       {
        //         nameOfQualification: '',
        //         yearOfPassing: '',
        //         nameOfInstitute: '',
        //         type: 'GRADUATE',
        //       },
        //       {
        //         nameOfQualification: '',
        //         yearOfPassing: '',
        //         nameOfInstitute: '',
        //         type: 'POSTGRADUATE',
        //       },
        //     ],
        //     interests: {
        //       hobbies: [],
        //       professional: [],
        //     },
        //     skills: {
        //       certificateDetails: '',
        //       additionalSkills: '',
        //       osCreatedBy: '',
        //     },
        //     employmentDetails: {
        //       departmentName: '',
        //       officialPostalAddress: '',
        //       employeeCode: '',
        //       allotmentYearOfService: '',
        //       payType: '',
        //       civilListNo: '',
        //       dojOfService: '',
        //       service: '',
        //       pinCode: '',
        //       cadre: '',
        //     },
        //   }

        //   this.usersSvc.createUserById(data[0].wid, userreq).subscribe(userdata => {
        //     if (userdata) {
        //       const dreq = {
        //         userId: data[0] ? data[0].wid : null,
        //         deptId: this.department ? this.department.id : null,
        //         roles: form.value.roles,
        //         isActive: true,
        //         isBlocked: false,
        //       }
        //       this.usersSvc.addUserToDepartment(dreq).subscribe(dres => {
        //         if (dres) {
        //           this.createUserForm.reset({ fname: '', lname: '', email: '', department: this.departmentName, roles: '' })
        //           this.router.navigate(['/app/home/users'])
        //         }
        //       })
        //     }
        //   })
        // })
      }
    },                                         (err: { error: string }) => {
      this.openSnackbar(err.error.split(':')[1])
    })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  modifyUserRoles(role: string) {
    if (this.userRoles.has(role)) {
      this.userRoles.delete(role)
    } else {
      this.userRoles.add(role)
    }
  }

  bindUrl(path: string) {
    if (path) {
      this.currentRoute = path
    }
  }

  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
    // if (this.bannerSubscription) {
    //   this.bannerSubscription.unsubscribe()
    // }
  }
}
