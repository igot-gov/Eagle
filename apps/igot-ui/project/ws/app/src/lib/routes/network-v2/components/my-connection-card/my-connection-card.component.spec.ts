import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { MyConnectionCardComponent } from './my-connection-card.component'

describe('MyConnectionCardComponent', () => {
  let component: MyConnectionCardComponent
  let fixture: ComponentFixture<MyConnectionCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MyConnectionCardComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(MyConnectionCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
