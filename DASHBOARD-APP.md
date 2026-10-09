# Three-tab certificate dashboard

The complete Angular + Spring Boot + Oracle application is in [dashboard-app](dashboard-app/README.md). This is a standalone application; the earlier root-level backend remains available.

Run the new application from `dashboard-app/backend` and `dashboard-app/frontend`. Use only `dashboard-app/database/schema.sql` for this application, in a dedicated Oracle schema. It does not automatically migrate tables or records from the earlier backend. Stop any other backend on port 8080 before starting this version.

Validation: Angular production build and Maven unit tests passed. Live Oracle persistence and SMTP delivery need your environment configuration.
