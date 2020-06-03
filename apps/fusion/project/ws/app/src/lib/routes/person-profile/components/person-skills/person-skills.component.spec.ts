import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { PersonSkillsComponent } from './person-skills.component'

describe('PersonSkillsComponent', () => {
  let component: PersonSkillsComponent
  let fixture: ComponentFixture<PersonSkillsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PersonSkillsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonSkillsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
