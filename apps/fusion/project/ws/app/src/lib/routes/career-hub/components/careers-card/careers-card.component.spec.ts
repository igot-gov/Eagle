import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CareersCardComponent } from './careers-card.component'

describe('CareersCardComponent', () => {
  let component: CareersCardComponent
  let fixture: ComponentFixture<CareersCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CareersCardComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CareersCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
