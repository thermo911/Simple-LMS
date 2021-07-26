CREATE TABLE IF NOT EXISTS categories (
	id    INTEGER PRIMARY KEY,
	title VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS users (
	id            INTEGER PRIMARY KEY,
	nickname      VARCHAR(30) UNIQUE,
	email         VARCHAR(60) UNIQUE,
	password      VARCHAR(30),
	name          VARCHAR(30),
	surname       VARCHAR(30),
	access_status VARCHAR(30),
	avatar        BLOB,
	registered_at DATE,
	updated_at    DATE,
	deleted_at    DATE,
	updated_by    INTEGER,
	deleted_by    INTEGER,
	
	FOREIGN KEY (updated_by) REFERENCES users (id),
	FOREIGN KEY (deleted_by) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS courses (
	id          INTEGER PRIMARY KEY,
	title       VARCHAR(30),
	description TEXT,
	category    INTEGER,
	hours       INTEGER,
	created_by  INTEGER,
	updated_by  INTEGER,
	deleted_by  INTEGER,
	created_at  DATE,
	updated_at  DATE,
	deleted_at  DATE,
	
	
	FOREIGN KEY (created_by) REFERENCES users      (id),
	FOREIGN KEY (updated_by) REFERENCES users      (id),
	FOREIGN KEY (deleted_by) REFERENCES users      (id),
	FOREIGN KEY (category)   REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS modules (
	id          INTEGER PRIMARY KEY,
	course_id   INTEGER,
	title       VARCHAR(30),
	description TEXT,
	created_by  INTEGER,
	updated_by  INTEGER,
	deleted_by  INTEGER,
	created_at  DATE,
	updated_at  DATE,
	deleted_at  DATE,
	
	FOREIGN KEY (created_by) REFERENCES users   (id),
	FOREIGN KEY (updated_by) REFERENCES users   (id),
	FOREIGN KEY (deleted_by) REFERENCES users   (id),
	FOREIGN KEY (course_id)  REFERENCES courses (id)
);

CREATE TABLE IF NOT EXISTS topics (
	id          INTEGER PRIMARY KEY,
	module_id   INTEGER,
	title       VARCHAR(30),
	description TEXT,
	created_by  INTEGER,
	updated_by  INTEGER,
	deleted_by  INTEGER,
	created_at  DATE,
	updated_at  DATE,
	deleted_at  DATE,
	
	FOREIGN KEY (created_by) REFERENCES users   (id),
	FOREIGN KEY (updated_by) REFERENCES users   (id),
	FOREIGN KEY (deleted_by) REFERENCES users   (id),
	FOREIGN KEY (module_id)  REFERENCES modules (id)
);

CREATE TABLE IF NOT EXISTS tasks (
	id          INTEGER PRIMARY KEY,
	topic_id    INTEGER,
	title       VARCHAR(30),
	description TEXT,
	
	FOREIGN KEY (topic_id) REFERENCES topics (id)
);

CREATE TABLE IF NOT EXISTS contents ( 
	id       INTEGER PRIMARY KEY,
	topic_id INTEGER,
	kind     VARCHAR(20),
	content  BLOB,
	
	FOREIGN KEY (topic_id) REFERENCES topics (id)
);


CREATE TABLE IF NOT EXISTS users_courses (
	user_id   INTEGER,
	course_id INTEGER,
	
	FOREIGN KEY (user_id)   REFERENCES users   (id) ON DELETE CASCADE,
	FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ratings (
	id        INTEGER PRIMARY KEY,
	course_id INTEGER,
	author_id INTEGER,
	score     INTEGER,
	
	FOREIGN KEY (course_id) REFERENCES courses (id),
	FOREIGN KEY (author_id) REFERENCES users   (id)
);





