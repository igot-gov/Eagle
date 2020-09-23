import { Component } from '@angular/core'

@Component({
  selector: 'ws-widget-card-badges',
  templateUrl: './card-badges.component.html',
  styleUrls: ['./card-badges.component.scss'],

})

export class CardBadgeComponent {
  newUserArray = ['1', '2', '3', '4', '1', '2', '3', '4']
  getUserFullName() {
    return 'RJ'
  }

}
