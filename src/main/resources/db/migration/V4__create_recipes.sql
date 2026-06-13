create table if not exists recipes (
    id uuid primary key,
    name varchar(255) not null,
    category varchar(255),
    area varchar(255),
    image_url varchar(1024),
    category_image_url varchar(1024),
    area_image_url varchar(1024),
    tags varchar(500),
    youtube_url varchar(1024),
    source_url varchar(1024),
    instructions text,
    ingredient1 varchar(255),
    ingredient2 varchar(255),
    ingredient3 varchar(255),
    ingredient4 varchar(255),
    ingredient5 varchar(255),
    ingredient6 varchar(255),
    ingredient7 varchar(255),
    ingredient8 varchar(255),
    ingredient9 varchar(255),
    ingredient10 varchar(255),
    measure1 varchar(100),
    measure2 varchar(100),
    measure3 varchar(100),
    measure4 varchar(100),
    measure5 varchar(100),
    measure6 varchar(100),
    measure7 varchar(100),
    measure8 varchar(100),
    measure9 varchar(100),
    measure10 varchar(100),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index if not exists idx_recipes_category on recipes(category);
create index if not exists idx_recipes_area on recipes(area);
create index if not exists idx_recipes_name on recipes(name);

insert into recipes (
    id, name, category, area, image_url, category_image_url, area_image_url, tags, youtube_url, source_url, instructions,
    ingredient1, ingredient2, ingredient3, ingredient4, ingredient5, ingredient6, ingredient7,
    measure1, measure2, measure3, measure4, measure5, measure6, measure7, created_at, updated_at
) values
('11111111-1111-1111-1111-111111111111', 'Fettuccine Alfredo', 'Pasta', 'Italian', '/images/restaurant.jpg', '/images/restaurant.jpg', '/images/restaurant.jpg', 'Pasta,Creamy', '', '', 'Cook pasta until tender. Warm cream and butter, fold in parmesan, season with pepper and nutmeg, then toss with pasta until glossy.',
 'Fettuccine', 'Cream', 'Butter', 'Parmesan', 'Nutmeg', 'Black pepper', 'Parsley', '250g', '1 cup', '25g', '100g', 'pinch', 'to taste', 'chopped', now(), now()),
('22222222-2222-2222-2222-222222222222', 'Masala Omelette', 'Breakfast', 'Indian', '/images/biryani.jpeg', '/images/biryani.jpeg', '/images/biryani.jpeg', 'Eggs,Quick', '', '', 'Whisk eggs with onion, tomato, chilli, coriander, and spices. Cook in a hot pan until set and lightly browned.',
 'Eggs', 'Onion', 'Tomato', 'Green chilli', 'Coriander', 'Turmeric', 'Salt', '3', '1 small', '1 small', '1', 'handful', 'pinch', 'to taste', now(), now()),
('33333333-3333-3333-3333-333333333333', 'Vegetable Fried Rice', 'Vegetarian', 'Chinese', '/images/dominospizza.jpeg', '/images/dominospizza.jpeg', '/images/dominospizza.jpeg', 'Rice,Vegetarian', '', '', 'Stir fry vegetables on high heat, add cooked rice, soy sauce, pepper, and spring onion. Toss until hot.',
 'Cooked rice', 'Carrot', 'Beans', 'Capsicum', 'Soy sauce', 'Pepper', 'Spring onion', '2 cups', '1/2 cup', '1/2 cup', '1/2 cup', '1 tbsp', 'to taste', '2 tbsp', now(), now()),
('44444444-4444-4444-4444-444444444444', 'Chicken Curry', 'Chicken', 'Indian', '/images/biryani.jpeg', '/images/biryani.jpeg', '/images/biryani.jpeg', 'Chicken,Curry', '', '', 'Saute onions, ginger, garlic, and spices. Add chicken and tomatoes, simmer until tender, then finish with coriander.',
 'Chicken', 'Onion', 'Tomato', 'Ginger garlic paste', 'Garam masala', 'Chilli powder', 'Coriander', '500g', '2', '2', '1 tbsp', '1 tsp', '1 tsp', 'handful', now(), now()),
('55555555-5555-5555-5555-555555555555', 'Chocolate Pudding', 'Dessert', 'British', '/images/checkout.png', '/images/checkout.png', '/images/checkout.png', 'Dessert,Chocolate', '', '', 'Heat milk with cocoa and sugar, thicken with cornflour, then chill until set.',
 'Milk', 'Cocoa powder', 'Sugar', 'Cornflour', 'Vanilla', null, null, '2 cups', '3 tbsp', '1/2 cup', '2 tbsp', '1 tsp', null, null, now(), now()),
('66666666-6666-6666-6666-666666666666', 'Fish Tacos', 'Seafood', 'Mexican', '/images/restaurant.jpg', '/images/restaurant.jpg', '/images/restaurant.jpg', 'Seafood,Tacos', '', '', 'Season fish and pan fry. Fill tortillas with fish, cabbage, salsa, lime, and a yogurt drizzle.',
 'Fish fillets', 'Tortillas', 'Cabbage', 'Salsa', 'Lime', 'Yogurt', 'Cumin', '400g', '6', '1 cup', '1/2 cup', '1', '1/3 cup', '1 tsp', now(), now()),
('77777777-7777-7777-7777-777777777777', 'Greek Salad', 'Starter', 'Greek', '/images/restaurant.jpg', '/images/restaurant.jpg', '/images/restaurant.jpg', 'Salad,Fresh', '', '', 'Combine cucumber, tomato, onion, olives, and feta. Dress with olive oil, lemon, oregano, salt, and pepper.',
 'Cucumber', 'Tomato', 'Red onion', 'Olives', 'Feta', 'Olive oil', 'Lemon', '1', '2', '1/2', '1/2 cup', '100g', '2 tbsp', '1', now(), now()),
('88888888-8888-8888-8888-888888888888', 'Beef Stir Fry', 'Beef', 'Thai', '/images/restaurant.jpg', '/images/restaurant.jpg', '/images/restaurant.jpg', 'Beef,Quick', '', '', 'Stir fry beef strips with garlic and vegetables, add soy sauce and basil, then serve hot with rice.',
 'Beef strips', 'Garlic', 'Mixed vegetables', 'Soy sauce', 'Basil', 'Oil', 'Rice', '400g', '3 cloves', '2 cups', '2 tbsp', 'handful', '1 tbsp', 'to serve', now(), now())
on conflict (id) do nothing;
