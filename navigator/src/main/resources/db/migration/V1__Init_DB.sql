CREATE TABLE users (
    id SERIAL NOT NULL,
    social_networks_links VARCHAR,
    interface_language VARCHAR(50) NOT NULL,
    ranking DOUBLE NOL NULL,
    is_blocked SMALLINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    last_request VARCHAR(255),
    reg_time TIMESTAMP NOT NULL,
    role VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR,
    restore_code VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE banned_to_user (
     id SERIAL NOT NULL,
     banned_id INT NOT NULL,
     user_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_banned FOREIGN KEY (banned_id) REFERENCES users (id),
     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE captchas (
     id SERIAL NOT NULL,
     generation_time TIMESTAMP NOT NULL,
     code VARCHAR(255) NOT NULL,
     secret_code VARCHAR(255) NOT NULL,
     PRIMARY KEY (id)
);

CREATE TABLE employees_data (
     employee_id SERIAL NOT NULL,
     is_driver_license SMALLINT NOT NULL,
     is_auto SMALLINT NOT NULL,
     employees_work_requirements VARCHAR(255),
     status VARCHAR(50) NOT NULL,
     PRIMARY KEY (employee_id),
     CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES users (id)
);

CREATE TABLE chat_message (
     id SERIAL NOT NULL,
     sender_id INT NOT NULL,
     recipient_id INT NOT NULL,
     content VARCHAR(255) NOT NULL,
     time TIMESTAMP NOT NULL,
     status VARCHAR(50) NOT NULL
     PRIMARY KEY (id),
     CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id),
     CONSTRAINT fk_recipient FOREIGN KEY (recipient_id) REFERENCES users (id)
);

CREATE TABLE chat_notifications (
     id SERIAL NOT NULL,
     sender_id INT NOT NULL,
     sender_name VARCHAR(255) NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id)
);

CREATE TABLE chat_rooms (
     id SERIAL NOT NULL,
     sender_id INT NOT NULL,
     recipient_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id),
     CONSTRAINT fk_recipient FOREIGN KEY (recipient_id) REFERENCES users (id)
);

CREATE TABLE comments (
     id SERIAL NOT NULL,
     from_user_id INT NOT NULL,
     to_user_id INT NOT NULL,
     content VARCHAR(255) NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_from_user FOREIGN KEY (from_user_id) REFERENCES users (id),
     CONSTRAINT fk_to_user FOREIGN KEY (to_user_id) REFERENCES users (id)
);

