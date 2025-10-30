CREATE TABLE IF NOT EXISTS user_preferences (
    preference_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    font_size TEXT NULL,
    contrast_mode TEXT NULL,
    CONSTRAINT fk_user_pref_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);



