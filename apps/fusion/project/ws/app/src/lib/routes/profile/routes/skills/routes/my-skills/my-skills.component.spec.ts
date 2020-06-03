import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { MySkillsComponent } from './my-skills.component'

describe('MySkillsComponent', () => {
  let component: MySkillsComponent
  let fixture: ComponentFixture<MySkillsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MySkillsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(MySkillsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
