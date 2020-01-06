package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.BookingAddServicesShip;
import com.netcracker.hotelbe.repository.BookingAddServicesShipRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@Service
public class BookingAddServicesShipService {

    @Autowired
    private BookingAddServicesShipRepository bookingAddServicesShipRepository;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    public BookingAddServicesShip findById(long id) {
        return bookingAddServicesShipRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<BookingAddServicesShip> findAll() {
        return bookingAddServicesShipRepository.findAll();
    }

    public List<BookingAddServicesShip> getAllByParams(Map<String, String> allParams) {
        if(allParams.size()!=0) {
            return bookingAddServicesShipRepository.findAll(filterService.fillFilter(allParams, BookingAddServicesShip.class));
        } else {
            return bookingAddServicesShipRepository.findAll();
        }
    }

    public BookingAddServicesShip save(BookingAddServicesShip bookingAddServices) {
        return bookingAddServicesShipRepository.save(bookingAddServices);
    }

    public BookingAddServicesShip update(BookingAddServicesShip bookingAddServicesShip, Long id) {

        bookingAddServicesShipRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        bookingAddServicesShip.setId(id);

        return bookingAddServicesShipRepository.save(bookingAddServicesShip);
    }

    public void deleteById(Long id) {
        BookingAddServicesShip delete = bookingAddServicesShipRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        bookingAddServicesShipRepository.delete(delete);
    }

    public BookingAddServicesShip patch(Long id, Map<String, Object> updates) {
        BookingAddServicesShip bookingAddServicesShip = bookingAddServicesShipRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return bookingAddServicesShipRepository.save((BookingAddServicesShip) entityService.fillFields(updates, bookingAddServicesShip));
    }
}
