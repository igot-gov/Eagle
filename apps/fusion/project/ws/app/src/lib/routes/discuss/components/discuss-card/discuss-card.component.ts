import { Component } from '@angular/core'

@Component({
  selector: 'app-dicuss-card',
  templateUrl: './discuss-card.component.html',
  styleUrls: ['./discuss-card.component.scss'],

})
export class DiscussCardComponent {
  panelOpenState = false
  items = ['1', '2', '3', '4', '5']

}
