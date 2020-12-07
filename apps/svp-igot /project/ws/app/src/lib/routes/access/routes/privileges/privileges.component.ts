import { Component, OnInit } from '@angular/core'

@Component({
  selector: 'ws-app-privileges',
  templateUrl: './privileges.component.html',
  styleUrls: ['./privileges.component.scss'],
})
export class PrivilegesComponent implements OnInit {
  userMgmtData: any = []
  fracData: any = []
  constructor() { }

  ngOnInit() {
    this.userMgmtData = [{
      name: 'Create Users',
      key: 'Create Users',
      checked: false,
      enabled: true,
    },
    {
      name: 'Activate Users',
      key: 'Activate Users',
      checked: true,
      enabled: true,
    },
    {
      name: 'Add/Remove Users',
      key: 'Add/Remove Users',
      checked: true,
      enabled: true,
    },
    {
      name: 'Block Users',
      key: 'Block Users',
      checked: false,
      enabled: true,
    },
    {
      name: 'Approve fields',
      key: 'Approve fields',
      checked: true,
      enabled: true,
    }]

    this.fracData = [{
      name: 'Competencies',
      key: 'competencies',
      checked: true,
      enabled: true,
    },
    {
      name: 'Postions',
      key: 'postions',
      checked: false,
      enabled: true,
    },
    {
      name: 'Roles',
      key: 'roles',
      checked: true,
      enabled: true,
    },
    {
      name: 'Knowledge resources',
      key: 'knowledge resources',
      checked: false,
      enabled: true,
    },
    {
      name: 'Question bank',
      key: 'question bank',
      checked: true,
      enabled: true,
    }]
  }

}
