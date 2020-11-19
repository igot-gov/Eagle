import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AddWebPagesComponent } from './add-web-pages.component'

describe('AddWebPagesComponent', () => {
  let component: AddWebPagesComponent
  let fixture: ComponentFixture<AddWebPagesComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddWebPagesComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AddWebPagesComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
