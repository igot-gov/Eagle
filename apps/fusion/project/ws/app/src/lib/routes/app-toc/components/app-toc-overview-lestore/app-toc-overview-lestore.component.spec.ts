import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocOverviewLestoreComponent } from './app-toc-overview-lestore.component'

describe('AppTocOverview2Component', () => {
  let component: AppTocOverviewLestoreComponent
  let fixture: ComponentFixture<AppTocOverviewLestoreComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocOverviewLestoreComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocOverviewLestoreComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
