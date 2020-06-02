import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CreateProjectEndorsementComponent } from './create-project-endorsement.component'

describe('CreateProjectEndorsementComponent', () => {
  let component: CreateProjectEndorsementComponent
  let fixture: ComponentFixture<CreateProjectEndorsementComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CreateProjectEndorsementComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateProjectEndorsementComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
