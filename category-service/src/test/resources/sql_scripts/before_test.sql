TRUNCATE TABLE categories;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1;
INSERT INTO categories (name, parent_id) VALUES
('Category1', null),
('Category2', 1),
('Category3', null),
('Category4', null),
('Category5', null);
