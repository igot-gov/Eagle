import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CdpLandingComponent } from './cdp-landing.component'

describe('CdpLandingComponent', () => {
  let component: CdpLandingComponent
  let fixture: ComponentFixture<CdpLandingComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CdpLandingComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CdpLandingComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
