import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CreateHomeComponent } from './create-home.component'

describe('CreateHomeComponent', () => {
  let component: CreateHomeComponent
  let fixture: ComponentFixture<CreateHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CreateHomeComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})