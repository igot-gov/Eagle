import { AfterViewInit, Component, HostBinding, Input, OnDestroy, OnInit } from '@angular/core'
// import { MatSnackBar } from '@angular/material'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
// import { ConfigurationsService, EventService, UtilityService, NsInstanceConfig } from '@ws-widget/utils'
// import { Subscription } from 'rxjs'
// // import { NsGoal } from '../btn-goals/btn-goals.model'
// import { NsPlaylist } from '../btn-playlist/btn-playlist.model'
// import { NsContent } from '../_services/widget-content.model'
import { NsTableCard } from './card-table.model'

@Component({
  selector: 'ws-widget-table-card-content',
  templateUrl: './card-content.component.html',
  styleUrls: ['./card-content.component.scss'],
})
export class CardTableComponent extends WidgetBaseComponent
  implements OnInit, OnDestroy, AfterViewInit, NsWidgetResolver.IWidgetData<NsTableCard.ITableCard> {
  @Input() widgetData!: NsTableCard.ITableCard
  @HostBinding('id')
  public id = `ws-card_${Math.random()}`
  constructor(
    // private events: EventService,
    // private configSvc: ConfigurationsService,
    // private utilitySvc: UtilityService,
    // private snackBar: MatSnackBar,
  ) {
    super()
  }
  ngOnInit() { }
  ngOnDestroy() {
    // if (this.prefChangeSubscription) {
    // this.prefChangeSubscription.unsubscribe()
    // }
  }

  ngAfterViewInit() {
    // this.assignThumbnail()
  }
}
