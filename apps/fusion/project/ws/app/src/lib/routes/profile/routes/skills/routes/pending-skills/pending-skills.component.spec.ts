import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { PendingSkillsComponent } from './pending-skills.component'

describe('PendingSkillsComponent', () => {
  let component: PendingSkillsComponent
  let fixture: ComponentFixture<PendingSkillsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PendingSkillsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(PendingSkillsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
