import { IWidgetElementHtml } from '@ws-widget/collection'
import { CONTENT_BASE_WEBHOST_ASSETS } from '@ws/author/src/lib/constants/apiEndpoints'
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core'

@Component({
  selector: 'ws-auth-html-v2',
  templateUrl: './html-v2.component.html',
  styleUrls: ['./html-v2.component.scss'],
})
export class HtmlV2Component implements OnInit {
  showInfo = ''
  @Input() isSubmitPressed = false
  @Input() content!: IWidgetElementHtml
  @Input() identifier = ''
  location = CONTENT_BASE_WEBHOST_ASSETS
  @Output() data = new EventEmitter<{ content: IWidgetElementHtml; isValid: boolean }>()
  dataType: 'html' | 'editor' = 'html'

  constructor() { }

  update(value: any, dataType: any) {
    if (dataType === 'editor') {
      this.content.html = value
    } else if (dataType === 'html') {
      const htmlText = this.extractAnchorTag(value.target.value)
      if (htmlText) {
        this.content.html = htmlText
      } else {
        this.content.html = value.target.value
      }
    }
  }
  extractAnchorTag(value: string): string | null {
    const el = document.createElement('html')
    el.innerHTML = value
    if (el.getElementsByTagName('a')[0]) {
      const href = el.getElementsByTagName('a')[0].getAttribute('href')
      if (href && href.indexOf(this.identifier) > -1 && href.indexOf('#') > -1) {
        if (href.indexOf('./page/') < 0) {
          el.getElementsByTagName('a')[0].setAttribute(
            'href',
            `./page/${this.identifier}#${href.split('#')[1]}`,
          )
          return el.innerHTML
        }
      }
    }
    return null
  }

  ngOnInit() { }
}
