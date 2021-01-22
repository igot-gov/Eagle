import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { EventsHomeComponent } from './events-home.component'

describe('CareersHomeComponent', () => {
  let component: EventsHomeComponent
  let fixture: ComponentFixture<EventsHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EventsHomeComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EventsHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
