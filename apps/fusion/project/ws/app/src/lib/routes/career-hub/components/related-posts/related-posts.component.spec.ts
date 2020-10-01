import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedPostsComponent } from './related-posts.component';

describe('RelatedPostsComponent', () => {
  let component: RelatedPostsComponent;
  let fixture: ComponentFixture<RelatedPostsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RelatedPostsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
