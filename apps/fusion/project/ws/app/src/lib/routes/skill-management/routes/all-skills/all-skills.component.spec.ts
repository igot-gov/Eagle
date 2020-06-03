import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AllSkillsComponent } from './all-skills.component'

describe('AllSkillsComponent', () => {
  let component: AllSkillsComponent
  let fixture: ComponentFixture<AllSkillsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AllSkillsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AllSkillsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
