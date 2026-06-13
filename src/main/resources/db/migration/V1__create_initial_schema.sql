create table users (
    id uuid primary key,
    email varchar(255) not null unique,
    mobile_number varchar(255) unique,
    password varchar(255) not null,
    first_name varchar(255),
    last_name varchar(255),
    image_url varchar(1024),
    image_public_id varchar(255),
    role varchar(50) not null,
    status varchar(50) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table user_addresses (
    user_id uuid not null references users(id) on delete cascade,
    address_line1 varchar(500),
    address_line2 varchar(500),
    city varchar(255),
    state varchar(255),
    postal_code varchar(50),
    country varchar(255),
    latitude double precision,
    longitude double precision
);

create table restaurants (
    id uuid primary key,
    owner_id uuid references users(id),
    name varchar(255) not null,
    description text,
    cuisine_type varchar(255),
    address_line1 varchar(500),
    address_line2 varchar(500),
    city varchar(255),
    state varchar(255),
    postal_code varchar(50),
    country varchar(255),
    latitude double precision,
    longitude double precision,
    image_url varchar(1024),
    average_rating double precision,
    active boolean,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table menu_items (
    id uuid primary key,
    restaurant_id uuid references restaurants(id),
    name varchar(255) not null,
    description text,
    price numeric(12, 2) not null,
    category varchar(255),
    veg_or_non_veg varchar(50),
    image_url varchar(1024),
    available boolean,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table orders (
    id uuid primary key,
    user_id uuid references users(id),
    restaurant_id uuid references restaurants(id),
    total_amount numeric(12, 2) not null,
    status varchar(50) not null,
    address_line1 varchar(500),
    address_line2 varchar(500),
    city varchar(255),
    state varchar(255),
    postal_code varchar(50),
    country varchar(255),
    latitude double precision,
    longitude double precision,
    payment_method varchar(100),
    payment_status varchar(50),
    ordered_at timestamp,
    delivered_at timestamp,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table order_items (
    id uuid primary key,
    order_id uuid references orders(id) on delete cascade,
    menu_item_id uuid references menu_items(id),
    item_name varchar(255) not null,
    quantity integer not null,
    unit_price numeric(12, 2) not null,
    subtotal numeric(12, 2) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table reviews (
    id uuid primary key,
    user_id uuid references users(id),
    restaurant_id uuid references restaurants(id),
    rating integer not null,
    comment text,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table announcements (
    id uuid primary key,
    title varchar(255),
    message text,
    active boolean not null,
    expires_at timestamptz,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table contact_messages (
    id uuid primary key,
    name varchar(255),
    email varchar(255),
    subject varchar(255),
    message text,
    resolved boolean not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_users_email on users(email);
create index idx_users_mobile_number on users(mobile_number);
create index idx_orders_user_status on orders(user_id, status);
create index idx_orders_restaurant on orders(restaurant_id);
create index idx_menu_items_restaurant_category on menu_items(restaurant_id, category);
create index idx_reviews_restaurant on reviews(restaurant_id);
