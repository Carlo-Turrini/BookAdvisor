INSERT INTO UsersInfo (Description, Email, Name, Password, Surname, Username) VALUES ('prova', 'mario.rossi@gmail.com', 'Mario', '$2y$10$XVs64DDkV5WmmA8X8MrwCOzTRfM8zeLrf1BhwN.M7RZglXPgKSzmC', 'Rossi', 'MarioRossi');
INSERT INTO Authorities (Authority, UserID) VALUES ('USER', 1);
INSERT INTO Authorities (Authority, UserID) VALUES ('ADMIN', 1);
INSERT INTO UsersInfo (Description, Email, Name, Password, Surname, Username) VALUES ('prova', 'carlo.turrini@gmail.com', 'Carlo', '$2y$10$XVs64DDkV5WmmA8X8MrwCOzTRfM8zeLrf1BhwN.M7RZglXPgKSzmC', 'Turrini', 'Tarlo');
INSERT INTO Authorities (Authority, UserID) VALUES ('USER', 2);
INSERT INTO Book (PubblicationYear, NumberOfPages, Synopsis, Title) VALUES (1923, 123, 'sinossi', 'Titolo');
INSERT INTO Prize (PrizeName, YearAwarded, BookID) VALUES ('Premio Strega', 2002, 1);
INSERT INTO Genre (Genre) VALUES ('Romanzo');
INSERT INTO Genre (Genre) VALUES ('Fantascienza');
INSERT INTO Genre (Genre) VALUES ('Fantasy');
INSERT INTO Genre (Genre) VALUES ('Biografia');
INSERT INTO Author (AuthorsFullname, Biography, BirthYear) VALUES ('Pablo Neruda', 'prova', 1900);
INSERT INTO AuthorJoinBook (AuthorID, BookID) VALUES (1, 1);
INSERT INTO Author (AuthorsFullname, Biography, BirthYear) VALUES ('Robert Jordan', 'prova', 1900);
INSERT INTO Book (PubblicationYear, NumberOfPages, Synopsis, Title) VALUES (1923, 123, 'sinossi', 'La ruota del tempo');
INSERT INTO AuthorJoinBook (AuthorID, BookID) VALUES (2, 2);
INSERT INTO Book (PubblicationYear, NumberOfPages, Synopsis, Title) VALUES (1923, 123, 'sinossi', 'Titolo 2');
INSERT INTO AuthorJoinBook (AuthorID, BookID) VALUES (1, 3);
INSERT INTO GenreJoinBook (BookID, GenreID) VALUES (1, 1);
INSERT INTO GenreJoinBook (BookID, GenreID) VALUES (2, 1);
INSERT INTO GenreJoinBook (BookID, GenreID) VALUES (3, 1);
INSERT INTO Saga (NumberInSaga, SagaTitle, BookID) VALUES (1, 'Saga', 1);
INSERT INTO Saga (NumberInSaga, SagaTitle, BookID) VALUES (2, 'Saga', 3);
INSERT INTO Review (OriginalityRating, PageTurnerRating, OverallRating, text, WritingQualityRating, BookID, UserID, CreationDate) VALUES (4, 4, 5, 'prova', 4, 1, 2, CURRENT_TIMESTAMP);
INSERT INTO UsefulReview (ReviewID, UserID) VALUES (1, 2);
INSERT INTO MyBooks (ShelfType, BookID, UserID) VALUES ('read', 1, 2);
INSERT INTO MyBooks (ShelfType, BookID, UserID) VALUES ('read', 2, 2);
INSERT INTO BookRanking (BookRank, MyBooksID) VALUES (1, 1);
INSERT INTO MyBooks (ShelfType, BookID, UserID) VALUES ('read', 3, 1);
INSERT INTO BookRanking (BookRank, MyBooksID) VALUES (1, 3);






