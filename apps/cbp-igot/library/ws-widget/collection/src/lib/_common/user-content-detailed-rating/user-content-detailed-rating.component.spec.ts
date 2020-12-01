import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { UserContentDetailedRatingComponent } from './user-content-detailed-rating.component'

describe('UserContentDetailedRatingComponent', () => {
  let component: UserContentDetailedRatingComponent
  let fixture: ComponentFixture<UserContentDetailedRatingComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserContentDetailedRatingComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(UserContentDetailedRatingComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
