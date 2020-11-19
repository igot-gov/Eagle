import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CardLearnComponent } from './card-learn.component'

describe('CardLearnComponent', () => {
  let component: CardLearnComponent
  let fixture: ComponentFixture<CardLearnComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CardLearnComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CardLearnComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
