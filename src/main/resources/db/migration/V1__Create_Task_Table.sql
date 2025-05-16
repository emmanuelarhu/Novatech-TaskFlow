-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    due_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on frequently queried columns
CREATE INDEX idx_task_status ON tasks (status);
CREATE INDEX idx_task_due_date ON tasks (due_date);

-- Insert some sample data
INSERT INTO tasks (title, description, due_date, status, created_at, updated_at)
VALUES
('Complete project proposal', 'Draft the initial project proposal document with timeline and resource requirements',
 DATEADD('DAY', 3, CURRENT_DATE), 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Review client requirements', 'Go through the client requirements document and make notes for the next meeting',
 DATEADD('DAY', 1, CURRENT_DATE), 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Set up development environment', 'Install and configure all necessary tools and frameworks for the project',
 DATEADD('DAY', -1, CURRENT_DATE), 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Weekly team meeting', 'Prepare agenda and conduct weekly progress review with the development team',
 DATEADD('DAY', 2, CURRENT_DATE), 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Database schema design', 'Create the database schema based on the finalized requirements',
 DATEADD('DAY', 5, CURRENT_DATE), 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);