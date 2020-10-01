import { Component, OnInit } from '@angular/core'

@Component({
  selector: 'ws-app-careers-home',
  templateUrl: './careers-home.component.html',
  styleUrls: ['./careers-home.component.scss'],
})
export class CareersHomeComponent implements OnInit {
  titles = [{ title: 'CAREER', url: '/app/careers/home', icon: 'work' }]

  constructor() { }

  ngOnInit() {
  }

}
