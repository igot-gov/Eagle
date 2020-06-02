import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CopyRoleComponent } from './copy-role.component'

describe('CopyRoleComponent', () => {
  let component: CopyRoleComponent
  let fixture: ComponentFixture<CopyRoleComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CopyRoleComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyRoleComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
