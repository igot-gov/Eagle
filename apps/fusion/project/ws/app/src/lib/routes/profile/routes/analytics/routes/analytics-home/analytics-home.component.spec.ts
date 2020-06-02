import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AnalyticsHomeComponent } from './analytics-home.component'

describe('AnalyticsHomeComponent', () => {
  let component: AnalyticsHomeComponent
  let fixture: ComponentFixture<AnalyticsHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AnalyticsHomeComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalyticsHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
