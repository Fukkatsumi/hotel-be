
--drop all objects
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
	
--apartmentStatus - current now
CREATE TYPE apartmentStatus AS ENUM ('ReadyToCheckIn', 'OnRepair', 'NeedCleaning', 'Occupied');
CREATE TYPE taskStatus AS ENUM ('OPEN', 'Started', 'Complete', 'Failed', 'Canceled');
CREATE TYPE speciality AS ENUM ('Cleaner', 'Handyman', 'Manager', 'Hotel Administrator');
CREATE TYPE userRole AS ENUM ('Client', 'Manager', 'Administrator', 'Worker');
CREATE TYPE bookingStatus AS ENUM ('Created', 'CheckedIn', 'Closed', 'Canceled');

--additional services for booking, price is value per day	
CREATE TABLE BookingAddServices (
   id serial PRIMARY KEY,
   service_name VARCHAR (30) UNIQUE NOT NULL,
   price integer NOT NULL
);
/*
--items services for apartment that physical has mounted
CREATE TABLE ApartmentItems (
   id serial PRIMARY KEY,
   service_name VARCHAR (30) UNIQUE NOT NULL
);
*/

--Type (or class) of appartment(lux, half-lux)
CREATE TABLE ApartmentClass (
	id serial PRIMARY KEY,
	name_class VARCHAR(50) NOT NULL,
	number_of_rooms integer NOT NULL,
	number_of_couchette integer NOT NULL
);

CREATE TABLE Apartments (
   id serial PRIMARY KEY,
   room_number integer UNIQUE NOT NULL,
   photo VARCHAR(4000) NOT NULL,
   description VARCHAR(200),
   status apartmentStatus NOT NULL,
   class_room integer references ApartmentClass(id) NOT NULL
);

--apartments that are on repair
CREATE TABLE UnavailableApartments (
	id serial PRIMARY KEY,
	id_apartment integer references Apartments(id) NOT NULL,
	start_date TIMESTAMP NOT NULL,
	end_date TIMESTAMP NOT NULL,
	cause_description VARCHAR(100) NOT NULL
);

--list of apartment price on each period
CREATE TABLE ApartmentPrices (
   id serial PRIMARY KEY,
   price integer NOT NULL,
   start_period TIMESTAMP NOT NULL,
   end_period TIMESTAMP NOT NULL,
   apartment_id integer references Apartments(id) NOT NULL
 );
/*
--relation many to many for apartments and apartmentItems
CREATE TABLE ApartmentItemsShip (
	number_apartment integer references apartments(id) NOT NULL,
	id_apartmentItem integer references ApartmentItems(id) NOT NULL,
	unique(number_apartment, add_service_id_apartment)
);
*/
CREATE TABLE Users (
	id serial PRIMARY KEY,
	login VARCHAR(30) UNIQUE NOT NULL,
	password VARCHAR(512) NOT NULL,
	user_role userRole NOT NULL,
	first_name VARCHAR(20) NOT NULL,
	last_name VARCHAR(30) NOT NULL,
	email VARCHAR(150) UNIQUE NOT NULL,
	phone_number VARCHAR(30) UNIQUE NOT NULL,
	points integer
);

CREATE TABLE Staff (
	id serial PRIMARY KEY,
	user_id integer references Users(id) UNIQUE NOT NULL,
	spec speciality NOT NULL,
	isActive boolean NOT NULL 
);

CREATE TABLE Tasks (
   id serial PRIMARY KEY,
   start_date TIMESTAMP NOT NULL,
   end_date TIMESTAMP NOT NULL,
   accept_date TIMESTAMP,
   complete_date TIMESTAMP,
   description VARCHAR(200),
   task_status taskStatus NOT NULL,
   apartment_id integer references apartments(id) NOT NULL
);

CREATE TABLE Bookings (
    id serial PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
	total_price integer NOT NULL,
	comments VARCHAR(200),
	created_date TIMESTAMP NOT NULL,
	review VARCHAR(300),
	booking_status bookingStatus NOT NULL,
	user_id integer references Users(id) NOT NULL,
	apartmentClass_id integer references ApartmentClass(id) NOT NULL,
	apartment_id integer references Apartments(id)
);

--many to many for staff and tasks
CREATE TABLE StaffTaskShip (
	id serial PRIMARY KEY,
	task_id integer references Tasks(id) NOT NULL,
	executor_id integer references Staff(user_id) NOT NULL,
	creator_id integer references Staff(user_id) NOT NULL,
	unique(task_id, executor_id, creator_id)
);

--many to many for add services and bookings
CREATE TABLE BookingAddServicesShip (
	id serial PRIMARY KEY,
	booking_id integer references Bookings(id) NOT NULL,
	add_service_id_booking integer references BookingAddServices(id) NOT NULL,
	count_services integer NOT NULL,
	unique(booking_id, add_service_id_booking)
);
/*
-- many to many for appartment and booking
CREATE TABLE ApartmentBookingShip (
	booking_id integer references Booking(id) NOT NULL,
	apartment_id integer references Apartments(id) NOT NULL,
	unique(booking_id, apartment_id)
);
*/
