DROP TABLE 'users';
CREATE TABLE 'users' ('_id' INTEGER NOT NULL PRIMARY KEY, 'name' TEXT, 'login' TEXT, 'pass' TEXT, 'type' INTEGER, 'tag_id' TEXT NOT NULL UNIQUE);
INSERT INTO 'users' VALUES (1, 'admin', 'admin', 'admin', 3, '01234567');
