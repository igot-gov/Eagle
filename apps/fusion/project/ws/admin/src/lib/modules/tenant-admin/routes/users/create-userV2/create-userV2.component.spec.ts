import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CreateUserComponentV2 } from './create-user.component'

describe('CreateUserComponentV2', () => {
  let component: CreateUserComponentV2
  let fixture: ComponentFixture<CreateUserComponentV2>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CreateUserComponentV2],
    })
    .compileComponents()
  }))
  
  beforeEach(() => {
    fixture = TestBed.createComponent(CreateUserComponentV2)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
