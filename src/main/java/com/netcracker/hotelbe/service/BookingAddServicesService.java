package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.BookingAddServices;
import com.netcracker.hotelbe.repository.BookingAddServicesRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@Service
public class BookingAddServicesService {

    @Autowired
    private BookingAddServicesRepository bookingAddServicesRepository;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    public BookingAddServices findById(long id){
        return bookingAddServicesRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<BookingAddServices> findAll(){
        return bookingAddServicesRepository.findAll();
    }

    public List<BookingAddServices> getAllByParams(Map<String, String> allParams) {
        if(allParams.size()!=0) {
            return bookingAddServicesRepository.findAll(filterService.fillFilter(allParams, BookingAddServices.class));
        } else {
            return bookingAddServicesRepository.findAllNative();
        }
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

    public BookingAddServices patch(Long id, Map<String, Object> updates) {
        BookingAddServices bookingAddServices = bookingAddServicesRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return bookingAddServicesRepository.save((BookingAddServices) entityService.fillFields(updates, bookingAddServices));
    }
}
