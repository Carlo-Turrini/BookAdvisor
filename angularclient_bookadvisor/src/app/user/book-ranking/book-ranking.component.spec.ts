import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookRankingComponent } from './book-ranking.component';

xdescribe('BookRankingComponent', () => {
  let component: BookRankingComponent;
  let fixture: ComponentFixture<BookRankingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BookRankingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookRankingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
