## Indigo ELN Database Schema Installation

To process with Indigo ELN Database Schema, execute the following commands in the current directory:

### Oracle

- Install schema:

        sqlplus sys/admin@//localhost:1521/xe as sysdba @oracle/schema_install.sql
        sqlldr indigo_owner/indigo_owner@//localhost:1521/xe oracle/xml_metadata.ctl

- Uninstall schema:

        sqlplus sys/admin@//localhost:1521/xe as sysdba @oracle/schema_uninstall.sql

### PostgreSQL

- Install schema:

        psql -h localhost -p 5432 -d postgres -U postgres -f postgresql/schema_install.sql

- Uninstall schema:

        psql -h localhost -p 5432 -d postgres -U postgres -f postgresql/schema_uninstall.sql
