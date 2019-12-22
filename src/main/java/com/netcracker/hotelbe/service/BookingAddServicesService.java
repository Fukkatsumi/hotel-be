package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.BookingAddServices;
import com.netcracker.hotelbe.repository.BookingAddServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class BookingAddServicesService {

    @Autowired
    private BookingAddServicesRepository bookingAddServicesRepository;

    public BookingAddServices findById(long id){
        return bookingAddServicesRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<BookingAddServices> findAll(){
        return bookingAddServicesRepository.findAll();
    }

    public BookingAddServices save(BookingAddServices bookingAddServices){
        return bookingAddServicesRepository.save(bookingAddServices);
    }

    public BookingAddServices update(BookingAddServices bookingAddServices, Long id) {

        BookingAddServices update = bookingAddServicesRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        update.setServiceName(bookingAddServices.getServiceName());
        update.setPrice(bookingAddServices.getPrice());

        return bookingAddServicesRepository.save(update);
    }

    public void deleteById(Long id){
        BookingAddServices delete = bookingAddServicesRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        bookingAddServicesRepository.delete(delete);

    }
}
