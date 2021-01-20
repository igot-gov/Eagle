import { Component, OnDestroy, OnInit, OnChanges, Input } from '@angular/core'
// import { ActivatedRoute } from '@angular/router'
// import { ConfigurationsService } from '@ws-widget/utils'
// import { NSProfileDataV2 } from '../../models/profile-v2.model'

@Component({
  selector: 'app-right-menu-card',
  templateUrl: './right-menu-card.component.html',
  styleUrls: ['./right-menu-card.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class RightMenuCardComponent implements OnInit, OnChanges, OnDestroy {
  
  @Input() data?: []
  joiningInfo: any = []

  constructor(
  
  ) {
  
  }
  ngOnInit(): void {
  
  }

  ngOnChanges() {
    if(this.data != undefined) {
      this.joiningInfo.push(this.data)
      console.log(this.joiningInfo)
    }
  }
  

  ngOnDestroy() {

  }
}
