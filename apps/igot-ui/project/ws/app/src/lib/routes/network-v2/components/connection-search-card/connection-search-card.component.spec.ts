import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConnectionSearchCardComponent } from './connection-search-card.component'

describe('ConnectionSearchCardComponent', () => {
  let component: ConnectionSearchCardComponent
  let fixture: ComponentFixture<ConnectionSearchCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionSearchCardComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectionSearchCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
