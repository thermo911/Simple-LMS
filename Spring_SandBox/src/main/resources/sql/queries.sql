-- courses by category
SELECT * FROM courses
    JOIN categories cat ON courses.category = cat.id
WHERE cat.title = "IT";

-- users by nick
SELECT * FROM users
WHERE nickname LIKE "A%";

-- all courses by author
SELECT c.id       AS course_id,
       c.title    AS course_title,
       u.nickname AS authors_nick
FROM courses c
    JOIN users u ON u.id = c.created_by
WHERE u.nickname = "Herbert";

-- all modules by course
SELECT m.id    AS module_id,
	   c.title AS course_title,
       m.title AS module_title
FROM modules m
    JOIN courses c ON c.id = m.course_id
WHERE c.id = 1;

-- all topics by module
SELECT t.id    AS topic_id,
	   m.title AS module_title,
       t.title AS topic_title
FROM topics t
    JOIN modules m ON t.module_id = m.id
WHERE m.id = 1;

-- top 10 courses
SELECT c.id    AS course_id,
       c.title AS course_title,
       ROUND(SUM(r.score) / COUNT(r.course_id), 2) AS rating
FROM courses c
    JOIN ratings r ON r.course_id = c.id
GROUP BY c.id
ORDER BY rating DESC
LIMIT 10;