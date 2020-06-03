import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { IframeAnalyticsComponent } from './iframe-analytics.component'

describe('IframeAnalyticsComponent', () => {
  let component: IframeAnalyticsComponent
  let fixture: ComponentFixture<IframeAnalyticsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IframeAnalyticsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(IframeAnalyticsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
