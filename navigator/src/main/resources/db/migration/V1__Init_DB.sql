CREATE TABLE users (

    id SERIAL NOT NULL,
    social_networks_links VARCHAR,
    interface_language VARCHAR(50) NOT NULL,
    ranking REAL NOT NULL,
    is_blocked SMALLINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    last_request VARCHAR(255),
    reg_time TIMESTAMP NOT NULL,
    role VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
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

CREATE TABLE chat_messages (

     id SERIAL NOT NULL,
     sender_id INT NOT NULL,
     recipient_id INT NOT NULL,
     content VARCHAR(255) NOT NULL,
     time TIMESTAMP NOT NULL,
     status VARCHAR(50) NOT NULL,
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
    user_id INT NOT NULL,
    PRIMARY KEY (id),
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
    longitude REAL NOT NULL,
    latitude REAL NOT NULL,
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
     CONSTRAINT fk_employee_data FOREIGN KEY (employee_data_id) REFERENCES employees_data (employee_id),
     CONSTRAINT fk_employer_requests FOREIGN KEY (employer_requests_id) REFERENCES employers_requests (employer_id)
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
     CONSTRAINT fk_code_name FOREIGN KEY (code_name_id) REFERENCES messages_code_names (id)
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
     PRIMARY KEY (id),
     CONSTRAINT fk_employer_request FOREIGN KEY (employer_requests_id) REFERENCES employers_requests (employer_id)
);

CREATE TABLE professions_to_passive_search (

     id SERIAL NOT NULL,
     passive_search_id INT NOT NULL,
     profession_id INT NOT NULL,
     PRIMARY KEY (id),
     CONSTRAINT fk_passive_search FOREIGN KEY (passive_search_id) REFERENCES employers_passive_search_data (id),
     CONSTRAINT fk_profession FOREIGN KEY (profession_id) REFERENCES professions (id)
);

INSERT INTO languages(id, language_endonym) VALUES (1, 'English'), (2, 'Русский');

INSERT INTO messages_code_names(id ,code_name)
VALUES
(3, 'PROFESSION_ALREADY_EXISTS'),
(4, 'INCORRECT_LIMIT'),
(5, 'NO_INFO_EMPLOYEE'),
(6, 'USER_NOT_FOUND'),
(7, 'PROFESSION_NOT_FOUND'),
(8, 'PROFESSION_SPECIFICATION_REQUIREMENT'),
(9, 'INCORRECT_JOB_ADDRESS'),
(10, 'TOO_MANY_ADDITIONAL_INFO'),
(11, 'SPECIFICATION_DATE_REQUIREMENT'),
(12, 'NOT_CONFIRMED'),
(13, 'PASSIVE_SEARCH_EXISTS_ALREADY'),
(14, 'OFFER_IS_NOT_EXIST'),
(15, 'ACCEPT'),
(16, 'IN_PROCESS'),
(17, 'EMPLOYEE_ACTIVE_SINCE'),
(18, 'ERROR_HAS_OCCURED'),
(19, 'AGREES'),
(20, 'ACCOUNT_DELETION_REQUIREMENT'),
(21, 'ADD'),
(22, 'REMOVE'),
(23, 'SOMETHING_IS_WRONG'),
(24, 'SENDING_CODE_EMAIL_REQUIREMENT'),
(25, 'EMAIL_USER_IS_NOT_FOUND'),
(26, 'RECOVERY_CODE_REQUEST'),
(27, 'NAMES_ARE_INCORRECT'),
(28, 'EMPLOYEES_WORK_REQUIREMENTS_TEXT_TOO_LONG'),
(29, 'USER_IS_TEMPORARILY_BUSY'),
(30, 'NOT_EMAIL'),
(31, 'INCORRECT_PHONE'),
(32, 'TOO_SHORT_PASSWORD'),
(33, 'COMMUNICATION_LANGUAGE_REQUIREMENT'),
(34, 'APP_DOES_NOT_HAVE_LANGUAGE'),
(35, 'INTERFACE_LANGUAGE_REQUIREMENT'),
(36, 'AVATAR_SIZE'),
(37, 'AVATAR_FORMAT'),
(38, 'ACCOUNT_IS_BANNED_MESSAGE'),
(39, 'INCORRECT_ENTERED_CAPTCHA'),
(40, 'CAPTCHA_IS_NOT_EXIST'),
(41, 'PASSWORD_REQUIREMENTS'),
(42, 'RECOVERY_CODE_IS_NOT_FOUND'),
(43, 'INCORRECT_CAPTCHA_SECRET'),
(44, 'EMPLOYEES_DO_NOT_EXIST'),
(45, 'MODERATOR_SETTING_REQUIREMENT'),
(46, 'LANGUAGE_IS_NOT_EXIST '),
(47, 'CODE_NAME_IS_NOT_EXIST'),
(48, 'IN_PROGRAM_MESSAGE_EXISTS_ALREADY'),
(49, 'CODE_NAME_EXISTS_ALREADY'),
(50, 'SOCIAL_NETWORKS_TEXT_TOO_LONG'),
(51, 'SPECIAL_EQUIPMENT_TEXT_TOO_LONG');