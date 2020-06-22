import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { ENTER, COMMA } from '@angular/cdk/keycodes'
import { Subscription, Observable } from 'rxjs'
import { MatSnackBar, MatChipInputEvent, DateAdapter, MAT_DATE_FORMATS } from '@angular/material'
import { UserProfileService } from '../../services/user-profile.service'
import { ConfigurationsService } from '../../../../../../../../../library/ws-widget/utils/src/public-api'
import { Router } from '@angular/router'
import { startWith, map, debounceTime, distinctUntilChanged } from 'rxjs/operators'
import { INationality, ILanguages, NsUserProfileTypes } from '../../models/user-profile.model'
import { AppDateAdapter, APP_DATE_FORMATS, changeformat } from '../../services/format-datepicker'

@Component({
  selector: 'ws-app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
  providers: [
    { provide: DateAdapter, useClass: AppDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS },
  ],
})
export class UserProfileComponent implements OnInit, OnDestroy {
  createUserForm: FormGroup
  unseenCtrl!: FormControl
  unseenCtrlSub!: Subscription
  uploadSaveData = false
  masterNationality: Observable<INationality[]> | undefined
  masterLanguages: Observable<ILanguages[]> | undefined
  masterKnownLanguages: Observable<ILanguages[]> | undefined
  masterNationalities!: INationality[]
  masterLanguagesEntries!: ILanguages[]
  selectedKnowLangs: ILanguages[] = []
  separatorKeysCodes: number[] = [ENTER, COMMA]
  EPrimaryEmailType = NsUserProfileTypes.EPrimaryEmailType
  EUserGender = NsUserProfileTypes.EUserGender
  EMaritalStatus = NsUserProfileTypes.EMaritalStatus
  ECategory = NsUserProfileTypes.ECategory
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  @ViewChild('knownLanguagesInput', { static: true }) knownLanguagesInputRef!: ElementRef<HTMLInputElement>