CREATE TABLE languages (
    id SERIAL NOT NULL,
    language_endonym VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE votes (
    id SERIAL NOT NULL,
    value SMALLINT NOT NULL,
    user_id INT NOT NULL
    PRIMARY KEY (id)
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE professions (
    id SERIAL NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE messages_code_names (
    id SERIAL NOT NULL,
    code_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE locations (
    user_id SERIAL NOT NULL,
    longitude DOUBLE NOT NULL,
    latitude DOUBLE NOT NULL,
    country VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE profession_to_user (
     id SERIAL NOT NULL,
     user_id INT NOT NULL,
     profession_id INT NOT NULL,
     extended_info_from_employee VARCHAR(255),
     PRIMARY KEY (id),
     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
     CONSTRAINT fk_profession FOREIGN KEY (profession_id) REFERENCES professions (id)
);

CREATE TABLE professions_to_passive_search (
     id SERIAL NOT NULL,
     passive_search_id INT NOT NULL,
     profession_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_passive_search FOREIGN KEY (passive_search_id) REFERENCES employers_passive_search_data (id),
     CONSTRAINT fk_profession FOREIGN KEY (profession_id) REFERENCES professions (id)
);

CREATE TABLE employers_requests (
     employer_id SERIAL NOT NULL,
     firm_name VARCHAR(255),
     PRIMARY KEY (employer_id),
     CONSTRAINT fk_employer FOREIGN KEY (employer_id) REFERENCES users (id)
);

CREATE TABLE jobs (
     id SERIAL NOT NULL,
     job_address VARCHAR(255) NOT NULL,
     designated_date_time TIMESTAMP,
     start_date_time TIMESTAMP,
     end_date_time TIMESTAMP,
     payment_and_additional_info VARCHAR(255) NOT NULL,
     employee_data_id INT NOT NULL,
     employer_requests_id INT NOT NULL,
     status VARCHAR(50) NOT NULL,
     expiration_time TIMESTAMP,
     PRIMARY KEY (id),
     CONSTRAINT fk_employee_data FOREIGN KEY (employee_data_id) REFERENCES employees_data (id),
     CONSTRAINT fk_employer_requests FOREIGN KEY (employer_requests_id) REFERENCES employers_requests (id)
);

CREATE TABLE professions_to_job (
     id SERIAL NOT NULL,
     job_id INT NOT NULL,
     profession_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_job FOREIGN KEY (job_id) REFERENCES jobs (id),
     CONSTRAINT fk_profession FOREIGN KEY (profession_id) REFERENCES professions (id)
);

CREATE TABLE professions_names (
     id SERIAL NOT NULL,
     profession_name VARCHAR(50) NOT NULL,
     language_id INT NOT NULL,
     profession_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES languages (id),
     CONSTRAINT fk_profession FOREIGN KEY (profession_id) REFERENCES professions (id)
);

CREATE TABLE language_to_user (
     id SERIAL NOT NULL,
     user_id INT NOT NULL,
     language_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
     CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES languages (id)
);

CREATE TABLE in_program_messages (
     id SERIAL NOT NULL,
     message VARCHAR(255) NOT NULL,
     language_id INT NOT NULL,
     code_name_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES languages (id),
     CONSTRAINT fk_code_name FOREIGN KEY (code_name_id) REFERENCES code_names (id)
);

CREATE TABLE favorite_to_user (
     id SERIAL NOT NULL,
     user_id INT NOT NULL,
     favorite_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
     CONSTRAINT fk_favorite FOREIGN KEY (favorite_id) REFERENCES users (id)
);

CREATE TABLE employers_passive_search_data (
     id SERIAL NOT NULL,
     job_address VARCHAR(255) NOT NULL,
     designated_date_time TIMESTAMP,
     start_date_time TIMESTAMP,
     end_date_time TIMESTAMP,
     payment_and_additional_info VARCHAR(255),
     employer_requests_id INT NOT NULL,
     PRIMARY KEY (employer_id),
     CONSTRAINT fk_employer_request FOREIGN KEY (employer_request_id) REFERENCES employers_request (id)
);

INSERT INTO languages(language_endonym) VALUES ('English'), ('Русский');

INSERT INTO messages_code_names(code_name)
VALUES
('PROFESSION_ALREADY_EXISTS'),
('INCORRECT_LIMIT'),
('NO_INFO_EMPLOYEE'),
('USER_NOT_FOUND'),
('PROFESSION_NOT_FOUND'),
('PROFESSION_SPECIFICATION_REQUIREMENT'),
('INCORRECT_JOB_ADDRESS'),
('TOO_MANY_ADDITIONAL_INFO'),
('SPECIFICATION_DATE_REQUIREMENT'),
('NOT_CONFIRMED'),
('PASSIVE_SEARCH_EXISTS_ALREADY'),
('OFFER_IS_NOT_EXIST'),
('ACCEPT'),
('IN_PROCESS'),
('EMPLOYEE_ACTIVE_SINCE'),
('ERROR_HAS_OCCURED'),
('AGREES'),
('ACCOUNT_DELETION_REQUIREMENT'),
('ADD'),
('REMOVE'),
('SOMETHING_IS_WRONG'),
('SENDING_CODE_EMAIL_REQUIREMENT'),
('EMAIL_USER_IS_NOT_FOUND'),
('RECOVERY_CODE_REQUEST'),
('NAMES_ARE_INCORRECT'),
('EMPLOYEES_WORK_REQUIREMENTS_TEXT_TOO_LONG'),
('USER_IS_TEMPORARILY_BUSY'),
('NOT_EMAIL'),
('INCORRECT_PHONE'),
('TOO_SHORT_PASSWORD'),
('COMMUNICATION_LANGUAGE_REQUIREMENT'),
('APP_DOES_NOT_HAVE_LANGUAGE'),
('INTERFACE_LANGUAGE_REQUIREMENT'),
('AVATAR_SIZE'),
('AVATAR_FORMAT'),
('ACCOUNT_IS_BANNED_MESSAGE'),
('INCORRECT_ENTERED_CAPTCHA'),
('CAPTCHA_IS_NOT_EXIST'),
('PASSWORD_REQUIREMENTS'),
('RECOVERY_CODE_IS_NOT_FOUND'),
('INCORRECT_CAPTCHA_SECRET'),
('EMPLOYEES_DO_NOT_EXIST'),
('MODERATOR_SETTING_REQUIREMENT'),
('LANGUAGE_IS_NOT_EXIST '),
('CODE_NAME_IS_NOT_EXIST'),
('IN_PROGRAM_MESSAGE_EXISTS_ALREADY'),
('CODE_NAME_EXISTS_ALREADY'),
('SOCIAL_NETWORKS_TEXT_TOO_LONG'),
('SPECIAL_EQUIPMENT_TEXT_TOO_LONG');