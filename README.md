# Project Roadmap

This roadmap outlines the development stages for the Tor-based web uploader.

## Phase 1: MVP - Core Connectivity (Current)
* [ ] **[Client]** Implement Tor SOCKS5 proxy integration via OkHttp.
* [ ] **[Server]** Basic Spring Boot REST endpoint to receive binary data.
* [ ] **[Client/Server]** Manual Auth Token exchange via SSH/Text-file.
* [ ] **[Client]** Basic CLI for uploading a single Markdown file.
* [ ] **[Testing]** Verify end-to-end upload from Client -> Tor -> Local Server.

## Phase 2: The Bundler & Integrity
* [ ] **[Client]** ZIP-bundling of Markdown and local image assets.
* [ ] **[Client]** SHA-256 fingerprinting of bundles.
* [ ] **[Server]** Bundle decompression and SHA-256 verification.
* [ ] **[Client]** Persistent configuration storage in `~/.config/`.

## Phase 3: Reliability & UX
* [ ] **[Client]** Upload progress bar for slow Tor circuits.
* [ ] **[Client]** Image normalization and compression engine (pre-upload).
* [ ] **[Client]** Advanced error handling (Tor proxy status, Auth failure, Server downtime).
* [ ] **[Server]** Improved response codes and server-side validation logs.

## Phase 4: Expansion & Clearnet Readiness
* [ ] **[Client]** Smart-switch logic for Tor vs. Clearnet (HTTPS).
* [ ] **[Server]** Multi-user support (Token management for multiple clients).
* [ ] **[Client]** GUI wrapper or enhanced interactive CLI.


Phase 4: Expansion & Clearnet Readiness
[ ] [Client] Smart-switch logic for Tor vs. Clearnet (HTTPS).

[ ] [Server] Multi-user support (Token management for multiple clients).

[ ] [Client] GUI wrapper or enhanced interactive CLI.
