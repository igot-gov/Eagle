import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ShareRoleComponent } from './share-role.component'

describe('ShareRoleComponent', () => {
  let component: ShareRoleComponent
  let fixture: ComponentFixture<ShareRoleComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ShareRoleComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareRoleComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
