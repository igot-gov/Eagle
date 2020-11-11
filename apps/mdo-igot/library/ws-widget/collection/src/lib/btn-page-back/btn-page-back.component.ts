import { animate, style, transition, trigger } from '@angular/animations'
import { Component, HostBinding, Input, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ConfigurationsService, NsInstanceConfig } from '@ws-widget/utils'
import { BtnPageBackService } from './btn-page-back.service'
type TUrl = undefined | 'none' | 'back' | string
@Component({
  selector: 'ws-widget-btn-page-back',
  templateUrl: './btn-page-back.component.html',
  styleUrls: ['./btn-page-back.component.scss'],
  animations: [
    trigger(
      'enterAnimation', [
      transition(':enter', [
        style({ transition: 'visibility 0s linear 0.23s, opacity 0.33s linear', opacity: 0 }),
        animate('300ms', style({ transition: 'visibility 0s linear 0.23s, opacity 0.33s linear', opacity: 1 })),
      ]),
      transition(':leave', [
        style({ transition: 'visibility 0s linear 0.23s, opacity 0.33s linear', opacity: 1 }),
        animate('300ms', style({ transition: 'visibility 0s linear 0.23s, opacity 0.33s linear', opacity: 0 })),
      ]),
    ]
    ),
  ],
})
export class BtnPageBackComponent extends WidgetBaseComponent
  implements OnInit, NsWidgetResolver.IWidgetData<{ url: TUrl }> {
  @Input() widgetData: { url: TUrl, titles?: NsWidgetResolver.ITitle[] } = { url: 'none', titles: [] }
  presentUrl = ''
  @HostBinding('id')
  public id = 'nav-back'
  visible = false
  enablePeopleSearch = true
  hubsList!: NsInstanceConfig.IHubs[]
  constructor(
    private btnBackSvc: BtnPageBackService,
    private router: Router,
    private configSvc: ConfigurationsService,
  ) {
    super()
  }

  ngOnInit() {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.hubsList = (instanceConfig.hubs || []).filter(i => i.active)
    }
    this.presentUrl = this.router.url

  }

  get backUrl(): { fragment?: string; routeUrl: string; queryParams: any } {

    if (this.presentUrl === '/page/explore') {
      return {
        queryParams: undefined,
        routeUrl: '/app/home',
      }
    }
    if (this.widgetData.url === 'home') {
      return {
        queryParams: undefined,
        routeUrl: '/app/home',
      }
    }

    if (this.widgetData.url === 'doubleBack') {
      return {
        fragment: this.btnBackSvc.getLastUrl(2).fragment,
        queryParams: this.btnBackSvc.getLastUrl(2).queryParams,
        routeUrl: this.btnBackSvc.getLastUrl(2).route,
      }
    } if (this.widgetData.url === 'back') {
      return {
        fragment: this.btnBackSvc.getLastUrl().fragment,
        queryParams: this.btnBackSvc.getLastUrl().queryParams,
        routeUrl: this.btnBackSvc.getLastUrl().route,
      }
    }
    if (this.widgetData.url !== 'back' && this.widgetData.url !== 'doubleBack') {

      this.btnBackSvc.checkUrl(this.widgetData.url)

    }

    return {
      queryParams: undefined,
      routeUrl: this.widgetData.url ? this.widgetData.url : '/app/home',
    }
  }

  // get titleUrl(): { fragment?: string; routeUrl: string; queryParams: any } {
  //   return {
  //     queryParams: undefined,
  //     routeUrl: this.widgetData.url ? this.widgetData.url : '/app/home',
  //   }
  // }
  toggleVisibility() {
    this.visible = !this.visible
  }
}
