import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CopySkillComponent } from './copy-skill.component'

describe('CopySkillComponent', () => {
  let component: CopySkillComponent
  let fixture: ComponentFixture<CopySkillComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CopySkillComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CopySkillComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
