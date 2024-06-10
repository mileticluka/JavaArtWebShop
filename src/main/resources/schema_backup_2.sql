CREATE TABLE CATEGORIES (
                          id INT PRIMARY KEY,
                          name VARCHAR(255)
);

CREATE TABLE ARTISTS (
                        id INT PRIMARY KEY,
                        name VARCHAR(255)
);

CREATE TABLE ART_PIECES (
                           id INT PRIMARY KEY,
                           title VARCHAR(255),
                           artist_id INT,
                           category_id INT,
                           image_path VARCHAR(255),
                           price DECIMAL(10, 2),
                           description NVARCHAR(1000),
                           FOREIGN KEY (artist_id) REFERENCES artists(id),
                           FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE TRANSACTION_ITEMS (
                                   id SERIAL PRIMARY KEY,
                                   art_piece_id BIGINT,
                                   quantity INT,
                                   transaction_id BIGINT,
                                   FOREIGN KEY (art_piece_id) REFERENCES ART_PIECES(id),
                                   FOREIGN KEY (transaction_id) REFERENCES TRANSACTIONS(id)
);

CREATE TABLE TRANSACTIONS (
                              id SERIAL PRIMARY KEY,
                              user_id BIGINT,
                              total_amount DECIMAL(10, 2),
                              transaction_date TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES USERS(id)
);

