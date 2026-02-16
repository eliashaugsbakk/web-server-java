# Project Roadmap

## Rewrite webserver without spring
The scope of the should be limited by design. The web server wil only serve html+css sites over http. When being deployed, nginx or some other webserver would probably live in between this webserver and the user. Nginx would serve all static content, like images and error pages, while the java web server would handle displaying different sides from a DB.

* [x] Get a simple static webserver running
* [ ] Implement routing
* [ ] Let different sides to be served depending on the request using a String variable containing the html.
* [ ] JDBC, SQLite
* [ ] Allow sites to be added with markdown syntax

## Security consernce
This web server will be designed to host sites over the Tor-network. The scope of the server would be small to limit the attack surface.
* [ ] SQL injection
* [ ] Cross-site scripting
* [ ] DOS
* [ ] Other potential security issues?

## Phase 1: MVP - Core Connectivity (Current)
* [x] **[Client]** Implement Tor SOCKS5 proxy integration via OkHttp.
* [ ] **[Server]** Move to a persisteint DB (sqlite)
* [ ] **[Server]** Update application.properties to minimize the footprint of the server.
* [ ] **[Server]** (SECURITY) Create robust errorhandling. Do not leak server information to http requests.
* [ ] **[Server]** (SECURITY) Disable all default Spring/Tomcat headers to prevent fingerprinting.
* [ ] **[Server]** Move images out of the DB. DB is for data, folders are for storage.
* [ ] **[Server]** Basic Spring Boot REST endpoint to receive binary data.
* [ ] **[Client/Server]** Manual Auth Token exchange via SSH/Text-file.
* [x] **[Client]** Basic CLI for uploading a single Markdown file.
* [ ] **[Testing]** Verify end-to-end upload from Client -> Tor -> Local Server.

## Phase 2: The Bundler & Integrity
* [x] **[Client]** ZIP-bundling of Markdown and local image assets.
* [x] **[Client]** SHA-256 fingerprinting of bundles.
* [ ] **[Server]** (SECURITY) Bundle decompression and SHA-256 verification.
* [ ] **[Server]** (SECURITY) Implement verification of the zip and image files before prosessing them further. Make sure the files do not contain any unwanted payload.
* [ ] **[SERVER]** (SECURITY) Implement Path Sanitization for ZIP extraction (Anti-ZipSlip).
* [x] **[Client]** Persistent configuration storage in `~/.config/`.

## Phase 3: Reliability & UX
* [ ] **[Client]** Upload progress bar for slow Tor circuits.
* [x] **[Client]** Image normalization and compression engine (pre-upload).
* [ ] **[Client]** Advanced error handling (Tor proxy status, Auth failure, Server downtime).
* [ ] **[Server]** Improved response codes and server-side validation logs.

## Phase 4: Expansion & Clearnet Readiness
* [ ] **[Client]** Smart-switch logic for Tor vs. Clearnet (HTTPS).
* [ ] **[Server]** Multi-user support (Token management for multiple clients).
* [ ] **[Client]** GUI wrapper or enhanced interactive CLI.
