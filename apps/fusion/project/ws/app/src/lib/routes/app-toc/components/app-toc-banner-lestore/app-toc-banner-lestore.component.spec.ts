import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocBannerLestoreComponent } from './app-toc-banner-lestore.component'

describe('AppTocBanner3Component', () => {
  let component: AppTocBannerLestoreComponent
  let fixture: ComponentFixture<AppTocBannerLestoreComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocBannerLestoreComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocBannerLestoreComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
