package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.*;
import com.netcracker.hotelbe.entity.enums.UserRole;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Qualifier("bookingValidator")
    private Validator bookingValidator;

    @Autowired
    @Qualifier("bookingAddServiceValidator")
    private Validator bookingAddServiceValidator;

    public List<Booking> getAll() {
        List<Booking> bookings = bookingRepository.findAll();
        bookings.forEach(this::correctingDate);

        return bookings;
    }

    public Booking save(final Booking booking) {
        final ApartmentClass apartmentClass = apartmentClassService.findById(booking.getApartmentClass().getId());
        booking.setApartmentClass(apartmentClass);
        User user = booking.getUser();
        if (user != null) {
            user = userService.findById(user.getId());
            booking.setUser(user);
        }
        Apartment apartment = booking.getApartment();
        if (apartment != null) {
            apartment = apartmentService.findById(apartment.getId());
        }
        booking.setApartment(apartment);

        Booking showBooking = bookingRepository.save(booking);
        Timestamp showCreatedDate = entityService.correctingTimestamp(showBooking.getCreatedDate(), MathOperation.PLUS, UnitOfTime.HOUR, 2);

        showBooking.setCreatedDate(showCreatedDate);
        return showBooking;
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public Booking getById(Long id) {
        Booking booking = findById(id);

        return correctingDate(booking);
    }

    public List<Booking> getAllByParams(Map<String, String> allParams) {
        List<Booking> bookings;
        if (allParams.size() != 0) {
            bookings = bookingRepository.findAll(filterService.fillFilter(allParams, Booking.class));
        } else {
            bookings = bookingRepository.findAll();
        }
        bookings.forEach(this::correctingDate);
        return bookings;
    }

    public Booking findOneByParam(Map<String, String> allParams) {
        Booking booking = null;
        if (allParams.size() != 0) {
            Optional<Booking> bookingOptional = bookingRepository.findOne(filterService.fillFilter(allParams, Booking.class));
            if (bookingOptional.isPresent()) {
                booking = bookingOptional.get();
            }
        }
        return booking;
    }

    public Booking update(Booking booking, Long id) {

        findById(id);

        ApartmentClass apartmentClass = apartmentClassService.findById(booking.getApartmentClass().getId());

        User user = userService.findById(booking.getUser().getId());

        booking.setApartmentClass(apartmentClass);
        booking.setUser(user);
        booking.setId(id);
        if (booking.getApartment() != null) {
            booking.setApartment(validateBookingApartment(booking.getApartment().getId(), booking));
        } else {
            throw new EntityNotFoundException("null");
        }

        Booking showBooking = bookingRepository.save(booking);
        Timestamp showCreatedDate = entityService.correctingTimestamp(showBooking.getCreatedDate(), MathOperation.PLUS, UnitOfTime.HOUR, 2);

        showBooking.setCreatedDate(showCreatedDate);
        return showBooking;
    }

    public void deleteById(Long id) {
        Booking delete = findById(id);

        bookingRepository.delete(delete);
    }

    public Booking patch(Long id, Map<String, Object> updates) {
        Booking booking = findById(id);

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
        return booking;
    }

    public Long addService(Long id, Map<String, Long> bookingAddServices) {
        Booking booking = findById(id);
        BookingAddServices bookingAddService = bookingAddServicesService.findById(bookingAddServices.get("id"));

        int countServices = bookingAddServices.get("countServices").intValue();
        BookingAddServicesShip bookingAddServicesShip = new BookingAddServicesShip();
        bookingAddServicesShip.setBooking(booking);
        bookingAddServicesShip.setBookingAddServices(bookingAddService);
        bookingAddServicesShip.setCountServices(countServices);
        Long bookingAddServicesId = bookingAddServicesShipService.save(bookingAddServicesShip).getId();
        booking.setTotalPrice(booking.getTotalPrice() + bookingAddService.getPrice() * countServices);
        save(booking);

        return bookingAddServicesId;
    }

    public List<BookingAddServicesCustom> getServices(Long id) {
        Map<String, String> params = new HashMap<>();
        params.put("booking", id.toString());
        List<BookingAddServicesCustom> bookingAddServiceCustoms = new ArrayList<>();
        List<BookingAddServicesShip> bookingAddServicesShips = bookingAddServicesShipService.getAllByParams(params);
        bookingAddServicesShips.forEach(bookingAddServicesShip -> {
            BookingAddServicesCustom bookingService = new BookingAddServicesCustom();
            bookingService.setBookingAddServices(bookingAddServicesShip.getBookingAddServices());
            bookingService.setCountServices(bookingAddServicesShip.getCountServices());
            bookingAddServiceCustoms.add(bookingService);
        });

        return bookingAddServiceCustoms;
    }

    public void deleteService(Long id, Long serviceId) throws Throwable {
        Map<String, String> values = new HashMap<>();
        values.put("booking", id.toString());
        values.put("bookingAddServices", serviceId.toString());

        BookingAddServicesShip bookingAddServicesShip = bookingAddServicesShipService.findOneByFilter(values);

        bookingAddServicesShipService.deleteById(bookingAddServicesShip.getId());

        Booking booking = findById(id);
        booking.setTotalPrice(calculateBookingTotalApartmentPrice(booking));
        save(booking);
    }

    public int recalculatePrice(Long id) {
        Booking booking = findById(id);
        int price = calculateBookingTotalApartmentPrice(booking);

        if (price != booking.getTotalPrice()) {
            booking.setTotalPrice(calculateBookingTotalApartmentPrice(booking));
            save(booking);
        }

        return price;
    }

    public void cascadeDeleteById(Long id) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().toString().equals(UserRole.Administrator.name())
                || SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().toString().equals(UserRole.Manager.name())) {

            Booking booking = findById(id);

            bookingAddServicesShipService.deleteByBooking(booking);

            bookingRepository.delete(booking);
        } else {
            cascadeDeleteMyById(id);
        }
    }

    public void cascadeDeleteMyById(Long id) {
        String login = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        User user = userService.findByLogin(login);

        Map<String, String> values = new HashMap<>();
        values.put("id", id.toString());
        values.put("user", user.getId().toString());

        Booking booking = findOneByParam(values);

        if (booking == null) {
            throw new CustomResponseEntityException("You cannot delete this booking");
        } else {
            bookingAddServicesShipService.deleteByBooking(booking);

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
        throw new EntityNotFoundException(idApartment + ". Apartment is engaged");
    }

    private int calculateBookingTotalApartmentPrice(Booking booking) {
        int priceAllServices = calculateBookingTotalServicesPrice(booking);
        long days = (booking.getEndDate().getTime() - booking.getStartDate().getTime()) / (1000 * 3600 * 24) + 1;

        Map<String, String> params = new HashMap<>();
        params.put("apartmentClass", booking.getApartmentClass().getId().toString());
        List<ApartmentPrice> apartmentPriceList = apartmentPriceService.getAllByParams(params);
        for (ApartmentPrice apartmentPrice :
                apartmentPriceList) {
            apartmentPriceService.correctingDateMinus(apartmentPrice);
        }
        priceAllServices = countPriceOnAllDays(days, apartmentPriceList, booking, priceAllServices);

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
        Booking booking = new Booking();
        booking.setApartmentClass(apartmentClass);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        return booking;
    }

    private int countPriceOnAllDays(long days, List<ApartmentPrice> apartmentPriceList, Booking booking, int priceAllServices) {
        LocalDate endDateBooking = booking.getEndDate().toLocalDate();
        LocalDate startDateBooking = booking.getStartDate().toLocalDate();
        while (days > 0) {
            for (ApartmentPrice apartmentPrice :
                    apartmentPriceList) {
                LocalDate endDatePrice = apartmentPrice.getEndPeriod().toLocalDate();
                LocalDate startDatePrice = apartmentPrice.getStartPeriod().toLocalDate();
                if (startDatePrice.compareTo(startDateBooking) >= 0
                        && (endDatePrice.compareTo(endDateBooking)) <= 0) {
                    long daysOfPeriod = (apartmentPrice.getEndPeriod().getTime() - apartmentPrice.getStartPeriod().getTime()) / (1000 * 3600 * 24) + 1;
                    priceAllServices += apartmentPrice.getPrice() * daysOfPeriod;
                    days -= daysOfPeriod;
                } else if (startDatePrice.compareTo(startDateBooking) <= 0
                        && endDatePrice.compareTo(endDateBooking) >= 0) {
                    priceAllServices += apartmentPrice.getPrice() * days;
                    days = 0;
                } else if (startDatePrice.compareTo(startDateBooking) >= 0
                        && endDatePrice.compareTo(endDateBooking) >= 0
                        && startDatePrice.compareTo(endDateBooking) <= 0) {
                    Period period = Period.between(startDatePrice, endDateBooking);
                    Integer daysOfPeriod = period.getDays() + 1;
                    priceAllServices += apartmentPrice.getPrice() * daysOfPeriod;
                    days -= daysOfPeriod;
                } else if (startDatePrice.compareTo(startDateBooking) <= 0
                        && endDatePrice.compareTo(endDateBooking) <= 0
                        && endDatePrice.compareTo(startDateBooking) >= 0) {
                    Period period = Period.between(startDateBooking, endDatePrice);
                    Integer daysOfPeriod = period.getDays() + 1;
                    priceAllServices += apartmentPrice.getPrice() * daysOfPeriod;
                    days -= daysOfPeriod;
                }
            }
            if (days > 0) {
                return priceAllServices;
            }
        }
        return priceAllServices;
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
        List<Booking> bookingList = getAllByParams(mapApClass);
        bookingList.forEach(this::correctingDateMinus);
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
        List<Booking> bookingList = getAll();
        bookingList.forEach(this::correctingDateMinus);
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

    public void validate(final Booking booking, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingValidator.validate(booking, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    public void validate(final Map<String, Long> values, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingAddServiceValidator.validate(values, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    private List<ApartmentClassCustom> toApartmentClassCustom(List<Apartment> apartmentList, Map<String, Integer> apartmentClassReservedMap, Date startDate, Date endDate) {
        List<ApartmentClassCustom> apartmentClassCustomsList = new ArrayList<>();
        for (ApartmentClass apartmentClass :
                apartmentClassService.findAll()) {
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
                apClassCustom.setCountOfApartments(apClassCustom.getCountOfApartments() - apartmentClassReservedMap.get(apClassCustom.getApartmentClass().getNameClass()));
            }
            apClassCustom.setApartmentPriceOnDates(calculateBookingTotalApartmentPrice(createMockBooking(startDate, endDate, apClassCustom.getApartmentClass())));
        }

        return apartmentClassCustomsList;
    }

    private Booking correctingDate(Booking booking) {
        Date startDate = entityService.correctingDate(booking.getStartDate(), MathOperation.PLUS, 1);
        booking.setStartDate(startDate);

        Date endDate = entityService.correctingDate(booking.getEndDate(), MathOperation.PLUS, 1);
        booking.setEndDate(endDate);

        Timestamp createdDate = entityService.correctingTimestamp(booking.getCreatedDate(), MathOperation.PLUS, UnitOfTime.HOUR, +2);
        booking.setCreatedDate(createdDate);

        return booking;
    }


    private void correctingDateMinus(Booking booking) {
        Date startDate = entityService.correctingDate(booking.getStartDate(), MathOperation.MINUS, 1);
        booking.setStartDate(startDate);

        Date endDate = entityService.correctingDate(booking.getEndDate(), MathOperation.MINUS, 1);
        booking.setEndDate(endDate);

        Timestamp createdDate = entityService.correctingTimestamp(booking.getCreatedDate(), MathOperation.MINUS, UnitOfTime.HOUR, 2);
        booking.setCreatedDate(createdDate);

    }


}
