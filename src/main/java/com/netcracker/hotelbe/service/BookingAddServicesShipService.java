package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.BookingAddServicesShip;
import com.netcracker.hotelbe.repository.BookingAddServicesShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class BookingAddServicesShipService {

    @Autowired
    private BookingAddServicesShipRepository bookingAddServicesShipRepository;

    public BookingAddServicesShip findById(long id) {
        return bookingAddServicesShipRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<BookingAddServicesShip> findAll() {
        return bookingAddServicesShipRepository.findAll();
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
}
