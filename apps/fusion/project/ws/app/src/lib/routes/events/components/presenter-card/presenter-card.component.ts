import { Component, OnInit, Input } from '@angular/core'

@Component({
  selector: 'app-presenter-card',
  templateUrl: './presenter-card.component.html',
  styleUrls: ['./presenter-card.component.scss'],
})
export class PresenterCardComponent implements OnInit {
  constructor() { }
  @Input()
  badge!: any

  ngOnInit() { }
}
