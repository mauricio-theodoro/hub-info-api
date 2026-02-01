ALTER TABLE captcha_challenges
  ADD COLUMN context_key VARCHAR(80) NULL AFTER page_url;
