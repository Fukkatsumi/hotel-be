package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.repository.ApartmentClassRepository;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ApartmentClassService {
    private static Logger logger = LogManager.getLogger(ApartmentClassService.class);
    private final static String ENTITY_NAME = ApartmentClass.class.getSimpleName();

    @Autowired
    private ApartmentClassRepository apartmentClassRepository;


    public List<ApartmentClass> findAll() {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ALL_ENTITY, ENTITY_NAME));

        final List<ApartmentClass> apartmentClasses = apartmentClassRepository.findAll();
        logger.info(String.format(CustomEntityLogMessage.FOUND_AMOUNT_ELEMENT, apartmentClasses.size()));

        return apartmentClasses;
    }

    public Long save(final ApartmentClass apartmentClass) {
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY, ENTITY_NAME));

        final ApartmentClass save = apartmentClassRepository.save(apartmentClass);
        final Long id = save.getId();
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY_WITH_ID, ENTITY_NAME, id));

        return id;
    }

    public ApartmentClass findById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ENTITY_BY_ID, ENTITY_NAME, id));

        return apartmentClassRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public Long update(final ApartmentClass apartmentClass, final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.UPDATE_ENTITY, ENTITY_NAME));

        ApartmentClass update = apartmentClassRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, id));

        update.setNameClass(apartmentClass.getNameClass());
        update.setNumberOfRooms(apartmentClass.getNumberOfRooms());
        update.setNumberOfCouchette(apartmentClass.getNumberOfCouchette());

        update = apartmentClassRepository.save(update);
        logger.trace(String.format(CustomEntityLogMessage.UPDATED_ENTITY_SAVED, ENTITY_NAME));

        return update.getId();
    }

    public void deleteById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.DELETE_ENTITY_BY_ID, ENTITY_NAME, id));

        ApartmentClass delete = apartmentClassRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_FOR_DELETE, ENTITY_NAME));

        apartmentClassRepository.delete(delete);
        logger.trace(String.format(CustomEntityLogMessage.ENTITY_DELETED, ENTITY_NAME));
    }
}
