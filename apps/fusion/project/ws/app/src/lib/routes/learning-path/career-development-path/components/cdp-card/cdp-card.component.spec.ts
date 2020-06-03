import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CdpCardComponent } from './cdp-card.component'

describe('CdpCardComponent', () => {
  let component: CdpCardComponent
  let fixture: ComponentFixture<CdpCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CdpCardComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CdpCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
