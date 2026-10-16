
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS assessment_submissions (
    submission_id INTEGER PRIMARY KEY AUTOINCREMENT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assessment_answers (
    answer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    submission_id INTEGER NOT NULL,
    question_key TEXT NOT NULL,
    answer_value TEXT,
    FOREIGN KEY (submission_id)
        REFERENCES assessment_submissions(submission_id)
        ON DELETE CASCADE
);
