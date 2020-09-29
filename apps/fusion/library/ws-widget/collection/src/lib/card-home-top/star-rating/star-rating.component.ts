import { Component, OnInit, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-widget-star-rating',
  templateUrl: './star-rating.component.html',
  styleUrls: ['./star-rating.component.scss'],
  encapsulation: ViewEncapsulation.Emulated,
})
export class StarRatingComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('rating') rating = 3
  // tslint:disable-next-line:no-input-rename
  @Input('starCount') starCount = 5
  // tslint:disable-next-line:no-input-rename
  @Input('color') color = 'accent'
  @Output() ratingUpdated = new EventEmitter()

  private snackBarDuration = 2000
  public ratingArr = Array()

  constructor(private snackBar: MatSnackBar) {
  }

  ngOnInit() {
    // console.log("a " + this.starCount)
    for (let index = 0; index < this.starCount;) {
      index = +1
      this.ratingArr.push(index)
    }
  }
  onClick(rating: number) {
    // console.log(rating)
    this.snackBar.open(`You rated ${rating}' / '${this.starCount}`, '', {
      duration: this.snackBarDuration,
    })
    this.ratingUpdated.emit(rating)
    return false
  }

  showIcon(index: number) {
    if (this.rating >= index + 1) {
      return 'star'
    }
    return 'star_border'

  }

}
export enum StarRatingColor {
  primary = 'primary',
  accent = 'accent',
  warn = 'warn',
}
