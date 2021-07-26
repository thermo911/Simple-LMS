-- categories
INSERT INTO categories (id, title) VALUES (1, "Science");
INSERT INTO categories (id, title) VALUES (2, "IT");
SELECT * FROM categories;

-- users
INSERT INTO users (id, nickname) VALUES (1, "Alex");
INSERT INTO users (id, nickname) VALUES (2, "Dima");
INSERT INTO users (id, nickname) VALUES (3, "Anna");
INSERT INTO users (id, nickname) VALUES (4, "Herbert");
SELECT * FROM users;

-- courses
INSERT INTO courses (id, title, category, created_by) VALUES (1, "Java", 2, 4); -- IT by Herbert
INSERT INTO courses (id, title, category, created_by) VALUES (2, "Math", 1, 3); -- Science by Anna
INSERT INTO courses (id, title, category, created_by) VALUES (3, "C++",  2, 4); -- IT 
SELECT * FROM courses;

-- modules for Java course by Herbert
INSERT INTO modules (id, course_id, title) VALUES (1, 1, "Introduction");
INSERT INTO modules (id, course_id, title) VALUES (2, 1, "Syntax Basics");
INSERT INTO modules (id, course_id, title) VALUES (3, 1, "Standart Library");
SELECT * FROM modules;

-- topics for Java Introduction
INSERT INTO topics (id, module_id, title) VALUES (1, 1, "History of Java");
INSERT INTO topics (id, module_id, title) VALUES (2, 1, "First program");
INSERT INTO topics (id, module_id, title) VALUES (3, 1, "How it works?");
SELECT * FROM topics; 
	  
-- ratings
INSERT INTO ratings (id, course_id, author_id, score) VALUES (1, 1, 1, 5); -- Alex -> 5 on Java
INSERT INTO ratings (id, course_id, author_id, score) VALUES (2, 2, 1, 3); -- Alex -> 3 on Math
INSERT INTO ratings (id, course_id, author_id, score) VALUES (3, 3, 1, 4); -- Alex -> 4 on C++
INSERT INTO ratings (id, course_id, author_id, score) VALUES (4, 2, 2, 3); -- Dima -> 3 on Math
INSERT INTO ratings (id, course_id, author_id, score) VALUES (5, 1, 3, 4); -- Anna -> 4 on Java
SELECT * FROM ratings;