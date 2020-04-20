package com.student.book_advisor.services;


import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.dto.LibroCardDTO;
import com.student.book_advisor.dto.LibroDTO;
import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.entityRepositories.LibroRepository;
import com.student.book_advisor.entityRepositories.RecensioneRepository;
import com.student.book_advisor.enums.FileUploadDir;
import com.student.book_advisor.enums.GenereLibro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@CrossOrigin(origins = "http://localhost:4200")
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepo;
    @Autowired
    private RecensioneRepository recensioneRepo;
    @Autowired
    private StorageService storageService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findAllBooks() {
        List<LibroCardDTO> allBooks = new ArrayList<LibroCardDTO>();
        allBooks = libroRepo.findAllBooks();
        for(LibroCardDTO book: allBooks) {
            Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        allBooks.sort(Comparator.comparingDouble(LibroCardDTO::getOverallRating).reversed());
        return allBooks;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findAllBooksByGenre(GenereLibro genere) {
        List<LibroCardDTO> allBooksByGenre = new ArrayList<LibroCardDTO>();
        allBooksByGenre = libroRepo.findLibriByGenere(genere);
        for(LibroCardDTO book: allBooksByGenre) {
            Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        allBooksByGenre.sort(Comparator.comparingDouble(LibroCardDTO::getOverallRating).reversed());
        return allBooksByGenre;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Libro findBookById(Long id) {
        return libroRepo.getOne(id);
    }

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
    public LibroDTO findBookDTOById(Long id) {
        LibroDTO book = libroRepo.getBookById(id);
        String bookCoverPath = libroRepo.findBookCoverPath(id);
        if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
            book.setCopertina(bookCoverPath);
        }
        else {
            book.setCopertina(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
        }
        book.setOverallRating(recensioneRepo.getAverageRatingOfBook(id));
        return book;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Libro newBook(Libro newBook) {
        return libroRepo.save(newBook);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Libro updateBook(Libro updatedBook) {
        return libroRepo.save(updatedBook);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBook(Long id) {
        Libro delBook = libroRepo.getOne(id);
        if(delBook != null) {
            delBook.setDelToken(UUID.randomUUID().toString());
            libroRepo.save(delBook);
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
            Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);
            String bookCoverPath = libroRepo.findBookCoverPath(book.getId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        allBooksContainingTitolo.sort(Comparator.comparingDouble(LibroCardDTO::getOverallRating).reversed());
        return allBooksContainingTitolo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<LibroCardDTO> findAllBooksByTitoloSagaExcludingCurrent(String titoloSaga, Long bookId) {
        List<LibroCardDTO> sagaBooks = new ArrayList<LibroCardDTO>();
        sagaBooks = this.libroRepo.findAllBooksByTitoloSagaExcludingCurrent(titoloSaga, bookId);
        for(LibroCardDTO book: sagaBooks) {
            Double rating = recensioneRepo.getAverageRatingOfBook(book.getId());
            if(rating == null) {
                book.setOverallRating(new Double(0));
            }
            else book.setOverallRating(rating);
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
    public Double getBookOverallRating(Long id) {
        return this.recensioneRepo.getAverageRatingOfBook(id);
    }
}
