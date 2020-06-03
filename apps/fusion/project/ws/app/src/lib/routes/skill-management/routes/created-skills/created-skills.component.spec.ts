import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CreatedSkillsComponent } from './created-skills.component'

describe('CreatedSkillsComponent', () => {
  let component: CreatedSkillsComponent
  let fixture: ComponentFixture<CreatedSkillsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CreatedSkillsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CreatedSkillsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
