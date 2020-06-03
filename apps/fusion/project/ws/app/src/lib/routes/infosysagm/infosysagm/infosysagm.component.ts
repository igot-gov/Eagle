import { Component, OnInit } from '@angular/core'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-infosysagm',
  templateUrl: './infosysagm.component.html',
  styleUrls: ['./infosysagm.component.scss'],
})
export class InfosysagmComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit() {
    this.router.navigate(['/page/lex_auth_01302995157056716838'])
  }
}
