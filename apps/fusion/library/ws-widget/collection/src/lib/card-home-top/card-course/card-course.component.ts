import { Component } from '@angular/core'
import { StarRatingColor } from '../star-rating/star-rating.component'

@Component({
  selector: 'ws-widget-card-course',
  templateUrl: './card-course.component.html',
  styleUrls: ['./card-course.component.scss'],

})

export class CardCourseComponent {
  rating = 3
  starCount = 5
  items = ['1', '2', '3', '4']
  starColor: StarRatingColor = StarRatingColor.accent
  starColorP: StarRatingColor = StarRatingColor.primary
  starColorW: StarRatingColor = StarRatingColor.warn
  onRatingChanged(rating: number) {
    // console.log(rating)
    this.rating = rating
  }

}
