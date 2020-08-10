import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { StatusContentDisplayComponent } from './status-content-display.component'

describe('StatusContentDisplayComponent', () => {
  let component: StatusContentDisplayComponent
  let fixture: ComponentFixture<StatusContentDisplayComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StatusContentDisplayComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusContentDisplayComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
