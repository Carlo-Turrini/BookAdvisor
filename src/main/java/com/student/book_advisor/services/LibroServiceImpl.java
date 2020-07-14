package com.student.book_advisor.services;


import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.dto.PrizeDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.BookRankRemovalInfoDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.data_persistency.model.entities.*;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@CrossOrigin(origins = "http://localhost:4200")
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepo;
    @Autowired
    private RecensioneRepository recensioneRepo;
    @Autowired
    private StorageService storageService;
    @Autowired
    private PrizeRepository prizeRepository;
    @Autowired
    private GenreJoinBookRepository genreJoinBookRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private SagaRepository sagaRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private AuthorJoinBookRepository authorJoinBookRepository;
    @Autowired
    private MyBooksRepository myBooksRepository;
    @Autowired
    private BookRankingService bookRankingService;
    @Autowired
    private BookRankingRepository bookRankingRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findAllBooks() {
        List<LibroCardDTO> allBooks = new ArrayList<LibroCardDTO>();
        allBooks = libroRepo.findAllBooks();
        for(LibroCardDTO book: allBooks) {
            /*Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);*/
            book.setGeneri(genreRepository.findGenresOfBook(book.getId()));
            book.setAutori(authorRepository.findAuthorsOfBook(book.getId()));
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        //allBooks.sort(Comparator.comparingDouble(LibroCardDTO::getOverallRating).reversed());
        return allBooks;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findAllBooksByGenre(String genere) {
        List<LibroCardDTO> allBooksByGenre = new ArrayList<LibroCardDTO>();
        allBooksByGenre = libroRepo.findLibriByGenere(genere);
        for(LibroCardDTO book: allBooksByGenre) {
            /*Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);*/
            book.setGeneri(genreRepository.findGenresOfBook(book.getId()));
            book.setAutori(authorRepository.findAuthorsOfBook(book.getId()));
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        //allBooksByGenre.sort(Comparator.comparingDouble(LibroCardDTO::getOverallRating).reversed());
        return allBooksByGenre;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Libro findBookById(Integer id) {
        return libroRepo.getOne(id);
    }

    //Toglibile
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public LibroDTO findBookByTitolo(String titolo) {
        LibroDTO book = libroRepo.findByTitolo(titolo);
        String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
        if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
            book.setCopertina(bookCoverPath);
        }
        else {
            book.setCopertina(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
        }
        return book;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public LibroDTO findBookDTOById(Integer id) {
        LibroDTO book = libroRepo.getBookById(id);
        String bookCoverPath = libroRepo.findBookCoverPath(id);
        if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
            book.setCopertina(bookCoverPath);
        }
        else {
            book.setCopertina(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
        }
        book.setGeneri(genreRepository.findGenresOfBook(book.getId()));
        book.setAutori(authorRepository.findAuthorsOfBook(book.getId()));
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUserPrincipal authUserPrincipal = null;
        if(principal instanceof AuthUserPrincipal) {
            authUserPrincipal = (AuthUserPrincipal) principal;
        }
        if(authUserPrincipal != null) {
            book.setBookShelf(myBooksRepository.getBookShelfByBookIDAndUserID(book.getId(), authUserPrincipal.getId()));
        }
        if(book.getTitoloSaga() != null && book.getTitoloSaga() != "") {
            book.setSaga(true);
        }
        //book.setOverallRating(recensioneRepo.getAverageRatingOfBook(id));
        return book;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Libro newBook(LibroFormDTO libroForm) {
        Libro book = new Libro();
        book.setTitolo(libroForm.getTitolo());
        book.setSinossi(libroForm.getSinossi());
        book.setPagine(libroForm.getPagine());
        book.setAnnoPubblicazione(libroForm.getAnnoPubblicazione());
        book = libroRepo.save(book);
        for(String gen : libroForm.getGeneri()) {
            Genre genre = genreRepository.findByGenre(gen);
            GenreJoinBook gjb = new GenreJoinBook();
            gjb.setBook(book);
            gjb.setGenre(genre);
            genreJoinBookRepository.save(gjb);
        }
        if(libroForm.getSaga()) {
            Saga saga = new Saga();
            saga.setBook(book);
            saga.setNumberInSaga(libroForm.getNumInSaga());
            saga.setSagaTitle(libroForm.getTitoloSaga());
            sagaRepository.save(saga);
        }
        for(AuthorOfBook author : libroForm.getAutori()) {
            Author auth = authorRepository.getOne(author.getId());
            if(auth != null) {
                AuthorJoinBook ajb = new AuthorJoinBook();
                ajb.setAuthor(auth);
                ajb.setBook(book);
                authorJoinBookRepository.save(ajb);
            }
            else throw new ApplicationException("This author doesn't exist!");
        }
        return book;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Libro updateBook(LibroFormDTO libroForm, Integer bookID) {
        Libro book = libroRepo.getOne(bookID);
        if(book != null) {
            book.setTitolo(libroForm.getTitolo());
            book.setSinossi(libroForm.getSinossi());
            book.setPagine(libroForm.getPagine());
            book.setAnnoPubblicazione(libroForm.getAnnoPubblicazione());
            book = libroRepo.save(book);
            List<String> genresUpdated = libroForm.getGeneri();
            List<String> genresOfBook = genreRepository.findGenresOfBook(bookID);
            for(String genre: genresOfBook) {
                for(String genreUpdated: genresUpdated) {
                    if(genre==genreUpdated) {
                        genresOfBook.remove(genre);
                        genresUpdated.remove(genreUpdated);
                    }
                }
            }
            for(String genre: genresOfBook) {
                Genre gen = genreRepository.findByGenre(genre);
                if(gen != null) {
                    GenreJoinBook gjb = genreJoinBookRepository.findByGenreAndBookID(genre, bookID);
                    if(gjb != null) {
                        genreJoinBookRepository.delete(gjb);
                    }
                }
            }
            for(String genreUpdated: genresUpdated) {
                Genre gen = genreRepository.findByGenre(genreUpdated);
                if(gen!=null) {
                    GenreJoinBook gjb = genreJoinBookRepository.findByGenreAndBookID(genreUpdated, bookID);
                    if(gjb == null) {
                        gjb = new GenreJoinBook();
                        gjb.setGenre(gen);
                        gjb.setBook(book);
                        genreJoinBookRepository.save(gjb);
                    }
                }
            }
            if (libroForm.getSaga()) {
                Saga saga = sagaRepository.findByBook(book);
                if (saga == null) {
                    saga = new Saga();
                    saga.setBook(book);
                }
                saga.setNumberInSaga(libroForm.getNumInSaga());
                saga.setSagaTitle(libroForm.getTitoloSaga());
                sagaRepository.save(saga);
            } else {
                Saga saga = sagaRepository.findByBook(book);
                if (saga != null) {
                    sagaRepository.delete(saga);
                }
            }
            List<AuthorOfBook> authorList = authorRepository.findAuthorsOfBook(bookID);
            List<AuthorOfBook> authorUpdatedList = libroForm.getAutori();
            for(AuthorOfBook author: authorList) {
                for(AuthorOfBook authUpdated: authorUpdatedList) {
                    if(author.getAuthorsFullname()==authUpdated.getAuthorsFullname() && author.getId()==authUpdated.getId()) {
                        authorList.remove(author);
                        authorUpdatedList.remove(authUpdated);
                    }
                }
            }
            for(AuthorOfBook author: authorList) {
                Author auth = authorRepository.getOne(author.getId());
                if(auth!=null) {
                    AuthorJoinBook ajb = authorJoinBookRepository.findByAuthorAndBook(auth, book);
                    if(ajb != null) {
                        authorJoinBookRepository.delete(ajb);
                    }
                }
            }
            for(AuthorOfBook authUpdated: authorUpdatedList) {
                Author auth = authorRepository.getOne(authUpdated.getId());
                if(auth!=null) {
                    AuthorJoinBook ajb = authorJoinBookRepository.findByAuthorAndBook(auth, book);
                    if(ajb == null) {
                        ajb = new AuthorJoinBook();
                        ajb.setAuthor(auth);
                        ajb.setBook(book);
                        authorJoinBookRepository.save(ajb);
                    }
                }
            }
            return book;
        }
        else return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBook(Integer id) {
        Libro delBook = libroRepo.getOne(id);
        if(delBook != null) {
            /*delBook.setDelToken(UUID.randomUUID().toString());
            libroRepo.save(delBook);*/
            List<BookRankRemovalInfoDTO> bookRankingInfoList = bookRankingRepository.getAllBookRanksByBookID(id);
            for(BookRankRemovalInfoDTO bookRankInfo : bookRankingInfoList) {
                bookRankingService.removeBookFromBookRank(bookRankInfo.getUserID(), bookRankInfo.getBookRankID());
            }
            String photoPath = delBook.getBookCoverPath();
            if(photoPath != Constants.DEF_BOOK_COVER) {
                storageService.delete(photoPath, FileUploadDir.coverImage);
            }
            libroRepo.delete(delBook);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isTitleUnique(String titolo) {
        return libroRepo.countAllByTitolo(titolo) == 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateBooksCoverPhoto(MultipartFile coverPhoto, Libro book) {
        String coverPath = null;
        if(!book.getBookCoverPath().equals(Constants.DEF_BOOK_COVER)) {
            coverPath = book.getBookCoverPath();
        }
        String filePath = storageService.store(coverPhoto, FileUploadDir.coverImage, coverPath);
        book.setBookCoverPath(filePath);
        libroRepo.save(book);
        String src = "{ \"img\":\"" + storageService.serve(filePath, FileUploadDir.coverImage) + "\"}";
        return src;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findBooksContainingTitolo(String titolo) {
        List<LibroCardDTO> allBooksContainingTitolo = new ArrayList<LibroCardDTO>();
        allBooksContainingTitolo = libroRepo.findAllBooksContainingTitolo(titolo);
        for(LibroCardDTO book: allBooksContainingTitolo) {
            /*Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);*/
            book.setGeneri(genreRepository.findGenresOfBook(book.getId()));
            book.setAutori(authorRepository.findAuthorsOfBook(book.getId()));
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        //allBooksContainingTitolo.sort(Comparator.comparingDouble(LibroCardDTO::getOverallRating).reversed());
        return allBooksContainingTitolo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findAllBooksByTitoloSagaExcludingCurrent(String titoloSaga, Integer bookId) {
        List<LibroCardDTO> sagaBooks = new ArrayList<LibroCardDTO>();
        sagaBooks = this.libroRepo.findAllBooksByTitoloSagaExcludingCurrent(titoloSaga, bookId);
        for(LibroCardDTO book: sagaBooks) {
            /*Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);*/
            book.setGeneri(genreRepository.findGenresOfBook(book.getId()));
            book.setAutori(authorRepository.findAuthorsOfBook(book.getId()));
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        return sagaBooks;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Double getBookOverallRating(Integer id) {
        return this.recensioneRepo.getAverageRatingOfBook(id);
    }

    @Override
    public OverallRatingsForBook getBookOverallRatings(Integer bookID) {
        return recensioneRepo.getAverageRatingsOfBook(bookID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<PrizeDTO> addPrize(PrizeDTO prize, Integer bookID) {
        Libro book = libroRepo.getOne(bookID);
        if(book != null) {
            Prize p = prizeRepository.findByBookIDAndPrizeName(bookID, prize.getPrizeName());
            if(p == null) {
                Prize addPrize = new Prize();
                addPrize.setBook(book);
                addPrize.setYearAwarded(prize.getYearAwarded());
                addPrize.setPrizeName(prize.getPrizeName());
                prizeRepository.save(addPrize);
                return prizeRepository.findAllPrizesForBook(bookID);
            }
            else return null;
        }
        else return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<PrizeDTO> removePrize(Integer prizeID, Integer bookID) {
        Libro book = libroRepo.getOne(bookID);
        if(book != null) {
            Prize p = prizeRepository.findByIdAndBookID(prizeID, bookID);
            if(p != null) {
                prizeRepository.delete(p);
                return prizeRepository.findAllPrizesForBook(bookID);
            }
            else return null;
        }
        else return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findAllBooksByAuthor(String authorsFullname) {
        List<LibroCardDTO> booksByAuthor = libroRepo.findAllBooksByAuthor(authorsFullname);
        for(LibroCardDTO book : booksByAuthor) {
            book.setGeneri(genreRepository.findGenresOfBook(book.getId()));
            book.setAutori(authorRepository.findAuthorsOfBook(book.getId()));
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        return booksByAuthor;
    }
}
