
-- courses by category
SELECT * FROM courses
    JOIN categories cat ON courses.category = cat.id
WHERE cat.title = "...";

-- users by nick
SELECT * FROM users
WHERE nickname LIKE "...";

-- all courses by author
SELECT course.id      AS course_id,
       course.title   AS course_title,
       user_.nickname AS author
FROM courses course
    JOIN users user_ ON user_.id = course.created_by
WHERE user_.nickname == "...";

-- all modules by course
SELECT m.id AS module_id,
       m.title AS module_title,
FROM modules m
    JOIN courses c ON c.id = m.course_id
WHERE c.id = 42;

-- all topics by module
SELECT t.id AS topic_id,
       t.title AS topic_title
FROM topics t
    JOIN modules m ON t.module_id = m.id
WHERE m.id = 42;

-- top 10 courses
SELECT c.id    AS course_id,
       c.title AS course_title,
       SUM(ur.score) / COUNT (ur.user_id) AS rating
FROM courses c
    JOIN ratings r        ON r.id = c.rating
    JOIN users_ratings ur ON ur.rating_id = r.id
GROUP BY c.id
ORDER BY rating DESC
LIMIT 10;