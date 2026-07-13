# Database Schema

PostgreSQL is used as the primary database. Tables are automatically generated and managed by Hibernate based on `@Entity` definitions.

## Entities

### `workspaces`
The root organizational entity.
- **id** (UUID, Primary Key)
- **name** (VARCHAR, Not Null)
- **created_at** (TIMESTAMP)

### `missions`
A specific task or project within a Workspace.
- **id** (UUID, Primary Key)
- **workspace_id** (UUID, Foreign Key -> `workspaces.id`, Not Null)
- **title** (VARCHAR, Not Null)
- **status** (VARCHAR/Enum: CREATED, PLANNING, RUNNING, REVIEW, MERGING, DONE)
- **created_at** (TIMESTAMP)

### `agents`
Individual autonomous agents spawned to work on a Mission.
- **id** (UUID, Primary Key)
- **mission_id** (UUID, Foreign Key -> `missions.id`, Not Null)
- **role** (VARCHAR/Enum: ARCHITECT, BACKEND_ENGINEER, FRONTEND_ENGINEER, DEVOPS_ENGINEER)
- **display_name** (VARCHAR)
- **model** (VARCHAR, e.g., 'claude-3-5-sonnet-latest')
- **status** (VARCHAR/Enum: WAITING, RUNNING, COMPLETED, FAILED)
- **cost_cents** (BIGINT, default 0)
- **elapsed_ms** (BIGINT, default 0)
- **last_output** (TEXT, nullable) - Stores the massive simulated IO log for the terminal.
- **created_at** (TIMESTAMP)
- **updated_at** (TIMESTAMP)

### `timeline_entries`
Immutable audit log of all significant events happening in a Workspace.
- **id** (UUID, Primary Key)
- **workspace_id** (UUID, Foreign Key -> `workspaces.id`, Not Null)
- **event_type** (VARCHAR)
- **actor_name** (VARCHAR)
- **actor_type** (VARCHAR)
- **summary** (TEXT)
- **details** (TEXT, nullable)
- **created_at** (TIMESTAMP)

## Relationships & Constraints
- All relationships are unidirectional from child to parent (e.g., Mission `ManyToOne` Workspace).
- Deleting a Workspace cascades and deletes all associated Missions and Timeline Entries (via JPA orchestration).
- Deleting a Mission cascades and deletes all associated Agents.
