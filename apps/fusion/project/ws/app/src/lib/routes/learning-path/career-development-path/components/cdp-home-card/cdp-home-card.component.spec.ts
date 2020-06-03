import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CdpHomeCardComponent } from './cdp-home-card.component'

describe('CdpHomeCardComponent', () => {
  let component: CdpHomeCardComponent
  let fixture: ComponentFixture<CdpHomeCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CdpHomeCardComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CdpHomeCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
