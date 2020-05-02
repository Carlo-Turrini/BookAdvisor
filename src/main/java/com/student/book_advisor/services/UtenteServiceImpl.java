package com.student.book_advisor.services;

import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.dto.*;
import com.student.book_advisor.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.entities.*;
import com.student.book_advisor.entityRepositories.*;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.enums.Credenziali;
import com.student.book_advisor.enums.FileUploadDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class UtenteServiceImpl implements UtenteService {
    @Autowired
    private StorageService storageService;
    @Autowired
    private MyBooksRepository myBooksRepository;
    @Autowired
    private BookRankingRepository bookRankingRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<UtenteCardDTO> findAllUsers() {
        List<UtenteCardDTO> utenti = usersInfoRepository.findAllUsers();
        for (UtenteCardDTO utente : utenti) {
            String fotoProfiloPath = usersInfoRepository.getUserProfilePhotoPath(utente.getId());
            if(fotoProfiloPath.equals(Constants.DEF_PROFILE_PIC)) {
                utente.setProfileImage(fotoProfiloPath);
            }
            else {
                utente.setProfileImage(storageService.serve(fotoProfiloPath, FileUploadDir.profileImage));
            }
        }
        return utenti;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfoDTO findById(Long id) {
        UsersInfoDTO user = usersInfoRepository.findUserById(id);
        String fotoProfiloPath = usersInfoRepository.getUserProfilePhotoPath(id);
        if(fotoProfiloPath.equals(Constants.DEF_PROFILE_PIC)) {
            user.setProfilePhoto(fotoProfiloPath);
        }
        else {
            user.setProfilePhoto(storageService.serve(fotoProfiloPath, FileUploadDir.profileImage));
        }
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo getUser(Long userId) {
        return usersInfoRepository.getOne(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo newUser(UtenteFormDTO userForm) {
        UsersInfo newUser = new UsersInfo();
        newUser.setName(userForm.getNome());
        newUser.setSurname(userForm.getCognome());
        newUser.setUsername(userForm.getUsername());
        newUser.setPassword(userForm.getPassword());
        newUser.setEmail(userForm.getEmail());
        newUser.setDescription(userForm.getDescrizione());
        newUser = usersInfoRepository.save(newUser);
        //Set newUsers authority in application domain.
        Authorities authority = new Authorities();
        authority.setAuthority("USER");
        authority.setUsersInfo(newUser);
        authoritiesRepository.save(authority);
        return newUser;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo updateUser(UsersInfo updatedUser, UtenteFormDTO userForm) {
        updatedUser.setName(userForm.getNome());
        updatedUser.setSurname(userForm.getCognome());
        updatedUser.setPassword(userForm.getPassword());
        updatedUser.setEmail(userForm.getEmail());
        updatedUser.setDescription(userForm.getDescrizione());
        return usersInfoRepository.save(updatedUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long id) {
        UsersInfo user = usersInfoRepository.getOne(id);
        if(user != null) {
            //user.setDelToken(UUID.randomUUID().toString());
            usersInfoRepository.save(user);
        }
    }

    /*@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo findUserToLogin(String username, String password) {
        return usersInfoRepository.findByUsernameAndPassword(username, password);
    }*/

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isUsernameUnique(String username) {
        Integer count = usersInfoRepository.countAllByUsername(username);
        if(count == null || count == 0) {
            return true;
        }
        else return false;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isEmailUnique(String email) {
        Integer count = usersInfoRepository.countAllByEmail(email);
        if(count == null || count == 0) {
            return true;
        }
        else return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateUsersProfilePhoto(MultipartFile profilePhoto, UsersInfo user) {
        String profilePath = null;
        if(!user.getProfilePhotoPath().equals(Constants.DEF_PROFILE_PIC)) {
            profilePath = user.getProfilePhotoPath();
        }
        String filePath = storageService.store(profilePhoto, FileUploadDir.profileImage, profilePath);
        user.setProfilePhotoPath(filePath);
        usersInfoRepository.save(user);
        String src = "{ \"img\":\""+storageService.serve(filePath, FileUploadDir.profileImage) + "\"}";
        return src;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookRankingDTO> findUsersBookRank(Long userID) {
        return bookRankingRepository.findBookRankingByUser(userID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookRankingDTO> addBookToBookRank(Long userID, Long bookID, Integer bookRank) {
        UsersInfo user = usersInfoRepository.getOne(userID);
        if(user != null) {
            List<BookRanking> bookRankingList = bookRankingRepository.findAllByUserID(userID);
            List<Long> bookIDsInRanking = bookRankingRepository.findAllBookIDsInRank(userID);
            Integer numOfBooksInRank = bookRankingList.size();
            Libro bookToAdd = libroRepository.getOne(bookID);
            if (bookToAdd != null && !bookIDsInRanking.contains(bookID)) {
                MyBooks myBook = myBooksRepository.getByBookIDAndUserID(bookID, userID);
                if(myBook != null) {
                    if(myBook.getShelfType().compareTo(BookShelf.read)!=0) {
                        myBook.setShelfType(BookShelf.read);
                        myBooksRepository.save(myBook);
                    }
                }
                else {
                    MyBooks addToMyBooks = new MyBooks();
                    addToMyBooks.setShelfType(BookShelf.read);
                    addToMyBooks.setBook(bookToAdd);
                    addToMyBooks.setUsersInfo(user);
                    myBooksRepository.save(addToMyBooks);
                }
                if (numOfBooksInRank == 10) {
                    bookRankingRepository.delete(bookRankingList.get(9));
                }
                if (bookRank > numOfBooksInRank && numOfBooksInRank == 10) {
                    bookRank = 10;
                } else if (bookRank > numOfBooksInRank + 1 && numOfBooksInRank < 10) {
                    bookRank = numOfBooksInRank + 1;
                }
                Integer maxIndex = numOfBooksInRank == 10 ? 8 : numOfBooksInRank - 1;
                for (int i = maxIndex; i >= bookRank - 1; i--) {
                    BookRanking br = bookRankingList.get(i);
                    br.setBookRank(i + 1);
                    bookRankingRepository.save(br);
                }
                BookRanking newBookInRank = new BookRanking();
                newBookInRank.setBookRank(bookRank);
                newBookInRank.setBook(bookToAdd);
                newBookInRank.setUsersInfo(user);
                bookRankingRepository.save(newBookInRank);

            }
            return bookRankingRepository.findBookRankingByUser(userID);
        }
        else return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookRankingDTO> removeBookFromBookRank(Long userID, Long bookRankID) {
        UsersInfo user = usersInfoRepository.getOne(userID);
        if(user != null) {
            List<BookRanking> bookRankingList = bookRankingRepository.findAllByUserID(userID);
            BookRanking bookRankingToDelete = bookRankingRepository.getOne(bookRankID);
            if(bookRankingToDelete.getUsersInfo().getId() == userID) {
                Integer bookRank = bookRankingToDelete.getBookRank();
                Integer bookRankListSize = bookRankingList.size();
                bookRankingRepository.delete(bookRankingToDelete);
                if(bookRank < bookRankListSize) {
                    for(int i=bookRank; i<bookRankListSize; i++) {
                        BookRanking br = bookRankingList.get(i);
                        br.setBookRank(i-1);
                        bookRankingRepository.save(br);
                    }
                }
                return bookRankingRepository.findBookRankingByUser(userID);
            }
            else return null;
        }
        else return null;
    }
}
