import { Component, Input, OnInit, OnDestroy, HostBinding } from '@angular/core'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ConfigurationsService, LogoutComponent, NsPage } from '@ws-widget/utils/src/public-api'
import { IBtnAppsConfig } from '../btn-apps/btn-apps.model'
import { MatDialog } from '@angular/material'
import { Subscription } from 'rxjs'
import { ROOT_WIDGET_CONFIG } from '../collection.config'

@Component({
  selector: 'ws-widget-btn-profile',
  templateUrl: './btn-profile.component.html',
  styleUrls: ['./btn-profile.component.scss'],
})
export class BtnProfileComponent extends WidgetBaseComponent
  implements OnInit, OnDestroy, NsWidgetResolver.IWidgetData<any> {
  @HostBinding('id')
  public id = 'Profile_link'
  @Input() widgetData!: any
  @HostBinding('class')
  public class = 'profile-link'
  basicBtnAppsConfig: NsWidgetResolver.IRenderConfigWithTypedData<IBtnAppsConfig> = {
    widgetType: 'actionButton',
    widgetSubType: 'actionButtonApps',
    widgetData: { allListingUrl: '/app/features' },
  }
  settingBtnConfig: NsWidgetResolver.IRenderConfigWithTypedData<IBtnAppsConfig> = {
    widgetType: 'actionButton',
    widgetSubType: 'actionButtonSetting',
    widgetData: { allListingUrl: '/app/features' },
  }
  isPinFeatureAvailable = true
  pinnedApps: NsWidgetResolver.IRenderConfigWithTypedData<NsPage.INavLink>[] = []

  btnAppsConfig!: NsWidgetResolver.IRenderConfigWithTypedData<IBtnAppsConfig>
  btnSettingsConfig!: NsWidgetResolver.IRenderConfigWithTypedData<IBtnAppsConfig>
  private pinnedAppsSubs?: Subscription
  givenName = 'Guest'
  profileImage!: string | null
  constructor(
    private configSvc: ConfigurationsService,
    private dialog: MatDialog
  ) {
    super()
    this.btnAppsConfig = { ...this.basicBtnAppsConfig }
    this.btnSettingsConfig = { ... this.settingBtnConfig }
    if (this.configSvc.userProfile) {
      this.givenName = this.configSvc.userProfile.givenName || ''
      // this.profileImage = this.configSvc.userProfile.source_profile_picture || null
      if (localStorage.getItem(this.configSvc.userProfile.userId)) {
        this.profileImage = localStorage.getItem(this.configSvc.userProfile.userId)
      }
    }
  }

  ngOnInit() {
    this.setPinnedApps()
    if (this.widgetData && this.widgetData.actionBtnId) {
      this.id = this.widgetData.actionBtnId
    }
  }

  ngOnDestroy() {
    if (this.pinnedAppsSubs) {
      this.pinnedAppsSubs.unsubscribe()
    }
  }

  logout() {
    this.dialog.open<LogoutComponent>(LogoutComponent)
  }

  setPinnedApps() {
    this.pinnedAppsSubs = this.configSvc.pinnedApps.subscribe(pinnedApps => {
      const appsConfig = this.configSvc.appsConfig
      if (!appsConfig) {
        return
      }
      this.pinnedApps = Array.from(pinnedApps)
        .filter(id => id in appsConfig.features)
        .map(id => ({
          widgetType: ROOT_WIDGET_CONFIG.actionButton._type,
          widgetSubType: ROOT_WIDGET_CONFIG.actionButton.feature,
          widgetHostClass: 'w-1/3 px-2 py-3 box-sizing-box',
          widgetData: {
            config: {
              type: 'feature-item',
              useShortName: true,
            },
            actionBtn: appsConfig.features[id],
          },
        }))
    })
  }
}
