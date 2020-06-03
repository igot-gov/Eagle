import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core'
import { ISortEvent } from '../../../../../../../../../modules/shared/directives/draggable/sortable-list.directive'
import { ConfigurationsService } from '@ws-widget/utils/src/lib/services/configurations.service'
import { IDrag } from './drag-n-sort.model'

@Component({
  selector: 'ws-auth-drag-n-sort',
  templateUrl: './drag-n-sort.component.html',
  styleUrls: ['./drag-n-sort.component.scss'],
})
export class DragNSortComponent implements OnInit {

  @Input() selectedData!: IDrag[]
  @Output() change = new EventEmitter<{
    sort: boolean,
    data: ISortEvent
  }>()
  @Input() dataAvailable = false
  defaultThumbnail = ''
  backupData: string[] = []
  constructor(
    private configSvc: ConfigurationsService,
  ) {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
  }

  ngOnInit() {
  }

  sort(event: ISortEvent) {
    const current = this.selectedData[event.currentIndex]
    const swapWith = this.selectedData[event.newIndex]
    this.selectedData[event.newIndex] = current
    this.selectedData[event.currentIndex] = swapWith
    this.change.emit({
      sort: true,
      data: event,
    })
  }

}
