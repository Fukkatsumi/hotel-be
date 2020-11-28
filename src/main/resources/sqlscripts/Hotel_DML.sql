
DROP TABLE StaffTaskShip;
ALTER TABLE Tasks ADD executor_id integer references Staff(user_id) NOT NULL;
	ALTER TABLE Tasks ADD creator_id integer references Staff(user_id) NOT NULL; 

ALTER TABLE tasks DROP CONSTRAINT tasks_creator_id_fkey;
ALTER TABLE tasks DROP CONSTRAINT tasks_executor_id_fkey;

ALTER TABLE staff DROP COLUMN user_id;
ALTER TABLE staff ADD CONSTRAINT staff_user_id_fkey FOREIGN KEY (id) REFERENCES users(id);

ALTER TABLE tasks ADD CONSTRAINT tasks_creator_id_fkey FOREIGN KEY (creator_id) REFERENCES staff(id);
ALTER TABLE tasks ADD CONSTRAINT tasks_executor_id_fkey FOREIGN KEY (executor_id) REFERENCES staff(id);

ALTER TYPE speciality ADD VALUE 'Hotel_Administrator';

alter table unavailableapartments alter column start_date type date using start_date::date;
alter table unavailableapartments alter column end_date type date using end_date::date;
alter table bookings alter column start_date type date using start_date::date;
alter table bookings alter column end_date type date using end_date::date;
alter table apartmentprices alter column start_period type date using start_period::date;
alter table apartmentprices alter column end_period type date using end_period::date;

ALTER TABLE apartmentclass
    ADD CONSTRAINT apartmentclass_name_unique UNIQUE (name_class);
