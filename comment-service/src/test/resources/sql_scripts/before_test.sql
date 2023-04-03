TRUNCATE TABLE question_comments;
ALTER TABLE question_comments ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE answer_comments;
ALTER TABLE answer_comments ALTER COLUMN id RESTART WITH 1;

INSERT INTO question_comments (content, rate, question_id, user_id, date) VALUES
('Comment1', 1, 1, 2, '2023-01-27T10:53:00.092804'),
('Comment2', 2, 1, 2, '2023-01-27T10:52:00.092804'),
('Comment3', 3, 2, 3, '2023-01-27T10:54:00.092804'),
('Comment4', 4, 2, 3, '2023-01-27T10:55:00.092804'),
('Comment5', 5, 4, 3, '2023-01-27T10:52:00.092804'),
('Comment6', 5, 6, 3, '2023-01-27T10:47:00.092804'),
('Comment7', 5, 6, 3, '2023-01-27T10:45:00.092804');

INSERT INTO answer_comments (content, rate, answer_id, user_id, date) VALUES
('Comment1', 1, 1, 2, '2023-01-27T10:53:00.092804'),
('Comment2', 2, 1, 2, '2023-01-27T10:52:00.092804'),
('Comment3', 3, 2, 3, '2023-01-27T10:54:00.092804'),
('Comment4', 4, 2, 3, '2023-01-27T10:55:00.092804'),
('Comment5', 5, 4, 3, '2023-01-27T10:52:00.092804'),
('Comment6', 5, 6, 3, '2023-01-27T10:47:00.092804'),
('Comment7', 5, 6, 3, '2023-01-27T10:45:00.092804');
