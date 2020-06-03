import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CdpHomeComponent } from './cdp-home.component'

describe('CdpHomeComponent', () => {
  let component: CdpHomeComponent
  let fixture: ComponentFixture<CdpHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CdpHomeComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CdpHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