  constructor(
    private snackBar: MatSnackBar,
    private userProfileSvc: UserProfileService,
    private configSvc: ConfigurationsService,
    private router: Router,
  ) {
    this.createUserForm = new FormGroup({
      firstname: new FormControl('', [Validators.required]),
      middlename: new FormControl('', []),
      surname: new FormControl('', [Validators.required]),
      mobile: new FormControl('', [Validators.required, Validators.minLength(10)]),
      telephone: new FormControl('', []),
      primaryEmail: new FormControl('', [Validators.required, Validators.email]),
      primaryEmailType: new FormControl('', [Validators.required]),
      secondaryEmail: new FormControl('', []),
      nationality: new FormControl('', [Validators.required]),
      dob: new FormControl('', [Validators.required]),
      gender: new FormControl('', [Validators.required]),
      maritalStatus: new FormControl('', [Validators.required]),
      motherTongue: new FormControl('', []),
      knownLanguages: new FormControl([''], []),
      residenceAddress: new FormControl('', [Validators.required]),
      category: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit() {
    // this.unseenCtrlSub = this.createUserForm.valueChanges.subscribe(value => {
    //   console.log('ngOnInit - value', value);
    // })
    this.getUserDetails()
    this.userProfileSvc.getMasterNationlity().subscribe(
      data => {
        this.masterNationalities = data.nationalities
        // this.masterNationality = of(data.nationalities)
        this.onChangesNationality()
      },
      (err: any) => {
        console.log('err :', err)
      })

    this.userProfileSvc.getMasterLanguages().subscribe(
      data => {
        this.masterLanguagesEntries = data.languages
        this.onChangesLanuage()
        this.onChangesKnownLanuage()
      },
      (err: any) => {
        console.log('err :', err)
      })
  }

  onChangesNationality(): void {
    if (this.createUserForm.get('nationality') != null) {

      // tslint:disable-next-line: no-non-null-assertion
      this.masterNationality = this.createUserForm.get('nationality')!.valueChanges
        .pipe(
          debounceTime(500),
          distinctUntilChanged(),
          startWith(''),
          map(value => typeof value === 'string' ? value : value.name),
          map(name => name ? this.filterNationality(name) : this.masterNationalities.slice())
        )
    }
  }

  onChangesLanuage(): void {

      // tslint:disable-next-line: no-non-null-assertion
      this.masterLanguages = this.createUserForm.get('motherTongue')!.valueChanges
        .pipe(
          debounceTime(500),
          distinctUntilChanged(),
          startWith(''),
          map(value => typeof value === 'string' ? value : value.name),
          map(name => name ? this.filterLanguage(name) : this.masterLanguagesEntries.slice())
        )
  }

  onChangesKnownLanuage(): void {
    // tslint:disable-next-line: no-non-null-assertion
    this.masterKnownLanguages = this.createUserForm.get('knownLanguages')!.valueChanges
        .pipe(
          debounceTime(500),
          distinctUntilChanged(),
          startWith(''),
          map(value => typeof value === 'string' || 'ILanguages'  ? value : value.name),
          map(name => {
            if (name) {
              if (name.constructor === Array) {
                return this.filterMultiLanguage(name)
               }
                 return this.filterLanguage(name)
            }
              return this.masterLanguagesEntries.slice()
          })
        )
  }

  private filterNationality(name: string): INationality[] {
    if (name) {
    const filterValue = name.toLowerCase()
      return this.masterNationalities.filter(option => option.name.toLowerCase().includes(filterValue))
    }
    return this.masterNationalities
  }

  private filterLanguage(name: string): ILanguages[]  {
    if (name) {
      const filterValue = name.toLowerCase()
      return this.masterLanguagesEntries.filter(option => option.name.toLowerCase().includes(filterValue))
    }
    return this.masterLanguagesEntries
  }

  private filterMultiLanguage(name: string[]): ILanguages[]  {
    if (name) {
      const filterValue = name.map(n => n.toLowerCase())
      return this.masterLanguagesEntries.filter(option => {
        // option.name.toLowerCase().includes(filterValue))
        filterValue.map(f => {
          return option.name.toLowerCase().includes(f)
        })
      })
    }
    return this.masterLanguagesEntries
  }

  ngOnDestroy() {
    if (this.unseenCtrlSub && !this.unseenCtrlSub.closed) {
      this.unseenCtrlSub.unsubscribe()
    }
  }

  public selectKnowLanguage(data: any) {
    const value: ILanguages = data.option.value
    if (!this.selectedKnowLangs.includes(value)) {
      this.selectedKnowLangs.push(data.option.value)
    }
    this.knownLanguagesInputRef.nativeElement.value = ''
    if (this.createUserForm.get('knownLanguages')) {
      // tslint:disable-next-line: no-non-null-assertion
      this.createUserForm.get('knownLanguages')!.setValue(null)
    }
  }

  public removeKnowLanguage(lang: any) {
    const index = this.selectedKnowLangs.indexOf(lang)

    if (index >= 0) {
      this.selectedKnowLangs.splice(index, 1)
    }

  }

  add(event: MatChipInputEvent): void {
    const input = event.input
    const value = event.value as unknown as ILanguages

    // Add our fruit
    if ((value || '')) {
      this.selectedKnowLangs.push(value)
    }

    // Reset the input value
    if (input) {
      input.value = ''
    }

    this.knownLanguagesInputRef.nativeElement.value = ''
    if (this.createUserForm.get('knownLanguages')) {
      // tslint:disable-next-line: no-non-null-assertion
      this.createUserForm.get('knownLanguages')!.setValue(null)
    }
  }

  getUserDetails() {
    if (this.configSvc.profileDetailsStatus) {
      if (this.configSvc.userProfile) {
      this.userProfileSvc.getUserdetailsFromRegistry().subscribe(
        data => {
          if (data && data.length) {
            this.createUserForm.patchValue({
              firstname: data[0].firstname,
              middlename: data[0].middlename,
              surname: data[0].surname,
              mobile: data[0].mobile,
              telephone: data[0].telephone,
              primaryEmail: data[0].primaryEmail,
              primaryEmailType: data[0].primaryEmailType,
              secondaryEmail: data[0].secondaryEmail,
              nationality: data[0].nationality,
              dob: new Date(data[0].dob),
              gender: data[0].gender,
              maritalStatus: data[0].maritalStatus,
              motherTongue: data[0].motherTongue,
              knownLanguages: data[0].knownLanguages,
              residenceAddress: data[0].residenceAddress,
              category: data[0].category,
            })
            this.selectedKnowLangs = data[0].knownLanguages
          }
        },
        (err: any) => {
          console.log('err :', err)
        })
      }
    } else {
      if (this.configSvc.userProfile) {
        this.userProfileSvc.getUserdetails(this.configSvc.userProfile.email).subscribe(
          data => {
            if (data && data.length) {
              this.createUserForm.patchValue({
                firstname: data[0].first_name,
                surname: data[0].last_name,
                primaryEmail: data[0].email,
              })
            }
          },
          (err: any) => {
            console.log('err :', err)
          })
      }
    }
  }

  async onSubmit(form: any) {
    // DO some co=ustomization on the input data
    form.value.knownLanguages = this.selectedKnowLangs
    form.value.dob = changeformat(form.value.dob)
    this.uploadSaveData = true
    this.userProfileSvc.updateProfileDetails(form.value).subscribe(
      () => {
        form.reset()
        this.uploadSaveData = false
        this.configSvc.profileDetailsStatus = true
        this.openSnackbar(this.toastSuccess.nativeElement.value)
        this.router.navigate(['page', 'home'])
      },
      (err: any) => {
        this.openSnackbar(this.toastError.nativeElement.value)
        this.uploadSaveData = false
        console.log('err :', err)
      })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, undefined, {
      duration,
    })
  }

}
