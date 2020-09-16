import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
// import { Router } from '@angular/router'
@Component({
  selector: 'app-dicuss-category-card',
  templateUrl: './category-card.component.html',
  styleUrls: ['./category-card.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-s margin-top-l' },
  /* tslint:enable */

})

export class CategoryCardComponent implements OnInit {
  @Input()
  category!: NSDiscussData.ICategorie
  constructor(
    // private router: Router,
    // private snackBar: MatSnackBar,
    // private discussionSvc: DiscussService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() { }

  getDiscussion() {
    // this.router.navigate([`/app/discuss/home/${this.category.cid}`])
  }
}
