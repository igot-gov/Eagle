import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { LoginSiemensComponent } from './login-siemsns.component'

describe('LoginSiemensComponent', () => {
  let component: LoginSiemensComponent
  let fixture: ComponentFixture<LoginSiemensComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LoginSiemensComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginSiemensComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
