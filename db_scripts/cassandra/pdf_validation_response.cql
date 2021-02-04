CREATE TABLE bodhi.pdf_validation_response (
    content_id text,
    pdf_file_name text,
    isCompleted boolean,
    total_pages int,
    profanity_word_count int,
    total_page_images int,
    score float,
    image_occrances text,
    overall_text_classification text,
    error_message text,
    profanity_word_list list<frozen<profanity_word_frequency>>,
    PRIMARY KEY (content_id, pdf_file_name)
);
