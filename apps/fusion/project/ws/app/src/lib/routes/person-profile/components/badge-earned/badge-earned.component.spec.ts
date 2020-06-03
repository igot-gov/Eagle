import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { BadgeEarnedComponent } from './badge-earned.component'

describe('BadgeEarnedComponent', () => {
  let component: BadgeEarnedComponent
  let fixture: ComponentFixture<BadgeEarnedComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BadgeEarnedComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(BadgeEarnedComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
