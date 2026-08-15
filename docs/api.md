# BridgeMind Complete API & Protocol Reference

Base URL: `http://localhost:8080/api`  
WebSocket STOMP URL: `ws://localhost:8080/ws`

---

## 🔐 1. Authentication (`/api/auth`)

### Register User
* **Endpoint**: `POST /api/auth/register`
* **Request**:
  ```json
  {
    "email": "developer@bridgemind.local",
    "password": "SecurePassword123!"
  }
  ```
* **Response (200 OK)**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

### Login User
* **Endpoint**: `POST /api/auth/login`
* **Request**:
  ```json
  {
    "email": "developer@bridgemind.local",
    "password": "SecurePassword123!"
  }
  ```
* **Response (200 OK)**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

---

## 🏢 2. Workspaces (`/api/workspaces`)

### List Workspaces
* **Endpoint**: `GET /api/workspaces`
* **Response (200 OK)**: `List<Workspace>`

### Create Workspace
* **Endpoint**: `POST /api/workspaces`
* **Request**: `{ "name": "BridgeMind Production Workspace" }`
* **Response (201 Created)**: `Workspace`

### Delete Workspace
* **Endpoint**: `DELETE /api/workspaces/{workspaceId}`
* **Response (204 No Content)**

### Get Workspace Files (Interactive File Explorer)
* **Endpoint**: `GET /api/workspaces/{workspaceId}/files`
* **Response (200 OK)**: `List<WorkspaceFileNode>`
  ```json
  [
    {
      "name": "src",
      "path": "src",
      "isDirectory": true,
      "size": 0,
      "extension": "",
      "children": [
        {
          "name": "index.ts",
          "path": "src/index.ts",
          "isDirectory": false,
          "size": 1420,
          "extension": "ts"
        }
      ]
    }
  ]
  ```

### Get File Content
* **Endpoint**: `GET /api/workspaces/{workspaceId}/files/content?path={filePath}`
* **Response (200 OK)**:
  ```json
  {
    "path": "src/index.ts",
    "content": "console.log('Hello BridgeMind');"
  }
  ```

### Save File Content
* **Endpoint**: `PUT /api/workspaces/{workspaceId}/files/content`
* **Request**:
  ```json
  {
    "path": "src/index.ts",
    "content": "console.log('Updated content');"
  }
  ```
* **Response (200 OK)**: `{ "status": "SAVED", "path": "src/index.ts" }`

---

## 🌿 3. Git Review & Human-in-the-Loop (`/api/workspaces/{workspaceId}/review`)

### Get Workspace Git Diff
* **Endpoint**: `GET /api/workspaces/{workspaceId}/review/diff`
* **Response (200 OK)**:
  ```json
  {
    "diff": "diff --git a/index.html b/index.html\n+ <h1>Welcome</h1>"
  }
  ```

### Approve & Commit Git Diff
* **Endpoint**: `POST /api/workspaces/{workspaceId}/review/approve?executionId={uuid}&message={commitMsg}`
* **Response (200 OK)**: Status 200

---

## 🧪 4. Automated Sandbox Test Runner (`/api/workspaces/{workspaceId}/tests`)

### Run Test Suite
* **Endpoint**: `POST /api/workspaces/{workspaceId}/tests/run`
* **Response (200 OK)**: `TestRunResult`
  ```json
  {
    "suiteType": "Node.js / Vitest / Jest",
    "status": "PASSED",
    "exitCode": 0,
    "durationMs": 1420,
    "output": "✓ 8 tests passed",
    "summary": "All tests in Node.js / Vitest / Jest suite passed cleanly.",
    "executedAt": "2026-08-15T21:50:00Z"
  }
  ```

### Get Latest Test Result
* **Endpoint**: `GET /api/workspaces/{workspaceId}/tests/latest`
* **Response (200 OK)**: `TestRunResult`

---

## 🌐 5. Model Context Protocol (MCP) Server (`/api/mcp`)

### MCP JSON-RPC 2.0 Handler
* **Endpoint**: `POST /api/mcp`
* **Request (List Tools)**:
  ```json
  {
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/list",
    "params": {}
  }
  ```
* **Response**:
  ```json
  {
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
      "tools": [
        { "name": "read_workspace_file", "description": "..." },
        { "name": "list_workspace_files", "description": "..." },
        { "name": "save_workspace_file", "description": "..." },
        { "name": "get_workspace_git_diff", "description": "..." },
        { "name": "approve_workspace_git_diff", "description": "..." }
      ]
    }
  }
  ```

### MCP Streamable Server-Sent Events (SSE)
* **Endpoint**: `GET /api/mcp/sse?clientId={uuid}`
* **Response**: `text/event-stream`

---

## 🎯 6. Missions & Tasks (`/api/missions`)

### Create Mission
* **Endpoint**: `POST /api/workspaces/{workspaceId}/missions`
* **Request**:
  ```json
  {
    "title": "Build Cyberpunk Space Arcade Game",
    "collaborationMode": "COLLABORATIVE"
  }
  ```
* **Response (201 Created)**: `Mission`

### List Mission Task DAG
* **Endpoint**: `GET /api/missions/{missionId}/tasks`
* **Response (200 OK)**: `List<MissionTask>`

### List Agents
* **Endpoint**: `GET /api/missions/{missionId}/agents`
* **Response (200 OK)**: `List<Agent>`

---

## ⚡ 7. STOMP WebSocket Channels

Connection: `ws://localhost:8080/ws`

* `/topic/workspace/{workspaceId}/missions`: Mission lifecycle events.
* `/topic/mission/{missionId}/agents`: Agent stream logs & status updates.
* `/topic/workspace/{workspaceId}/tests`: Sandbox test execution start and completions.
* `/topic/workspace/{workspaceId}/execution`: Hardened Docker boundary lifecycle events.
* `/topic/workspace/{workspaceId}/runtimes`: External AI coding runtime events.
