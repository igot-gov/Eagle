import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AvailableCardComponent } from './available-card.component'

describe('AvailableCardComponent', () => {
  let component: AvailableCardComponent
  let fixture: ComponentFixture<AvailableCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AvailableCardComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AvailableCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
