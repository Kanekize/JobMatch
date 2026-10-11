CREATE DATABASE IF NOT EXISTS assessment;

USE assessment;

CREATE TABLE IF NOT EXISTS assessment_submissions (
    submission_id INT AUTO_INCREMENT PRIMARY KEY,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assessment_answers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    question_key VARCHAR(100) NOT NULL,
    answer_value TEXT,
    FOREIGN KEY (submission_id)
        REFERENCES assessment_submissions(submission_id)
        ON DELETE CASCADE
);