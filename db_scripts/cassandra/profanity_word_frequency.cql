CREATE TYPE bodhi.profanity_word_frequency (
    no_of_occurrence int,
    page_occurred set<int>,
    word text,
    level text,
    category text
);
