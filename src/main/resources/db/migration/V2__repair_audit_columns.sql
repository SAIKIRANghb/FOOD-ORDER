alter table if exists users add column if not exists created_at timestamptz;
alter table if exists users add column if not exists updated_at timestamptz;
update users set created_at = coalesce(created_at, now()) where created_at is null;
update users set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists users alter column created_at set not null;
alter table if exists users alter column updated_at set not null;

alter table if exists restaurants add column if not exists created_at timestamptz;
alter table if exists restaurants add column if not exists updated_at timestamptz;
update restaurants set created_at = coalesce(created_at, now()) where created_at is null;
update restaurants set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists restaurants alter column created_at set not null;
alter table if exists restaurants alter column updated_at set not null;

alter table if exists menu_items add column if not exists created_at timestamptz;
alter table if exists menu_items add column if not exists updated_at timestamptz;
update menu_items set created_at = coalesce(created_at, now()) where created_at is null;
update menu_items set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists menu_items alter column created_at set not null;
alter table if exists menu_items alter column updated_at set not null;

alter table if exists orders add column if not exists created_at timestamptz;
alter table if exists orders add column if not exists updated_at timestamptz;
update orders set created_at = coalesce(created_at, now()) where created_at is null;
update orders set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists orders alter column created_at set not null;
alter table if exists orders alter column updated_at set not null;

alter table if exists order_items add column if not exists created_at timestamptz;
alter table if exists order_items add column if not exists updated_at timestamptz;
update order_items set created_at = coalesce(created_at, now()) where created_at is null;
update order_items set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists order_items alter column created_at set not null;
alter table if exists order_items alter column updated_at set not null;

alter table if exists reviews add column if not exists created_at timestamptz;
alter table if exists reviews add column if not exists updated_at timestamptz;
update reviews set created_at = coalesce(created_at, now()) where created_at is null;
update reviews set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists reviews alter column created_at set not null;
alter table if exists reviews alter column updated_at set not null;

alter table if exists announcements add column if not exists created_at timestamptz;
alter table if exists announcements add column if not exists updated_at timestamptz;
update announcements set created_at = coalesce(created_at, now()) where created_at is null;
update announcements set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists announcements alter column created_at set not null;
alter table if exists announcements alter column updated_at set not null;

alter table if exists contact_messages add column if not exists created_at timestamptz;
alter table if exists contact_messages add column if not exists updated_at timestamptz;
update contact_messages set created_at = coalesce(created_at, now()) where created_at is null;
update contact_messages set updated_at = coalesce(updated_at, now()) where updated_at is null;
alter table if exists contact_messages alter column created_at set not null;
alter table if exists contact_messages alter column updated_at set not null;
