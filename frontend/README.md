# 🖥️ BridgeMind Frontend Studio

The modern, responsive web application for **BridgeMind — The Autonomous Development Environment (ADE)**. Built with React 19, TypeScript, Vite, Tailwind CSS v4, Zustand, and TanStack React Query.

---

## 🚀 Key UI Modules

* **Interactive File Explorer (`BridgeSpace`)**: Recursive directory tree, file search filter, and path-traversal protected in-browser code editor.
* **Swarm Topology Task DAG Bar (`BridgeSwarm`)**: Visualizes flow through Coordinator $\rightarrow$ Builder $\rightarrow$ Scout $\rightarrow$ Reviewer with real-time status badges.
* **4-Pane Terminal Multiplexer (`AgentGrid`)**: Matrix view with live STOMP streaming outputs, auto-scrolling, and blinking cursor effects.
* **Git Diff Review & Approval (`BridgeBoard`)**: Color-coded addition (`+` emerald) and deletion (`-` rose) syntax highlighting with 1-click commit approval.
* **Automated Sandbox Test Runner (`TestRunnerPanel`)**: Trigger and monitor unit/integration test executions in real time with duration timer and failure logs.
* **Live HTML5 Canvas Preview**: Sandboxed playable iframe preview with instant Desktop, Tablet, and Mobile viewport switching.
* **Memory Inspector & Live Event Stream**: Collapsible bottom drawer for audit logs and STOMP event telemetry.

---

## 🛠️ Tech Stack & Dependencies

* **Framework**: React 19 + TypeScript + Vite
* **State Management**: Zustand (`workspaceStore.ts`)
* **Server State & Caching**: TanStack React Query (`@tanstack/react-query`)
* **Real-time Streaming**: STOMP over SockJS (`@stomp/stompjs`, `sockjs-client`)
* **Styling**: Tailwind CSS v4 + Lucide Icons (`lucide-react`)
* **Testing**: Vitest + React Testing Library + JSDOM

---

## ⚡ Development & Scripts

### 1. Install Dependencies
```bash
npm install
```

### 2. Start Local Development Server
```bash
npm run dev
```
Starts the Vite dev server at `http://localhost:5173`.

### 3. Run Automated Tests
```bash
npm test -- --run
```

### 4. Build for Production
```bash
npm run build
```
Creates an optimized production bundle in `dist/`.
