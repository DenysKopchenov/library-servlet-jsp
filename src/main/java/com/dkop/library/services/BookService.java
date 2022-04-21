package com.dkop.library.services;

import com.dkop.library.dao.BooksDao;
import com.dkop.library.dao.OrderDao;
import com.dkop.library.entity.Book;
import com.dkop.library.exceptions.AlreadyExistException;
import com.dkop.library.exceptions.NotFoundException;
import com.dkop.library.exceptions.UnableToDeleteException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


import static com.dkop.library.utils.LocalizationUtil.localizationBundle;

/**
 * Service for handling operations with books
 */
@Service
public class BookService {

    private final BooksDao booksDao;
    private final OrderDao orderDao;
    private static final Logger LOGGER = LogManager.getLogger(BookService.class);

    public BookService(BooksDao booksDao, OrderDao orderDao) {
        this.booksDao = booksDao;
        this.orderDao = orderDao;
    }

    /**
     * Creates a book entity, with the specified parameters and inserts it in the database
     * @param title
     * @param author
     * @param publisher
     * @param publishingDate
     * @param amount
     * @throws AlreadyExistException if book already exist in database
     */
    public void createBook(String title, String author, String publisher, String publishingDate, String amount) throws AlreadyExistException {
        LocalDate date = LocalDate.parse(publishingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Book book = Book.newBuilder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .publishingDate(date)
                .amount(Integer.parseInt(amount))
                .build();
        try  {
            booksDao.create(book);
        } catch (SQLException e) {
            LOGGER.error(e, e.getCause());
            throw new AlreadyExistException(localizationBundle.getString("book.already.exist"), e);
        }
    }

    /**
     * Finds a book by the specified id and updates it with the given parameters
     * @param id
     * @param title
     * @param author
     * @param publisher
     * @param publishingDate
     * @param amount
     * @throws NotFoundException
     */
    public void updateBook(int id, String title, String author, String publisher, String publishingDate, String amount) throws NotFoundException {
            Book bookFromDB = booksDao.findById(id);
            LocalDate date = LocalDate.parse(publishingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Book updatingBook = Book.newBuilder()
                    .id(id)
                    .title(title)
                    .author(author)
                    .publisher(publisher)
                    .publishingDate(date)
                    .amount(Integer.parseInt(amount))
                    .onOrder(bookFromDB.getOnOrder())
                    .build();
            booksDao.update(updatingBook);
    }

    /**
     * Finds a book by the specified id and deletes it
     * @param id
     * @throws NotFoundException
     * @throws UnableToDeleteException
     */
    public void deleteBook(int id) throws NotFoundException, UnableToDeleteException {
            booksDao.findById(id);
            if (orderDao.isAvailableToDeleteBook(id)) {
                booksDao.delete(id);
            } else {
                throw new UnableToDeleteException(localizationBundle.getString("unable.delete.book"));
            }
    }

    public List<Book> findAllBooksByAuthor(String author) {
            return booksDao.findAllByAuthor(author);
    }

    public List<Book> findAllBooksByTitle(String title) {
            return booksDao.findAllByTitle(title);
    }

    public Book findById(int id) throws NotFoundException {
            return booksDao.findById(id);
    }

    public List<Book> findAllSorted(String sortBy, int start, int numberOfRecords) {
            return booksDao.findAllSorted(sortBy, start, numberOfRecords);
    }

    public int countAllRows() {
            return booksDao.countAllRows();
    }
}
