import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppChatbotComponent } from './app-chatbot.component'

describe('AppChatbotComponent', () => {
  let component: AppChatbotComponent
  let fixture: ComponentFixture<AppChatbotComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppChatbotComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppChatbotComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
