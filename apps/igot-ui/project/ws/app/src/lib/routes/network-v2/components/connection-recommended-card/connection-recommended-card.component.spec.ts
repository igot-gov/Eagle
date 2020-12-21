import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConnectionRecommendedCardComponent } from './connection-recommended-card.component'

describe('ConnectionRecommendedCardComponent', () => {
  let component: ConnectionRecommendedCardComponent
  let fixture: ComponentFixture<ConnectionRecommendedCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionRecommendedCardComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectionRecommendedCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
