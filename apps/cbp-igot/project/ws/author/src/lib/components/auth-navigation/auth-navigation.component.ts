import { AuthNavBarToggleService } from '@ws/author/src/lib/services/auth-nav-bar-toggle.service'
import { NsPage, ConfigurationsService } from '@ws-widget/utils'
import { AfterViewInit, Component, OnInit } from '@angular/core'
import { SafeUrl, DomSanitizer } from '@angular/platform-browser'
import { Router, NavigationEnd } from '@angular/router'
import { EditorContentService } from '../../routing/modules/editor/services/editor-content.service'
import { NSContent } from '../../interface/content'

@Component({
  selector: 'ws-auth-root-navigation',
  templateUrl: './auth-navigation.component.html',
  styleUrls: ['./auth-navigation.component.scss'],
  providers: [EditorContentService],
})
export class AuthNavigationComponent implements OnInit, AfterViewInit {

  appIcon: SafeUrl | null = null
  search = false
  primaryNavbar: Partial<NsPage.INavBackground> | null = null
  pageNavbar: Partial<NsPage.INavBackground> | null = null
  backData: any = { url: 'back' }
  canShow = true
  currentContentId = ''
  currentContentData!: NSContent.IContentMeta

  currentRout = 'Home'
  constructor(
    private domSanitizer: DomSanitizer,
    private configSvc: ConfigurationsService,
    private authNavBarSvc: AuthNavBarToggleService,
    private router: Router,
  ) {

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        if (event.url.indexOf('home') >= 0) {
          this.currentRout = 'Home'
        } else if (event.url.indexOf('create') >= 0) {
          this.currentRout = 'Create'
        } else if (event.url.indexOf('draft') >= 0) {
          this.currentRout = 'Drafts'
        } else if (event.url.indexOf('inreview') >= 0) {
          this.currentRout = 'Send for Review'
        } else if (event.url.indexOf('published') >= 0) {
          this.currentRout = 'Published'
        } else if (event.url.indexOf('unpublished') >= 0) {
          this.currentRout = 'Unpublished'
        } else if (event.url.indexOf('review') >= 0) {
          this.currentRout = 'Review'
        } else if (event.url.indexOf('publish') >= 0) {
          this.currentRout = 'Publish'
        } else {
          // this.currentRout = 'Content'
          this.currentRout = 'New CBP'
        }
        // author/my-content?status=draft
      }
    })
  }
  ngAfterViewInit(): void {
    // this.getCurrentId()
  }

  ngOnInit() {

    this.authNavBarSvc.toggleNavBar.subscribe(
      data => this.canShow = data,
    )
    if (this.configSvc.instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.logos.app,
      )
      this.primaryNavbar = this.configSvc.primaryNavBar
      this.pageNavbar = this.configSvc.pageNavBar
    }
  }
  back() {
    window.history.back()
  }

}
