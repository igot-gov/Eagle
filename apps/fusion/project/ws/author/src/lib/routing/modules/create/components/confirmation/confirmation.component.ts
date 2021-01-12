import { Component, OnInit, OnDestroy } from '@angular/core'
import { ConfigurationsService } from '@ws-widget/utils'

@Component({
  selector: 'ws-auth-confirm',
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.scss'],

})
export class ConfirmationComponent implements OnInit, OnDestroy {
  isChecked = false
  contentQualityCourse: string | undefined
  ngOnInit() {
    this.contentQualityCourse = this.configService.courseContentPath
  }
  constructor(private configService: ConfigurationsService) {

  }

  ngOnDestroy(): void {

  }

  setAll(checked: boolean) {
    if (checked) {
      this.isChecked = true
    } else {
      this.isChecked = false
    }
  }

}
