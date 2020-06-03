import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { SkillsHomeComponent } from './skills-home.component'

describe('SkillsHomeComponent', () => {
  let component: SkillsHomeComponent
  let fixture: ComponentFixture<SkillsHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SkillsHomeComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(SkillsHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
