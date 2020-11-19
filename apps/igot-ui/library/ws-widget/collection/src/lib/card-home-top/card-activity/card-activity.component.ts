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
  items = [
    { count: 7, icon: 'shop_two', name: 'Courses' },
    { count: 3, icon: 'card_membership', name: 'Certificates' },
    { count: 42, icon: 'query_builder', name: 'Training Hours' },
    { count: 20, icon: 'history', name: 'Daily Minutes' },
    { count: 56, icon: 'hourglass_empty', name: 'Karma' },
    { count: 123, icon: 'group_work', name: 'IGOT Coins' }]
  starColor: StarRatingColor = StarRatingColor.accent
  starColorP: StarRatingColor = StarRatingColor.primary
  starColorW: StarRatingColor = StarRatingColor.warn
  onRatingChanged(rating: number) {
    this.rating = rating
  }

}
