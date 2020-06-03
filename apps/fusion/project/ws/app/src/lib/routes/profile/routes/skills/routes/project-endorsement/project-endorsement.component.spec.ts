import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ProjectEndorsementComponent } from './project-endorsement.component'

describe('ProjectEndorsementComponent', () => {
  let component: ProjectEndorsementComponent
  let fixture: ComponentFixture<ProjectEndorsementComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectEndorsementComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectEndorsementComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
