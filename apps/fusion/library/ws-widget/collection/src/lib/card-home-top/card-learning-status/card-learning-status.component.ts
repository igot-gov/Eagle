import { Component } from '@angular/core'

@Component({
  selector: 'ws-widget-card-learning-status',
  templateUrl: './card-learning-status.component.html',
  styleUrls: ['./card-learning-status.component.scss'],

})

export class CardLearningStatusComponent {
  items = [{
    count: 14,
    status: 'Completed',
  }, {
    count: 32,
    status: 'In Progress',
  }, {
    count: 11,
    status: 'Not Started',
  }]

}
