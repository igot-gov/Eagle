import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'
import { MatSnackBar } from '@angular/material'
import { SignupAutoService } from './signup-auto.service'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-signup-auto',
  templateUrl: './signup-auto.component.html',
  styleUrls: ['./signup-auto.component.scss'],
})
export class SignupAutoComponent implements OnInit {
  fetching = false
  showResonse = false
  uniqueId: any
  msg = ''
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>

  constructor(
    private snackBar: MatSnackBar,
    private signupAutoService: SignupAutoService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.uniqueId = params.get('id')
      this.signup(this.uniqueId)
    })
  }

  signup(id: any) {
    this.fetching = true
    this.signupAutoService.signup(id).subscribe(
      result => {
        this.fetching = false
        const [resonseCode] = result.msg.split(':')
        const email = result.email
        this.showResonse = true
        switch (resonseCode) {
          case '1001': this.msg = `Something went wrong, please contact administrator`
            break
          case '1002': this.msg = `Registered email address is not valid, so please contact administrator`
            break
          case '1003': this.msg = `You have been already registered successfully on the platform with email ${email}.
          Please check your email`
            break
          case '1004': this.msg = `You have been already registered successfully on the platform.
            If you have trouble logging in please contact administrator`
            break
          case '1005': this.msg = `You have been registered successfully on the platform with email ${email}.
          Please check your email`
            break
          default: this.msg = `Something went wrong, please contact administrator`
        }
        this.openSnackbar(this.msg)
      },
      err => {
        this.fetching = false
        this.showResonse = true
        this.msg = `Something went wrong please try again later!!`
        this.openSnackbar(err.error.msg)
      })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

}
