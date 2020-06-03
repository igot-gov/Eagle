import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { KbDetailComponent } from './kb-detail.component'

describe('KbDetailComponent', () => {
  let component: KbDetailComponent
  let fixture: ComponentFixture<KbDetailComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [KbDetailComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(KbDetailComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
