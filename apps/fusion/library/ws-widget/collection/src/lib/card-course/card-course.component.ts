import { Component, Input } from '@angular/core'
// import { StarRatingColor } from './star-rating/star-rating.component'
import { WidgetBaseComponent } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-card-course',
  templateUrl: './card-course.component.html',
  styleUrls: ['./card-course.component.scss'],

})

export class CardCourseComponent extends WidgetBaseComponent {
  rating = 3
  starCount = 5
  items = ['1', '2', '3']
  // starColor: StarRatingColor = StarRatingColor.accent
  // starColorP: StarRatingColor = StarRatingColor.primary
  // starColorW: StarRatingColor = StarRatingColor.warn
  @Input() widgetData: any
  onRatingChanged(rating: number) {
    // console.log(rating)
    this.rating = rating
  }

}
