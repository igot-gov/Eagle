import { Component, OnInit, OnDestroy } from '@angular/core'
import { FracService } from '../../services/frac.service'
// import { ConfigurationsService } from '@ws-widget/utils/src/lib/services/configurations.service'
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser'
import { IFrac } from '../../interfaces/frac.model'

@Component({
  selector: 'ws-app-frac',
  templateUrl: './frac.component.html',
  styleUrls: ['./frac.component.scss'],
})

export class FracComponent implements
  OnInit,
  OnDestroy {
  widgetData: IFrac = {
    iframeId: 'fracData',
    title: 'Frac',
    containerStyle: '',
    containerClass: '',
    iframeSrc: 'https://google.com',
  }
  iframeSrc: SafeResourceUrl | null = null
  constructor(
    private domSanitizer: DomSanitizer,
    private fracService: FracService
  ) {

  }
  ngOnInit() {
    this.fracService.fetchFrac().then((result: IFrac) => {
      if (result) {
        this.widgetData = result
        if (this.widgetData && this.widgetData.iframeSrc) {
          this.iframeSrc = this.domSanitizer.bypassSecurityTrustResourceUrl(this.widgetData.iframeSrc)
        }
      } else {
        this.iframeSrc = this.domSanitizer.bypassSecurityTrustResourceUrl(`${window.location.origin}/frac`)
      }
    })
  }

  ngOnDestroy() {

  }
}
