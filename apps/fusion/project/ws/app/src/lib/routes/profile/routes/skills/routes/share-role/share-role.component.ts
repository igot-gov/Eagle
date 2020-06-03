import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'
import { SkillsService } from '../../services/skills.service'
import { IShareObj, IRoleQuotientResponse, ISearchAutoComplete, ISelectedEmailId } from '../../models/competency-model'
import { TFetchStatus } from '@ws-widget/utils'
import { ActivatedRoute, Router } from '@angular/router'
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms'
import { MatSnackBar } from '@angular/material'
import { distinctUntilChanged } from 'rxjs/operators'
import { MatChipInputEvent } from '@angular/material/chips'
import { MatAutocomplete } from '@angular/material/autocomplete'
import { NsAutoComplete } from '@ws-widget/collection'
@Component({
  selector: 'ws-app-share-role',
  templateUrl: './share-role.component.html',
  styleUrls: ['./share-role.component.scss'],
})
export class ShareRoleComponent implements OnInit {
  shareRoleFetchStatus: TFetchStatus = 'fetching'
  selectable = true
  roleId = ''
  shareControl = new FormControl()
  filteredOptions: ISearchAutoComplete[] = []
  roleQuotientData: any
  roleName = ''
  roleSkills = []
  searchText = ''
  options: ISearchAutoComplete[] = []
  selectedSkills: ISelectedEmailId[] = []
  emailIdList: string[] = []
  noRoles = false
  roleStatus = false
  shareForm: FormGroup | null = null
  removable = true
  skillId = ''
  loader = true
  isClient = this.route.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  shareRole: IShareObj | null = null
  users: NsAutoComplete.IUserAutoComplete[] = []
  widList: string[] = []
  @ViewChild('emptyContent', { static: true })
  emptyContentMessage!: ElementRef<any>
  @ViewChild('successContent', { static: true })
  successContentMessage!: ElementRef<any>
  @ViewChild('emailInput', { static: false }) emailInput: ElementRef<HTMLInputElement> | null = null
  @ViewChild('auto', { static: false }) matAutocomplete: MatAutocomplete | null = null
  constructor(
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private skillSvc: SkillsService,
    private router: Router,
    private form1: FormBuilder,
  ) {
    this.shareControl.valueChanges.pipe(
      distinctUntilChanged(),
    )
      .subscribe((value: string) => {
        this.autoCompleteEmailId(value, 'assign_role')
      })
    this.shareForm = this.form1.group({
      roleName: [''],
      emailId: ['', Validators.required],
      message: '',
    })
  }

  ngOnInit() {
    this.shareRoleFetchStatus = 'fetching'
    this.roleId = this.route.snapshot.params.id.replace(/%20/g, ' ')
    this.roleQuotient()
    this.shareRoleFetchStatus = 'done'
  }

  roleQuotient() {
    this.skillSvc.roleQuotient(this.roleId).subscribe((roleQuotient: IRoleQuotientResponse) => {
      this.roleQuotientData = roleQuotient
      if (this.isClient) {
        this.roleName = this.roleQuotientData.role_name
      } else {
        this.roleName = this.roleQuotientData.role_quotient_details.role_name
        this.roleId = this.roleQuotientData.role_quotient_details.role_id
      }
    })
  }
  openSnackBar() {
    this.snackBar.open(this.successContentMessage.nativeElement.value)
  }

  displayFn() {
    return ''
  }
  add(event: MatChipInputEvent): void {
    // Add fruit only when MatAutocomplete is not open
    // To make sure this does not conflict with OptionSelected Event
    if (this.matAutocomplete && !this.matAutocomplete.isOpen) {
      const input = event.input
      const value = event.value

      // Add our fruit
      if ((value || '').trim()) {
        this.emailIdList.push(value.trim())
      }

      // Reset the input value
      if (input) {
        input.value = ''
      }

      this.shareControl.setValue(null)
    }
  }
  remove(emailId: string): void {
    const index = this.emailIdList.indexOf(emailId)

    if (index >= 0) {
      this.emailIdList.splice(index, 1)
    }
  }
  onOptionSelected(emailId: string) {
    this.emailIdList.push(emailId)
    if (this.emailInput) {
      this.emailInput.nativeElement.value = ''
    }
    this.shareControl.setValue(null)
  }

  autoCompleteEmailId(text: string, field: string) {
    this.skillSvc.autoCompleteSkills(text, field).subscribe((res: any) => {
      this.filteredOptions = res
    })
  }
  onShareRole(roleName: string, userMessage: string) {
    this.shareRole = {
      role_name: roleName,
      role_id: this.roleId,
      wid: this.widList,
      message: userMessage,
    }
    if (this.users.length === 0) {
      this.snackBar.open(this.emptyContentMessage.nativeElement.value)
    } else {
      this.skillSvc.shareRole(this.shareRole).subscribe(() => {
        this.openSnackBar()
        this.router.navigate(['/app/profile/skills/roles'])
      },
      )
    }
  }
  usersList(event: NsAutoComplete.IUserAutoComplete[]) {
    this.users = []
    this.widList = []
    this.users = event
    this.users.forEach((widObj: NsAutoComplete.IUserAutoComplete) => {
      this.widList.push(widObj.wid)
    })
  }
}
