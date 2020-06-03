import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { CohortsMiniprofileComponent } from './cohorts-miniprofile.component'
describe('CohortsMiniprofileComponent', () => {
  let component: CohortsMiniprofileComponent
  let fixture: ComponentFixture<CohortsMiniprofileComponent>
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CohortsMiniprofileComponent],
    })
      .compileComponents()
  }))
  beforeEach(() => {
    fixture = TestBed.createComponent(CohortsMiniprofileComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })
  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
