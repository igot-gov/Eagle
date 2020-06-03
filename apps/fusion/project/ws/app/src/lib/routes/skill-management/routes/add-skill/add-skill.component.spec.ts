import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AddSkillComponent } from './add-skill.component'

describe('AddSkillComponent', () => {
  let component: AddSkillComponent
  let fixture: ComponentFixture<AddSkillComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddSkillComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AddSkillComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
