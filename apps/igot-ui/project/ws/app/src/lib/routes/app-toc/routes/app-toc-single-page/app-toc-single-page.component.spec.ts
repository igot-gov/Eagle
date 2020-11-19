import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocSinglePageComponent } from './app-toc-single-page.component'

describe('AppTocOverviewHomeComponent', () => {
  let component: AppTocSinglePageComponent
  let fixture: ComponentFixture<AppTocSinglePageComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocSinglePageComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocSinglePageComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
