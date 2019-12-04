package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.UnavailableApartment;
import com.netcracker.hotelbe.repository.UnavailableApartmentRepository;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UnavailableApartmentService {
    private static Logger logger = LogManager.getLogger(ApartmentPriceService.class);
    private final static String ENTITY_NAME = UnavailableApartment.class.getSimpleName();

    @Autowired
    private UnavailableApartmentRepository unavailableApartmentRepository;

    @Autowired
    private ApartmentService apartmentService;

    public List<UnavailableApartment> getAll() {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ALL_ENTITY, ENTITY_NAME));

        final List<UnavailableApartment> unavailableApartments = unavailableApartmentRepository.findAll();
        logger.info(String.format(CustomEntityLogMessage.FOUND_AMOUNT_ELEMENT, unavailableApartments.size()));

        return unavailableApartments;
    }

    public Long save(UnavailableApartment unavailableApartment) {
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY, ENTITY_NAME));

        final Apartment apartment = apartmentService.findById(unavailableApartment.getApartment().getId());
        unavailableApartment.setApartment(apartment);

        final UnavailableApartment save = unavailableApartmentRepository.save(unavailableApartment);
        final Long id = save.getId();
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY_WITH_ID, ENTITY_NAME, id));

        return id;
    }

    public UnavailableApartment findById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ENTITY_BY_ID, ENTITY_NAME, id));

        return unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public Long update(final UnavailableApartment unavailableApartment, final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.UPDATE_ENTITY, ENTITY_NAME));

        final Apartment apartment = apartmentService.findById(unavailableApartment.getApartment().getId());
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, unavailableApartment.getApartment().getId()));

        UnavailableApartment update = unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, unavailableApartment.getApartment().getId()));

        update.setStartDate(unavailableApartment.getStartDate());
        update.setEndDate(unavailableApartment.getEndDate());
        update.setCauseDescription(unavailableApartment.getCauseDescription());
        update.setApartment(apartment);
        update = unavailableApartmentRepository.save(update);
        logger.trace(String.format(CustomEntityLogMessage.UPDATED_ENTITY_SAVED, ENTITY_NAME));

        return update.getId();
    }

    public void deleteById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.DELETE_ENTITY_BY_ID, ENTITY_NAME, id));

        final UnavailableApartment delete = unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_FOR_DELETE, ENTITY_NAME));

        unavailableApartmentRepository.delete(delete);
        logger.trace(String.format(CustomEntityLogMessage.ENTITY_DELETED, ENTITY_NAME));
    }
}
