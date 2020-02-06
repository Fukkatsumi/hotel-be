package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.*;
import com.netcracker.hotelbe.exception.CustomResponseEntityException;
import com.netcracker.hotelbe.repository.BookingRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import com.netcracker.hotelbe.utils.LoggingManager;
import com.netcracker.hotelbe.utils.enums.MathOperation;
import com.netcracker.hotelbe.utils.enums.UnitOfTime;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class BookingService {

    private static final Logger LOG = LogManager.getLogger(LoggingManager.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ApartmentClassService apartmentClassService;

    @Autowired
    private ApartmentPriceService apartmentPriceService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UnavailableApartmentService unavailableApartmentService;

    @Autowired
    private BookingAddServicesService bookingAddServicesService;

    @Autowired
    private BookingAddServicesShipService bookingAddServicesShipService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private EmailService emailService;

    @Autowired
    @Qualifier("bookingValidator")
    private Validator bookingValidator;

    @Autowired
    @Qualifier("bookingAddServiceValidator")
    private Validator bookingAddServiceValidator;

    public List<Booking> getAll() {
        List<Booking> bookings;
        if (securityService.isManagerOrAdmin()) {
            bookings = findAll();
        } else {
            bookings = findMy();
        }

        bookings.forEach(this::correctingDate);

        return bookings;
    }

    public List<Booking> getAll(Map<String, String> allParams) {
        List<Booking> bookings;
        if (securityService.isManagerOrAdmin()) {
            if (allParams.size() != 0) {
                bookings = findAll(allParams);
            } else {
                bookings = bookingRepository.findAllNative();
            }
        } else {
            bookings = findMy(allParams);
        }

        bookings.forEach(this::correctingDate);

        return bookings;
    }

    public Booking getById(Long id) {
        Booking booking = getNotForbiddenBooking(id);

        if (booking != null) {
            booking = correctingDate(booking);
        }
        return booking;
    }

    public Booking save(final Booking booking) {
        Booking correctedBooking = bookingRepository.save(booking);

        Timestamp showCreatedDate = entityService.correctingTimestamp(correctedBooking.getCreatedDate(),
                MathOperation.PLUS, UnitOfTime.HOUR, 2);
        correctedBooking.setCreatedDate(showCreatedDate);

        Thread deleteCreatedBooking = getThreadToDeleteCreatedBooking(correctedBooking.getId());
        deleteCreatedBooking.start();

        return correctedBooking;
    }

    public Booking update(Booking booking, Long id) {
        if (securityService.isManagerOrAdmin()) {
            if (findById(id) != null) {
                if (booking.getApartment() != null) {
                    booking.setApartment(validateBookingApartment(booking.getApartment().getId(), booking));
                } else {
                    throw new EntityNotFoundException("null");
                }
                booking.setId(id);

                Booking correctedBooking = bookingRepository.save(booking);
                Timestamp showCreatedDate = entityService.correctingTimestamp(correctedBooking.getCreatedDate(),
                        MathOperation.PLUS, UnitOfTime.HOUR, 2);

                correctedBooking.setCreatedDate(showCreatedDate);

                return correctedBooking;
            } else {
                throw new EntityNotFoundException(String.valueOf(id));
            }
        } else {
            throw new CustomResponseEntityException("You do not have permission to perform this action", HttpStatus.FORBIDDEN);
        }
    }

    public void deleteById(Long id) {
        if (securityService.isManagerOrAdmin()) {
            bookingAddServicesShipService.deleteServicesByBookingId(id);
            Booking delete = bookingRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException(String.valueOf(id))
            );

            bookingRepository.delete(delete);
        } else {
            deleteMyById(id);
        }

    }

    public Booking patch(Long id, Map<String, Object> updates) {
        Booking booking;
        if (securityService.isManagerOrAdmin()) {
            booking = findById(id);
            if (updates.containsKey("apartment")) {
                long idApartment = Long.parseLong(updates.get("apartment").toString());
                Apartment apartment = validateBookingApartment(idApartment, booking);
                updates.replace("apartment", apartment);
            }
            if (updates.containsKey("user")) {
                Long idUser = Long.valueOf(updates.get("user").toString());
                User user = userService.findById(idUser);
                updates.replace("user", user);
            }

            booking = bookingRepository.save((Booking) entityService.fillFields(updates, booking));
            booking.setStartDate(entityService.correctingDate(booking.getStartDate(), MathOperation.PLUS, 1));
            booking.setEndDate(entityService.correctingDate(booking.getEndDate(), MathOperation.PLUS, 1));
        } else {
            booking = findMyById(id);
            Map<String, Object> values = new HashMap<>();
            if (updates.containsKey("comment")) {
                values.put("comment", updates.get("comment"));
            }
            if (updates.containsKey("review")) {
                values.put("review", updates.get("review"));
            }
            if (updates.containsKey("bookingStatus")) {
                values.put("bookingStatus", updates.get("bookingStatus"));
            }
            if (updates.size() != 0) {
                booking = bookingRepository.save((Booking) entityService.fillFields(values, booking));

                booking.setStartDate(entityService.correctingDate(booking.getStartDate(), MathOperation.PLUS, 1));
                booking.setEndDate(entityService.correctingDate(booking.getEndDate(), MathOperation.PLUS, 1));
            }
        }

        if (booking.getUser() != null && updates.get("bookingStatus").equals("Confirmed")) {
            emailService.sendBookingInformationToUser(booking);
        }

        return booking;
    }

    public Long addService(Long id, Map<String, Long> bookingAddServices) {
        Booking booking = getNotForbiddenBooking(id);

        if (booking != null) {
            BookingAddServices bookingAddService = bookingAddServicesService.findById(bookingAddServices.get("id"));

            int countServices = bookingAddServices.get("countServices").intValue();

            BookingAddServicesShip bookingAddServicesShip = new BookingAddServicesShip();
            bookingAddServicesShip.setBooking(booking);
            bookingAddServicesShip.setBookingAddServices(bookingAddService);
            bookingAddServicesShip.setCountServices(countServices);

            Long bookingAddServicesId = bookingAddServicesShipService.save(bookingAddServicesShip).getId();
            booking.setTotalPrice(booking.getTotalPrice() + bookingAddService.getPrice() * countServices);

//            save(booking);
            bookingRepository.save(booking);
            return bookingAddServicesId;
        } else {
            throw new CustomResponseEntityException("You cannot add a service to this booking", HttpStatus.FORBIDDEN);
        }
    }

    public List<Long> addService(Long id, List<Map<String, Long>> services) {
        List<Long> bookingAddServicesIds = new LinkedList<>();
        for (Map<String, Long> service : services) {
            bookingAddServicesIds.add(this.addService(id, service));
        }
        return bookingAddServicesIds;
    }

    public List<BookingAddServicesCustom> getServices(Long bookingId) {
        Booking booking = bookingRepository.getOne(bookingId);

        if (booking != null) {
            Map<String, String> params = new HashMap<>(1, 1.1f);
            params.put("booking", String.valueOf(booking.getId()));

            List<BookingAddServicesCustom> bookingAddServiceCustoms = new ArrayList<>();
            List<BookingAddServicesShip> bookingAddServicesShips = bookingAddServicesShipService.getAllByParams(params);
            bookingAddServicesShips.forEach(bookingAddServicesShip -> {
                BookingAddServicesCustom bookingService = new BookingAddServicesCustom();
                bookingService.setBookingAddServices(bookingAddServicesShip.getBookingAddServices());
                bookingService.setCountServices(bookingAddServicesShip.getCountServices());
                bookingAddServiceCustoms.add(bookingService);
            });

            return bookingAddServiceCustoms;
        } else {
            throw new CustomResponseEntityException("You cannot get services for this booking", HttpStatus.FORBIDDEN);
        }
    }

    public void deleteService(Long id, Long serviceId) throws Throwable {
        Booking booking = getNotForbiddenBooking(id);

        if (booking != null) {
            Map<String, String> values = new HashMap<>();
            values.put("booking", id.toString());
            values.put("bookingAddServices", serviceId.toString());

            BookingAddServicesShip bookingAddServicesShip = bookingAddServicesShipService.findOneByFilter(values);

            bookingAddServicesShipService.deleteById(bookingAddServicesShip.getId());

            booking.setTotalPrice(calculateBookingTotalPrice(booking));
//            save(booking);
            bookingRepository.save(booking);
        } else {
            throw new CustomResponseEntityException("You cannot delete service for this booking", HttpStatus.FORBIDDEN);
        }
    }

    public int recalculatePrice(Long id) {
        Booking booking = getNotForbiddenBooking(id);

        if (booking != null) {
            int price = calculateBookingTotalPrice(booking);

            if (price != booking.getTotalPrice()) {
                booking.setTotalPrice(calculateBookingTotalPrice(booking));
//                save(booking);
                bookingRepository.save(booking);
            }

            return price;
        } else {
            throw new CustomResponseEntityException("You cannot recalculate price for this booking", HttpStatus.FORBIDDEN);
        }
    }

    public void cascadeDeleteById(Long id) {
        if (securityService.isManagerOrAdmin()) {

            Booking booking = findById(id);

            bookingAddServicesShipService.deleteByBooking(booking);

            bookingRepository.delete(booking);
        } else {
            deleteMyById(id);
        }
    }

    public List<Apartment> findFreeApartmentsForApartmentClass(String startDateStr, String endDateStr, Long idApClass) {
        Date startDate = toDate(startDateStr);
        Date endDate = toDate(endDateStr);
        List<Apartment> apartmentList = checkUnavailableApartment(startDate, endDate);
        if (apartmentList == null) {
            return null;
        }
        apartmentList.removeIf(apartmentTemp -> !apartmentTemp.getApartmentClass().getId().equals(idApClass));
        Map<String, String> mapApClass = new HashMap<>();
        mapApClass.put("apartmentClass", idApClass.toString());
        List<Booking> bookingList = findAll(mapApClass);
        for (Booking booking :
                bookingList) {
            if ((startDate.compareTo(booking.getStartDate()) >= 0
                    && (endDate.compareTo(booking.getEndDate()) <= 0))
                    || ((startDate.compareTo(booking.getStartDate()) < 0)
                    && (endDate.compareTo(booking.getEndDate()) <= 0)
                    && (endDate.compareTo(booking.getStartDate()) >= 0))
                    || ((startDate.compareTo(booking.getStartDate()) >= 0)
                    && (startDate.compareTo(booking.getEndDate()) <= 0)
                    && (endDate.compareTo(booking.getEndDate()) > 0))
                    || ((startDate.compareTo(booking.getStartDate()) < 0)
                    && (endDate.compareTo(booking.getEndDate()) > 0))) {
                apartmentList.remove(booking.getApartment());
            }
        }
        return apartmentList;
    }

    public List<ApartmentClassCustom> findFreeApartments(String startDateStr, String endDateStr) {
        List<Booking> bookingList = findAll();
        Map<String, Integer> apartmentClassReservedMap = new HashMap<>();
        Date startDate = toDate(startDateStr);
        Date endDate = toDate(endDateStr);
        List<Apartment> apartmentList = checkUnavailableApartment(startDate, endDate);
        if (apartmentList == null) {
            return null;
        }
        for (Booking booking :
                bookingList) {
            if ((startDate.compareTo(booking.getStartDate()) >= 0
                    && (endDate.compareTo(booking.getEndDate()) <= 0))
                    || ((startDate.compareTo(booking.getStartDate()) < 0)
                    && (endDate.compareTo(booking.getEndDate()) <= 0)
                    && (endDate.compareTo(booking.getStartDate()) >= 0))
                    || ((startDate.compareTo(booking.getStartDate()) >= 0)
                    && (startDate.compareTo(booking.getEndDate()) <= 0)
                    && (endDate.compareTo(booking.getEndDate()) > 0))
                    || ((startDate.compareTo(booking.getStartDate()) < 0)
                    && (endDate.compareTo(booking.getEndDate()) > 0))) {
                if (booking.getApartment() == null) {
                    if (apartmentClassReservedMap.containsKey(booking.getApartmentClass().getNameClass())) {
                        Integer tempQuantity = apartmentClassReservedMap.get(booking.getApartmentClass().getNameClass());
                        apartmentClassReservedMap.replace(booking.getApartmentClass().getNameClass(), ++tempQuantity);
                    } else {
                        apartmentClassReservedMap.put(booking.getApartmentClass().getNameClass(), 1);
                    }
                } else {
                    apartmentList.remove(booking.getApartment());
                }
            }
        }
        return toApartmentClassCustom(apartmentList, apartmentClassReservedMap, startDate, endDate);
    }

    private List<ApartmentClassCustom> toApartmentClassCustom(List<Apartment> apartmentList,
                                                              Map<String, Integer> apartmentClassReservedMap,
                                                              Date startDate, Date endDate) {
        List<ApartmentClassCustom> apartmentClassCustomsList = new ArrayList<>();
        List<ApartmentClass> apartmentClassList = apartmentClassService.findAll();
        for (ApartmentClass apartmentClass :
                apartmentClassList) {
            ApartmentClassCustom apartmentClassCustomTemp = new ApartmentClassCustom(apartmentClass);
            apartmentClassCustomsList.add(apartmentClassCustomTemp);
        }

        for (Apartment apartment :
                apartmentList) {
            for (ApartmentClassCustom apClassCustom :
                    apartmentClassCustomsList) {
                if (apClassCustom.getApartmentClass().getId().equals(apartment.getApartmentClass().getId())) {
                    apClassCustom.setCountOfApartments(apClassCustom.getCountOfApartments() + 1);
                }
            }
        }

        for (ApartmentClassCustom apClassCustom :
                apartmentClassCustomsList) {
            if (apartmentClassReservedMap.containsKey(apClassCustom.getApartmentClass().getNameClass())) {
                apClassCustom.setCountOfApartments(apClassCustom.getCountOfApartments()
                        - apartmentClassReservedMap.get(apClassCustom.getApartmentClass().getNameClass()));
            }
            apClassCustom.setApartmentPriceOnDates(calculateBookingTotalPrice(
                    createMockBooking(startDate, endDate, apClassCustom.getApartmentClass())));
        }

        return apartmentClassCustomsList;
    }


    public void validateBooking(final Booking booking, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingValidator.validate(booking, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    public void validateService(final Map<String, Long> values, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingAddServiceValidator.validate(values, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    public void validateServices(final List<Map<String, Long>> values, BindingResult bindingResult) throws MethodArgumentNotValidException {
        for (Map<String, Long> val : values) {
            this.validateService(val, bindingResult);
        }
    }

    private Booking getNotForbiddenBooking(Long bookingId) {
        Booking booking;
        if (securityService.isManagerOrAdmin()) {
            booking = findById(bookingId);
        } else {
            booking = findMyById(bookingId);
        }
        return booking;
    }

    private List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    private List<Booking> findAll(Map<String, String> allParams) {
        return bookingRepository.findAll(filterService.fillFilter(allParams, Booking.class));
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    private Booking findOneByParam(Map<String, String> allParams) {
        Booking booking = null;
        if (allParams.size() != 0) {
            Optional<Booking> bookingOptional = bookingRepository.findOne(filterService.fillFilter(allParams, Booking.class));
            if (bookingOptional.isPresent()) {
                booking = bookingOptional.get();
            }
        }
        return booking;
    }

    private List<Booking> findMy(Map<String, String> allParams) {

        User user = securityService.getCurrentUser();

        allParams.put("user", user.getId().toString());

        return findAll(allParams);
    }

    private List<Booking> findMy() {
        User user = securityService.getCurrentUser();

        Map<String, String> values = new HashMap<>();
        values.put("user", user.getId().toString());

        return findAll(values);
    }

    private Booking findMyById(Long id) {
        User user = securityService.getCurrentUser();

        Map<String, String> values = new HashMap<>();
        values.put("id", id.toString());
        values.put("user", user.getId().toString());

        return findOneByParam(values);
    }

    private void deleteCreatedBookingById(Long id) {
        bookingRepository.deleteCreatedBookingById(id);
    }

    private void deleteMyById(Long id) {
        User user = securityService.getCurrentUser();

        Map<String, String> values = new HashMap<>();
        values.put("id", id.toString());
        values.put("user", user.getId().toString());

        Booking booking = findOneByParam(values);

        if (booking == null) {
            throw new CustomResponseEntityException("You cannot delete this booking");
        } else {
            bookingAddServicesShipService.deleteServicesByBookingId(id);

            bookingRepository.delete(booking);
        }
    }

    private Apartment validateBookingApartment(Long idApartment, Booking booking) {
        Apartment apartment = apartmentService.findById(idApartment);
        ApartmentClass apartmentClass = booking.getApartmentClass();
        if (!apartmentClass.getId().equals(apartment.getApartmentClass().getId())) {
            throw new EntityNotFoundException("Apartment number does not entered correctly");
        }

        List<ApartmentClassCustom> apartmentClassCustomList = findFreeApartments(booking.getStartDate().toString(), booking.getEndDate().toString());
        List<Apartment> apartmentList = findFreeApartmentsForApartmentClass(booking.getStartDate().toString(), booking.getEndDate().toString(), booking.getApartmentClass().getId());
        for (ApartmentClassCustom apartmentClassCustom :
                apartmentClassCustomList) {
            if (apartmentClassCustom.getCountOfApartments() != 0 && apartmentList.contains(apartment)) {
                return apartment;
            }
        }
        throw new CustomResponseEntityException("Entity with id = " + idApartment + ". Apartment is engaged", HttpStatus.NOT_FOUND);
    }

    private int calculateTotalApartmentPrice(Booking booking) {
        long days = (booking.getEndDate().getTime() - booking.getStartDate().getTime()) / (1000 * 3600 * 24) + 1;

        Map<String, String> params = new HashMap<>(1, 1.1f);
        params.put("apartmentClass", booking.getApartmentClass().getId().toString());
        List<ApartmentPrice> apartmentPriceList = apartmentPriceService.getAllByParams(params);
        for (ApartmentPrice apartmentPrice :
                apartmentPriceList) {
            apartmentPriceService.correctingDateMinus(apartmentPrice);
        }

        return countPriceOnAllDays(days, apartmentPriceList, booking);
    }

    private int calculateBookingTotalPrice(Booking booking) {
        int priceAllServices = calculateBookingTotalServicesPrice(booking);
        priceAllServices += calculateTotalApartmentPrice(booking);
        return priceAllServices;
    }

    private int calculateBookingTotalServicesPrice(Booking booking) {
        List<BookingAddServicesCustom> bookingAddServicesCustomList = getServices(booking.getId());
        int priceAllServices = 0;
        for (BookingAddServicesCustom bookingAddServicesCustom :
                bookingAddServicesCustomList) {
            int countServices = bookingAddServicesCustom.getCountServices();
            BookingAddServices bookingAddServices = bookingAddServicesCustom.getBookingAddServices();
            int priceService = bookingAddServices.getPrice();
            priceAllServices += countServices * priceService;
        }
        return priceAllServices;
    }

    private Booking createMockBooking(Date startDate, Date endDate, ApartmentClass apartmentClass) {
        return Booking.builder()
                .apartmentClass(apartmentClass)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    private int countPriceOnAllDays(long days, List<ApartmentPrice> apartmentPriceList, Booking booking) {
        int priceAllDays = 0;
        LocalDate endDateBooking = booking.getEndDate().toLocalDate();
        LocalDate startDateBooking = booking.getStartDate().toLocalDate();
        while (days > 0) {
            for (ApartmentPrice apartmentPrice :
                    apartmentPriceList) {
                LocalDate endDatePrice = apartmentPrice.getEndPeriod().toLocalDate();
                LocalDate startDatePrice = apartmentPrice.getStartPeriod().toLocalDate();
                if (startDatePrice.compareTo(startDateBooking) >= 0
                        && (endDatePrice.compareTo(endDateBooking)) <= 0) {
                    long daysOfPeriod = (apartmentPrice.getEndPeriod().getTime()
                            - apartmentPrice.getStartPeriod().getTime()) / (1000 * 3600 * 24) + 1;
                    priceAllDays += apartmentPrice.getPrice() * daysOfPeriod;
                    days -= daysOfPeriod;
                } else if (startDatePrice.compareTo(startDateBooking) <= 0
                        && endDatePrice.compareTo(endDateBooking) >= 0) {
                    priceAllDays += apartmentPrice.getPrice() * days;
                    days = 0;
                } else if (startDatePrice.compareTo(startDateBooking) >= 0
                        && endDatePrice.compareTo(endDateBooking) >= 0
                        && startDatePrice.compareTo(endDateBooking) <= 0) {
                    Period period = Period.between(startDatePrice, endDateBooking);
                    Integer daysOfPeriod = period.getDays() + 1;
                    priceAllDays += apartmentPrice.getPrice() * daysOfPeriod;
                    days -= daysOfPeriod;
                } else if (startDatePrice.compareTo(startDateBooking) <= 0
                        && endDatePrice.compareTo(endDateBooking) <= 0
                        && endDatePrice.compareTo(startDateBooking) >= 0) {
                    Period period = Period.between(startDateBooking, endDatePrice);
                    Integer daysOfPeriod = period.getDays() + 1;
                    priceAllDays += apartmentPrice.getPrice() * daysOfPeriod;
                    days -= daysOfPeriod;
                }
            }
            if (days > 0) {
                return priceAllDays;
            }
        }
        return priceAllDays;
    }

    private List<Apartment> checkUnavailableApartment(Date startDate, Date endDate) {
        List<Apartment> apartmentList = apartmentService.getAll();
        List<UnavailableApartment> unavailableApartmentList = unavailableApartmentService.getAll();
        for (UnavailableApartment unavailableApartment :
                unavailableApartmentList) {
            unavailableApartmentService.correctingDateMinus(unavailableApartment);
        }
        if (isValidDates(startDate, endDate)) {
            return null;
        }
        for (UnavailableApartment unavailableApartment :
                unavailableApartmentList) {
            if ((startDate.compareTo(unavailableApartment.getStartDate()) >= 0
                    && (endDate.compareTo(unavailableApartment.getEndDate()) <= 0))
                    || ((startDate.compareTo(unavailableApartment.getStartDate()) < 0)
                    && (endDate.compareTo(unavailableApartment.getEndDate()) <= 0)
                    && (endDate.compareTo(unavailableApartment.getStartDate()) >= 0))
                    || ((startDate.compareTo(unavailableApartment.getStartDate()) >= 0)
                    && (startDate.compareTo(unavailableApartment.getEndDate()) <= 0)
                    && (endDate.compareTo(unavailableApartment.getEndDate()) > 0))
                    || ((startDate.compareTo(unavailableApartment.getStartDate()) < 0)
                    && (endDate.compareTo(unavailableApartment.getEndDate()) > 0))) {
                apartmentList.remove(unavailableApartment.getApartment());
            }
        }
        return apartmentList;
    }

    private Date toDate(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date date = simpleDateFormat.parse(strDate);
            long dateLong = date.getTime();
            return new Date(dateLong);
        } catch (ParseException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private boolean isValidDates(Date startDate, Date endDate) {
        return endDate == null || startDate == null || endDate.compareTo(startDate) < 0;
    }

    private Booking correctingDate(Booking booking) {
        if (booking != null) {
            Date startDate = entityService.correctingDate(booking.getStartDate(), MathOperation.PLUS, 1);
            booking.setStartDate(startDate);

            Date endDate = entityService.correctingDate(booking.getEndDate(), MathOperation.PLUS, 1);
            booking.setEndDate(endDate);

            Timestamp createdDate = entityService.correctingTimestamp(booking.getCreatedDate(),
                    MathOperation.PLUS, UnitOfTime.HOUR, +2);
            booking.setCreatedDate(createdDate);
        }

        return booking;
    }


    private Thread getThreadToDeleteCreatedBooking(Long id) {
        Runnable task = () -> {
            try {
                TimeUnit.MINUTES.sleep(15);
                deleteCreatedBookingById(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        return new Thread(task);
    }

}
