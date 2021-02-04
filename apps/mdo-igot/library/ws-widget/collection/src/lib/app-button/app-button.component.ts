import { Component, OnInit, EventEmitter, Input, Output } from '@angular/core'

@Component({
  selector: 'ws-widget-app-button',
  templateUrl: './app-button.component.html',
  styleUrls: ['./app-button.component.scss'],
})
export class AppButtonComponent implements OnInit {
  @Input() id?: string
  @Input() label?: string
  @Input() disabled?: boolean
  @Input() icon?: string
  @Input() type?: string
  @Input() styles?: any = {}
  @Input() classes?: String = ''
  @Output() eonClick = new EventEmitter<any>()

  constructor() {
  }
  onClickbutton(event: any) {
    if (!this.disabled) {
      this.eonClick.emit(event)
    }
  }
  ngOnInit() {
  }
}
