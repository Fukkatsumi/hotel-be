package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.entity.BookingAddServicesCustom;
import com.netcracker.hotelbe.entity.Email;
import com.netcracker.hotelbe.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static String USER_CREDENTIAL = "You have successfully registered!\n\nHi, %s!\nYour credential\nLogin: %s\nPassword: %s";
    private static String BOOKING_INFORMATION = "Booking #%s:\n\nCustomer:%s %s\nArrive date:%s\nDeparture date:%s\nApartment class:%s\nAdditional services:\n%s\n\nTotalPrice:%s";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    public void sendToUser(Email email, Long userId) {
        User user = userService.findById(userId);

        send(email, user.getEmail());

    }


    public void sendCredentialToUser(Long id) {
        User user = userService.findById(id);

        Email email = new Email();
        email.setSubject("Hotel: your credential");
        email.setText(String.format(USER_CREDENTIAL, user.getFirstname(), user.getLogin(), user.getPassword()));

        send(email, user.getEmail());
    }

    public void sendCredentialToUser(User user) {
        Email email = new Email();
        email.setSubject("Hotel: your credential");
        email.setText(String.format(USER_CREDENTIAL, user.getFirstname(), user.getLogin(), user.getPassword()));

        send(email, user.getEmail());
    }

    public boolean sendBookingInformationToUser(Booking booking) {
        User user = booking.getUser();
        if (user != null) {
            StringBuilder additionalServicesInfo = new StringBuilder();
            List<BookingAddServicesCustom> bookingAddServicesCustomList = bookingService.getServices(booking.getId());
            if (bookingAddServicesCustomList.isEmpty()) {
                additionalServicesInfo.append("none");
            } else {
                for (BookingAddServicesCustom bookingAddServicesCustom : bookingAddServicesCustomList) {
                    String serviceName = bookingAddServicesCustom.getBookingAddServices().getServiceName();
                    int serviceCount = bookingAddServicesCustom.getCountServices();
                    int servicePrice = bookingAddServicesCustom.getBookingAddServices().getPrice();
                    additionalServicesInfo.append("-")
                            .append(serviceName)
                            .append(" x")
                            .append(serviceCount)
                            .append(" - $")
                            .append(servicePrice * serviceCount)
                            .append("\n");
                }
            }

            Email email = new Email();
            email.setSubject("Information about booking #" + booking.getId());
            email.setText(String.format(BOOKING_INFORMATION, booking.getId(), user.getFirstname(), user.getLastname(),
                    booking.getStartDate(), booking.getEndDate(), booking.getApartmentClass().getNameClass(), additionalServicesInfo, booking.getTotalPrice()));

            send(email, user.getEmail());
            return true;
        } else {
            return false;
        }
    }

    private void send(Email email, String address) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(address);
        smm.setSubject(email.getSubject());
        smm.setText(email.getText());

        javaMailSender.send(smm);
    }
}
