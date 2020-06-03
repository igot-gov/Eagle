import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocBannerPathfindersComponent } from './app-toc-banner-pathfinders.component'

describe('AppTocBanner2Component', () => {
  let component: AppTocBannerPathfindersComponent
  let fixture: ComponentFixture<AppTocBannerPathfindersComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocBannerPathfindersComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocBannerPathfindersComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
