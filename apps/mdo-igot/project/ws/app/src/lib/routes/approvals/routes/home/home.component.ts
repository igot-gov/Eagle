import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core'
import { Router } from '@angular/router'
@Component({
  selector: 'ws-app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, AfterViewInit {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  approval: any
  tabsData!: any[]
  currentTab = 'needsapproval'
  sticky = false
  elementPosition: any

  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = true
    } else {
      this.sticky = false
    }
  }
  constructor(private router: Router) { }

  ngOnInit() {
    const url = this.router.url.split('/')
    this.approval = url[url.length - 2]
    this.tabsData = [{
      name: 'Needs approval',
      key: 'needsapproval',
      render: true,
      enabled: true,
      count: 0,
    },
    {
      name: 'Basic Info',
      key: 'basicinfo',
      render: true,
      enabled: true,
    },
    {
      name: 'Position',
      key: 'position',
      render: true,
      enabled: true,
    },
    {
      name: 'Education',
      key: 'education',
      render: true,
      enabled: true,
    },
    {
      name: 'Role',
      key: 'role',
      render: true,
      enabled: true,
    }]
  }

  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  onSideNavTabClick(id: string) {
    this.currentTab = id
    const el = document.getElementById(id)
    if (el != null) {
      el.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  }
}
