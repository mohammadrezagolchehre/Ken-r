module github.com/kenar/backend

go 1.22

// Foundation is intentionally stdlib-only so it compiles with zero external
// fetches. External deps (pgx, redis, websocket, minio) are added in the
// adapter that introduces them. See ROADMAP.md §12.
