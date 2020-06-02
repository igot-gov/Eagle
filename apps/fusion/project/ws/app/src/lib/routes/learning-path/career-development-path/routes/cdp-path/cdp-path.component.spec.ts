import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CdpPathComponent } from './cdp-path.component'

describe('CdpPathComponent', () => {
  let component: CdpPathComponent
  let fixture: ComponentFixture<CdpPathComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CdpPathComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CdpPathComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
