import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { EndorsedSkillsComponent } from './endorsed-skills.component'

describe('EndorsedSkillsComponent', () => {
  let component: EndorsedSkillsComponent
  let fixture: ComponentFixture<EndorsedSkillsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EndorsedSkillsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EndorsedSkillsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
