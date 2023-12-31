CREATE TABLE IF NOT EXISTS marks (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS colors (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    color VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS years (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    year VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cars (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    number VARCHAR NOT NULL UNIQUE,
    mark_id UUID NOT NULL
        CONSTRAINT fk_mark_id REFERENCES marks (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    color_id UUID NOT NULL
        CONSTRAINT fk_color_id REFERENCES colors (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    year_id UUID NOT NULL
        CONSTRAINT fk_year_id REFERENCES years (id) ON UPDATE CASCADE ON DELETE NO ACTION
);
