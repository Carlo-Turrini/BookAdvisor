import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookRankingFormComponent } from './book-ranking-form.component';

xdescribe('BookRankingFormComponent', () => {
  let component: BookRankingFormComponent;
  let fixture: ComponentFixture<BookRankingFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BookRankingFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookRankingFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
