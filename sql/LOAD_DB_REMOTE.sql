USE u836275703_store;
/*--------------------------------------------------
					DROP TABLES
--------------------------------------------------*/
DROP TABLE IF EXISTS STORY_GROUPS;
DROP TABLE IF EXISTS STORIES;
DROP TABLE IF EXISTS USERS;
/*--------------------------------------------------
					CREATE TABLES
--------------------------------------------------*/
CREATE TABLE IF NOT EXISTS USERS (
USER_ID VARCHAR(50) UNIQUE,
USER_NAME VARCHAR(500) UNIQUE,
DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP,
DATE_UPDATED DATETIME,
PRIMARY KEY (USER_ID)
); 
CREATE TABLE IF NOT EXISTS STORIES (
STORY_ID INT(8) UNSIGNED AUTO_INCREMENT,
USER_ID VARCHAR(50),
STORY_NAME VARCHAR(500),
STORY_TEXT MEDIUMTEXT,
DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP,
DATE_UPDATED DATETIME,
PRIMARY KEY (STORY_ID),
FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS STORY_GROUPS (
STORY_GROUP_ID INT(8) UNSIGNED AUTO_INCREMENT,
STORY_ID INT(8) UNSIGNED,
USER_ID VARCHAR(50),
MODERATOR BOOLEAN,
DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP,
DATE_UPDATED DATETIME,
PRIMARY KEY (STORY_GROUP_ID),
FOREIGN KEY (STORY_ID) REFERENCES STORIES(STORY_ID) ON DELETE CASCADE, 
FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) 
);
DELIMITER //
CREATE TRIGGER NEW_STORY
AFTER INSERT
   ON STORIES FOR EACH ROW
BEGIN
   DECLARE MODERATOR BOOLEAN;
   SELECT TRUE INTO MODERATOR;
   INSERT INTO STORY_GROUPS (STORY_ID,USER_ID,MODERATOR) VALUES (NEW.STORY_ID,NEW.USER_ID,MODERATOR );
END; //
DELIMITER ;