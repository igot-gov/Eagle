import { Component, ElementRef, AfterViewInit, Input, ViewChild, OnInit } from '@angular/core'
import { ConfigurationsService } from '../../../../../utils/src/public-api'

@Component({
  selector: 'ws-widget-btn-linkedin-share',
  templateUrl: './btn-linkedin-share.component.html',
  styleUrls: ['./btn-linkedin-share.component.scss'],
})
export class BtnLinkedinShareComponent implements OnInit, AfterViewInit {
  @Input() url = location.href
  @ViewChild('element', { static: true }) element: ElementRef | null = null
  isSocialMediaLinkedinShareEnabled = false
  constructor(private configSvc: ConfigurationsService) {
    // load twitter sdk if required
    const url = 'https://platform.linkedin.com/in.js'
    if (!document.querySelector(`script[src='${url}']`)) {
      const script = document.createElement('script')
      script.src = url
      script.innerHTML = ' lang: en_US'
      document.body.appendChild(script)
    }
  }

  ngOnInit() {
    if (this.configSvc.restrictedFeatures) {
      this.isSocialMediaLinkedinShareEnabled = !this.configSvc.restrictedFeatures.has(
        'socialMediaLinkedinShare',
      )
    }
  }

  ngAfterViewInit(): void {
    if (this.isSocialMediaLinkedinShareEnabled && this.element) {
      // add linkedin share button script tag to element
      let urlarr=location.href.split('/');
      let contentId= urlarr[urlarr.length-2]
      let url = `https://d136953gtttd92.cloudfront.net/share/content/${contentId}` 
      console.log(url); 

      this.element.nativeElement.innerHTML = `<script type="IN/Share" data-url="${url}"></script>`

      // render share button
      // tslint:disable-next-line: no-unused-expression  tslint:disable-next-line: whitespace
      ;(window as any)['IN'] && (window as any)['IN'].parse()
    }
  }
}
