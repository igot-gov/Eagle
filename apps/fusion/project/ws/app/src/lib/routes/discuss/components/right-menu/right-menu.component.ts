import { Component, OnInit } from '@angular/core'

@Component({
  selector: 'app-discuss-right-menu',
  templateUrl: './right-menu.component.html',
  styleUrls: ['./right-menu.component.scss'],
})
export class RightMenuComponent implements OnInit {
  items = [
    'All new methods of control of powers of the administrative authorities and more such policies.',
    'Powers and functions of the administrative authorities Methods of control of powers of the administrative authorities',
  ]
  ngOnInit(): void {

  }

}
