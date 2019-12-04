
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