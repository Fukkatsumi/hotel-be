package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.repository.ApartmentRepository;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ApartmentService {
    private static Logger logger = LogManager.getLogger(ApartmentPriceService.class);
    private final static String ENTITY_NAME = Apartment.class.getSimpleName();

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ApartmentClassService apartmentClassService;

    public List<Apartment> getAll() {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ALL_ENTITY, ENTITY_NAME));

        final List<Apartment> apartments = apartmentRepository.findAll();
        logger.info(String.format(CustomEntityLogMessage.FOUND_AMOUNT_ELEMENT, apartments.size()));

        return apartments;
    }

    public Long save(Apartment apartment) {
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY, ENTITY_NAME));

        final ApartmentClass apartmentClass = apartmentClassService.findById(apartment.getApartmentClass().getId());
        apartment.setApartmentClass(apartmentClass);

        final Apartment save = apartmentRepository.save(apartment);
        final Long id = save.getId();
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY_WITH_ID, ENTITY_NAME, id));

        return id;
    }

    public Apartment findById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ENTITY_BY_ID, ENTITY_NAME, id));

        return apartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public Long update(final Apartment apartment, final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.UPDATE_ENTITY, ENTITY_NAME));

        final ApartmentClass apartmentClass = apartmentClassService.findById(apartment.getApartmentClass().getId());
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, apartment.getApartmentClass().getId()));

        Apartment update = apartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, apartment.getApartmentClass().getId()));

        update.setRoomNumber(apartment.getRoomNumber());
        update.setPhoto(apartment.getPhoto());
        update.setDescription(apartment.getDescription());
        update.setStatus(apartment.getStatus());
        update.setApartmentClass(apartmentClass);
        update = apartmentRepository.save(update);
        logger.trace(String.format(CustomEntityLogMessage.UPDATED_ENTITY_SAVED, ENTITY_NAME));

        return update.getId();
    }

    public void deleteById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.DELETE_ENTITY_BY_ID, ENTITY_NAME, id));

        final Apartment delete = apartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_FOR_DELETE, ENTITY_NAME));

        apartmentRepository.delete(delete);
        logger.trace(String.format(CustomEntityLogMessage.ENTITY_DELETED, ENTITY_NAME));
    }

}
