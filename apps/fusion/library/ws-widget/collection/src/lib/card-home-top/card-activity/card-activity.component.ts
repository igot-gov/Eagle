import { Component } from '@angular/core'
import { StarRatingColor } from '../star-rating/star-rating.component'

@Component({
  selector: 'ws-widget-card-activity',
  templateUrl: './card-activity.component.html',
  styleUrls: ['./card-activity.component.scss'],

})

export class CardActivityComponent {
  rating = 3
  starCount = 5
  items = ['1', '2', '3', '4', '5', '6']
  starColor: StarRatingColor = StarRatingColor.accent
  starColorP: StarRatingColor = StarRatingColor.primary
  starColorW: StarRatingColor = StarRatingColor.warn
  onRatingChanged(rating: number) {
    this.rating = rating
  }

}
