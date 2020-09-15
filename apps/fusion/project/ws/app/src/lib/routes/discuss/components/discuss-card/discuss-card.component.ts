import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
@Component({
  selector: 'app-dicuss-card',
  templateUrl: './discuss-card.component.html',
  styleUrls: ['./discuss-card.component.scss'],
  host: { 'class': 'flex flex-1 margin-top-l' }
})

export class DiscussCardComponent implements OnInit {
  @Input()
  discuss!: NSDiscussData.IDiscussionData
  constructor(
    // private snackBar: MatSnackBar,
    // private discussionSvc: DiscussService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() { }
}
