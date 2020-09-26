import { Component } from '@angular/core'
import { StarRatingColor } from '../star-rating/star-rating.component'
@Component({
  selector: 'ws-widget-card-badges',
  templateUrl: './card-badges.component.html',
  styleUrls: ['./card-badges.component.scss'],

})

export class CardBadgeComponent {
  rating = 3
  starCount = 5
  starColor: StarRatingColor = StarRatingColor.accent
  starColorP: StarRatingColor = StarRatingColor.primary
  starColorW: StarRatingColor = StarRatingColor.warn
  onRatingChanged(rating: number) {
    // console.log(rating)
    this.rating = rating
  }

}
