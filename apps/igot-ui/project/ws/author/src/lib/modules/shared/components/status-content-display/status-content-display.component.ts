import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core'

@Component({
  selector: 'ws-auth-status-content-display',
  templateUrl: './status-content-display.component.html',
  styleUrls: ['./status-content-display.component.scss'],
})
export class StatusContentDisplayComponent implements OnInit {
  @Input() workFlow: IContentFlow[] | undefined
  @Output() selectes = new EventEmitter<string>()

  constructor() { }

  ngOnInit() { }
  customStepper($event: string) {
    this.selectes.emit($event)
  }
}

export interface IContentFlow {
  name: string,
  isActive: boolean,
  isCompleted: boolean,
  step: number
}
