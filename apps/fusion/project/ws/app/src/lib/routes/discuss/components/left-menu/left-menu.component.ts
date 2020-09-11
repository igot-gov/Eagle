import { Component, OnInit } from '@angular/core'

@Component({
  selector: 'app-discuss-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.scss'],
})
export class LeftMenuComponent implements OnInit {
  items = ['1', '2', '3', '4', '5']
  ngOnInit(): void {

  }

}
