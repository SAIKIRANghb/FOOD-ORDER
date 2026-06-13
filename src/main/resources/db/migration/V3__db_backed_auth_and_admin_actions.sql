create table if not exists verification_codes (
    id uuid primary key,
    flow varchar(100) not null,
    identifier varchar(255) not null,
    code varchar(20) not null,
    expires_at timestamptz not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index if not exists idx_verification_codes_flow_identifier_created
    on verification_codes(flow, identifier, created_at desc);

create table if not exists refresh_tokens (
    id uuid primary key,
    user_id uuid references users(id) on delete cascade,
    email varchar(255) not null,
    token varchar(2048) not null unique,
    expires_at timestamptz not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index if not exists idx_refresh_tokens_email on refresh_tokens(email);

create table if not exists admin_credential_shares (
    id uuid primary key,
    admin_id uuid references users(id) on delete cascade,
    shared_at timestamptz not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index if not exists idx_admin_credential_shares_admin
    on admin_credential_shares(admin_id);
