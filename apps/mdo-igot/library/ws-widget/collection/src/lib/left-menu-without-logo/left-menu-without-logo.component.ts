import { Component, OnInit, OnDestroy, Input } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ILeftMenuWithoutLogo } from './left-menu-without-logo.model'

@Component({
  selector: 'ws-widget-left-menu-without-logo',
  templateUrl: './left-menu-without-logo.component.html',
  styleUrls: ['./left-menu-without-logo.component.scss'],
})
export class LeftMenuWithoutLogoComponent extends WidgetBaseComponent
  implements OnInit, OnDestroy, NsWidgetResolver.IWidgetData<ILeftMenuWithoutLogo[]>  {
  @Input() widgetData!: ILeftMenuWithoutLogo[]
  param: any
  constructor(private activatedRoute: ActivatedRoute) {
    super()
  }

  ngOnInit(): void {

  }

  public isLinkActive(url: string): boolean {
    return (this.activatedRoute.snapshot.fragment === url)
  }

  ngOnDestroy() {
  }

  getLink(tab: ILeftMenuWithoutLogo) {
    if (tab && tab.customRouting && this.activatedRoute.snapshot && this.activatedRoute.snapshot.firstChild && tab.paramaterName) {
      return (tab.routerLink.replace('<param>', this.activatedRoute.snapshot.firstChild.params[tab.paramaterName]))
    }
    return
  }
}
