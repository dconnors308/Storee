/*--------------------------------------------------
					ACCESS DB
GRANT ALL PRIVILEGES ON STOHRE.* TO 'root'@'localhost';
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('123KSDG#!@$5267FEW');
--------------------------------------------------*/
GRANT ALL PRIVILEGES ON stohre.* TO 'root'@'localhost';
SET PASSWORD FOR 'root'@'localhost' = '123KSDG#!@$5267FEW';
CREATE DATABASE IF NOT EXISTS stohre;
USE stohre;
/*--------------------------------------------------
					DROP TABLES
--------------------------------------------------*/
DROP TABLE IF EXISTS STORIES;
DROP TABLE IF EXISTS USER_GROUPS;
DROP TABLE IF EXISTS STORY_GROUPS;
DROP TABLE IF EXISTS USERS;
/*--------------------------------------------------
					CREATE TABLES
--------------------------------------------------*/
CREATE TABLE IF NOT EXISTS USERS (
USER_ID INT(6) UNSIGNED UNIQUE,
USER_NAME VARCHAR(500) UNIQUE,
DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP,
DATE_UPDATED DATETIME,
PRIMARY KEY (USER_ID)
); 
CREATE TABLE IF NOT EXISTS STORY_GROUPS (
GROUP_ID INT(6) UNSIGNED AUTO_INCREMENT,
USER_GROUP_ID INT(6) UNSIGNED,
GROUP_NAME VARCHAR(500),
DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP,
DATE_UPDATED DATETIME,
PRIMARY KEY (GROUP_ID)
);
CREATE TABLE IF NOT EXISTS USER_GROUPS (
USER_GROUP_ID INT(6) UNSIGNED AUTO_INCREMENT,
USER_ID INT(6) UNSIGNED,
GROUP_ID INT(6) UNSIGNED,
DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP,
DATE_UPDATED DATETIME,
PRIMARY KEY (USER_GROUP_ID),
FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE,
FOREIGN KEY (GROUP_ID) REFERENCES STORY_GROUPS(GROUP_ID) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS STORIES (
STORY_ID INT(6) UNSIGNED AUTO_INCREMENT,
GROUP_ID INT(6) UNSIGNED,
STORY_NAME VARCHAR(500),
STORY_TEXT VARCHAR(2000),
DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP,
DATE_UPDATED DATETIME,
PRIMARY KEY (STORY_ID),
FOREIGN KEY (GROUP_ID) REFERENCES STORY_GROUPS(GROUP_ID) 
);

/*--------------------------------------------------
					INSERT TEST DATA
--------------------------------------------------*/
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(1,"USER1");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(2,"USER2");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(3,"USER3");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(4,"USER4");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(5,"USER5");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(6,"USER6");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(7,"USER7");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(8,"USER8");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(9,"USER9");
INSERT INTO USERS (USER_ID,USER_NAME) VALUES(10,"USER10");
INSERT INTO STORY_GROUPS (USER_GROUP_ID,GROUP_NAME) VALUES(1,"USERS1-3");
INSERT INTO STORY_GROUPS (USER_GROUP_ID,GROUP_NAME) VALUES(2,"USERS4-7");
INSERT INTO STORY_GROUPS (USER_GROUP_ID,GROUP_NAME) VALUES(3,"USERS8-9");
INSERT INTO STORY_GROUPS (USER_GROUP_ID,GROUP_NAME) VALUES(4,"USERS10");
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(1,1);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(2,1);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(3,1);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(4,2);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(5,2);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(6,2);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(7,2);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(8,3);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(9,3);
INSERT INTO USER_GROUPS (USER_ID,GROUP_ID) VALUES(10,4);
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (1,"DOOLER GOES TO VEGAS","ONCE UPON A TIME DOOLER WENT TO VEGAS");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (1,"DOOLER GOES TO ROME","ONCE UPON A TIME DOOLER WENT TO ROME");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (1,"DOOLER GOES TO AUSTRIA","ONCE UPON A TIME DOOLER WENT TO AUSTRIA");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (1,"DOOLER GOES TO TEXAS","ONCE UPON A TIME DOOLER WENT TO TEXAS");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (2,"DOOLER GOES TO ZANZABAR","ONCE UPON A TIME DOOLER WENT TO ZANZABAR");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (2,"DOOLER GOES TO WELLINGTON","ONCE UPON A TIME DOOLER WENT TO WELLINGTON");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (2,"DOOLER GOES TO CHINA","ONCE UPON A TIME DOOLER WENT TO CHINA");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (3,"DOOLER GOES TO RUSSIA","ONCE UPON A TIME DOOLER WENT TO RUSSIA");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (4,"DOOLER GOES TO ITALY","ONCE UPON A TIME DOOLER WENT TO ITALY");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (4,"DOOLER GOES TO ZIMBABWE","ONCE UPON A TIME DOOLER WENT TO ZIMBABWE");
INSERT INTO STORIES (GROUP_ID,STORY_NAME,STORY_TEXT) VALUES (4,"DOOLER GOES TO ALGERIA","ONCE UPON A TIME DOOLER WENT TO ALGERIA");
/*--------------------------------------------------
					SELECT TEST DATA
--------------------------------------------------*/
SELECT
U.USER_NAME,
U.USER_ID,
U.DATE_CREATED,
U.DATE_UPDATED,
UG.GROUP_ID,
G.GROUP_NAME,
S.STORY_NAME,
S.STORY_TEXT
FROM STORIES S
JOIN USER_GROUPS UG ON S.GROUP_ID = UG.GROUP_ID
JOIN USERS U ON UG.USER_ID = U.USER_ID
JOIN STORY_GROUPS G ON UG.GROUP_ID = G.GROUP_ID
WHERE 1=1
AND S.GROUP_ID =1
ORDER BY U.USER_ID