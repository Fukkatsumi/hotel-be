package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.*;
import com.netcracker.hotelbe.repository.BookingRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import com.netcracker.hotelbe.utils.LoggingManager;
import com.netcracker.hotelbe.utils.enums.MathOperation;
import com.netcracker.hotelbe.utils.enums.UnitOfTime;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public List<Booking> getAll() {
        List<Booking> bookings = bookingRepository.findAll();
        bookings.forEach(this::correctingDate);

        return bookings;
    }

    public Booking save(final Booking booking) {
        final ApartmentClass apartmentClass = apartmentClassService.findById(booking.getApartmentClass().getId());
        booking.setApartmentClass(apartmentClass);
        final User user = userService.findById(booking.getUser().getId());
        booking.setUser(user);
        return bookingRepository.save(booking);
    }

    public Booking findById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

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

    public Booking update(Booking booking, Long id) {

        bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        ApartmentClass apartmentClass = apartmentClassService.findById(booking.getApartmentClass().getId());

        User user = userService.findById(booking.getUser().getId());

        booking.setApartmentClass(apartmentClass);
        booking.setUser(user);
        booking.setId(id);

        return bookingRepository.save(booking);
    }

    public void deleteById(Long id) {
        Booking delete = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        bookingRepository.delete(delete);
    }

    public Booking patch(Long id, Map<String, Object> updates) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        if (updates.containsKey("apartment")) {
            long idApartment = Long.parseLong(updates.get("apartment").toString());
            Apartment apartment = apartmentService.findById(idApartment);
            ApartmentClass apartmentClass = booking.getApartmentClass();
            if (!apartmentClass.equals(apartment.getApartmentClass())) {
                throw new EntityNotFoundException("Apartment number does not entered correctly");
            }
            List<ApartmentClassCustom> apartmentClassCustomList = findFreeApartments(booking.getStartDate().toString(), booking.getEndDate().toString());
            boolean isExistingApartment = false;
            for (ApartmentClassCustom apartmentClassCustom:
                 apartmentClassCustomList) {
                if (apartmentClassCustom.getApartmentList().contains(apartment)) {
                    updates.replace("apartment", apartment);
                    isExistingApartment = true;
                    break;
                }
            }
            if (!isExistingApartment){
                throw new EntityNotFoundException(String.valueOf(apartment.getId()) + ". Apartment is engaged");
            }
        }
        return bookingRepository.save((Booking) entityService.fillFields(updates, booking));
    }

    public int calculateBookingTotalPrice(Booking booking) {
        List<BookingAddServicesCustom> bookingAddServicesCustomList = getServices(booking.getId());
        int priceAllServices = 0;
        for (BookingAddServicesCustom bookingAddServicesCustom:
             bookingAddServicesCustomList) {
            int countServices = bookingAddServicesCustom.getCountServices();
            BookingAddServices bookingAddServices = bookingAddServicesCustom.getBookingAddServices();
            int priceService = bookingAddServices.getPrice();
            priceAllServices += countServices * priceService;
        }
        long days =  (booking.getEndDate().getTime() - booking.getStartDate().getTime()) / (1000 * 3600 * 24) + 1;

        Map<String, String> params = new HashMap<>();
        params.put("apartmentClass", booking.getApartmentClass().getId().toString());
        List<ApartmentPrice> apartmentPriceList = apartmentPriceService.getAllByParams(params);
        for (ApartmentPrice apartmentPrice:
             apartmentPriceList) {
            apartmentPriceService.correctingDateMinus(apartmentPrice);
        }
        priceAllServices = countDaysInApartmentPrice(days, apartmentPriceList, booking, priceAllServices);

        return priceAllServices;
    }

    private int countDaysInApartmentPrice(long days, List<ApartmentPrice> apartmentPriceList, Booking booking, int priceAllServices) {
        LocalDateTime endDateBooking = booking.getEndDate().toLocalDate().atStartOfDay();
        LocalDateTime startDateBooking = booking.getStartDate().toLocalDate().atStartOfDay();
        while (days > 0) {
            for (ApartmentPrice apartmentPrice :
                    apartmentPriceList) {
                LocalDateTime endDatePrice = apartmentPrice.getEndPeriod().toLocalDate().atStartOfDay();
                LocalDateTime startDatePrice = apartmentPrice.getStartPeriod().toLocalDate().atStartOfDay();
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
                    long daysOfPeriod = (booking.getEndDate().getTime() - apartmentPrice.getStartPeriod().getTime()) / (1000 * 3600 * 24) + 1;
                    priceAllServices += apartmentPrice.getPrice() * daysOfPeriod;
                    days -= daysOfPeriod;
                } else if (startDatePrice.compareTo(startDateBooking) <= 0
                        && endDatePrice.compareTo(endDateBooking) <= 0
                        && endDatePrice.compareTo(startDateBooking) >= 0) {
                    long daysOfPeriod = (apartmentPrice.getEndPeriod().getTime() - booking.getStartDate().getTime()) / (1000 * 3600 * 24) + 2;
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

    public List<ApartmentClassCustom> findFreeApartments(String startDateStr, String endDateStr) {
        List<Booking> bookingList = getAll();
        bookingList.forEach(this::correctingDateMinus);
        List<UnavailableApartment> unavailableApartmentList = unavailableApartmentService.getAll();
        for (UnavailableApartment unavailableApartment:
             unavailableApartmentList) {
            unavailableApartmentService.correctingDateMinus(unavailableApartment);
        }
        List<Apartment> apartmentList = apartmentService.getAll();
        Date startDate = toDate(startDateStr);
        Date endDate = toDate(endDateStr);
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
                removeApartment(apartmentList, booking);
            }
        }
        return toApartmentClassCustom(apartmentList);
    }

    private void removeApartment(List<Apartment> apartmentList, Booking booking) {
        ApartmentClass apartmentClass;
        if (booking.getApartment() == null) {
            apartmentClass = booking.getApartmentClass();
            for (Apartment apartment :
                    apartmentList) {
                if (apartment.getApartmentClass() == apartmentClass) {
                    apartmentList.remove(apartment);
                    break;
                }
            }
        }
        apartmentList.remove(booking.getApartment());
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

    private List<ApartmentClassCustom> toApartmentClassCustom(List<Apartment> apartmentList) {
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
                    apClassCustom.addToApartmentList(apartment);
                }
            }
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


    private Booking correctingDateMinus(Booking booking) {
        Date startDate = entityService.correctingDate(booking.getStartDate(), MathOperation.MINUS, 1);
        booking.setStartDate(startDate);

        Date endDate = entityService.correctingDate(booking.getEndDate(), MathOperation.MINUS, 1);
        booking.setEndDate(endDate);

        Timestamp createdDate = entityService.correctingTimestamp(booking.getCreatedDate(), MathOperation.MINUS, UnitOfTime.HOUR, 2);
        booking.setCreatedDate(createdDate);

        return booking;
    }

    public Long addService(Long id, Map<String, Long> bookingAddServices) {
        if (bookingAddServices.containsKey("id") && bookingAddServices.containsKey("countServices")) {
            Booking booking = findById(id);
            BookingAddServices bookingAddService = bookingAddServicesService.findById(bookingAddServices.get("id"));
            int countServices = bookingAddServices.get("countServices").intValue();
            if (booking != null && bookingAddService != null) {
                BookingAddServicesShip bookingAddServicesShip = new BookingAddServicesShip();
                bookingAddServicesShip.setBooking(booking);
                bookingAddServicesShip.setBookingAddServices(bookingAddService);
                bookingAddServicesShip.setCountServices(countServices);

                return  bookingAddServicesShipService.save(bookingAddServicesShip).getId();
            }
        }
        return (long) -1;
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
}
