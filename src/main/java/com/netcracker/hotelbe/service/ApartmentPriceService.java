package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.entity.ApartmentPrice;
import com.netcracker.hotelbe.repository.ApartmentPriceRepository;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ApartmentPriceService {
    private static Logger logger = LogManager.getLogger(ApartmentPriceService.class);
    private final static String ENTITY_NAME = ApartmentPrice.class.getSimpleName();

    @Autowired
    private ApartmentPriceRepository apartmentPriceRepository;

    @Autowired
    private ApartmentClassService apartmentClassService;

    public List<ApartmentPrice> findAll() {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ALL_ENTITY, ENTITY_NAME));

        final List<ApartmentPrice> apartmentPrices = apartmentPriceRepository.findAll();
        logger.info(String.format(CustomEntityLogMessage.FOUND_AMOUNT_ELEMENT, apartmentPrices.size()));

        return apartmentPrices;
    }

    public Long save(ApartmentPrice apartmentPrice) {
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY, ENTITY_NAME));

        final ApartmentClass apartmentClass = apartmentClassService.findById(apartmentPrice.getApartmentClass().getId());
        apartmentPrice.setApartmentClass(apartmentClass);

        final ApartmentPrice save = apartmentPriceRepository.save(apartmentPrice);
        final Long id = save.getId();
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY_WITH_ID, ENTITY_NAME, id));

        return id;
    }

    public ApartmentPrice findById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.FIND_ENTITY_BY_ID, ENTITY_NAME, id));

        return apartmentPriceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public Long update(final ApartmentPrice apartmentPrice, final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.UPDATE_ENTITY, ENTITY_NAME));

        final ApartmentClass apartmentClass = apartmentClassService.findById(apartmentPrice.getApartmentClass().getId());
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, apartmentPrice.getApartmentClass().getId()));


        ApartmentPrice update = apartmentPriceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, apartmentPrice.getApartmentClass().getId()));

        update.setPrice(apartmentPrice.getPrice());
        update.setStartPeriod(apartmentPrice.getStartPeriod());
        update.setEndPeriod(apartmentPrice.getEndPeriod());
        update.setApartmentClass(apartmentClass);
        update = apartmentPriceRepository.save(update);
        logger.trace(String.format(CustomEntityLogMessage.UPDATED_ENTITY_SAVED, ENTITY_NAME));

        return update.getId();
    }

    public void deleteById(final Long id) {
        logger.trace(String.format(CustomEntityLogMessage.DELETE_ENTITY_BY_ID, ENTITY_NAME, id));

        final ApartmentPrice delete = apartmentPriceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_FOR_DELETE, ENTITY_NAME));

        apartmentPriceRepository.delete(delete);
        logger.trace(String.format(CustomEntityLogMessage.ENTITY_DELETED, ENTITY_NAME));
    }

}
