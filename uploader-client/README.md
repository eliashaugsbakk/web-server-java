# Client program for uploading pages to the website

### User stories (As a user, I want to...)

- Run a local program to upload data to the website.
- Configure the url and authentication once. (Shoul persist)
- The program should handle:
  - preparation of data (markdown file and images).
  - Connection to the webserver (via http)
  - Authentication via Authentication token.
- As a user I want to know if a file uploaded successfully.
- I want to see a progressbar of the upload. Is it working properly?


## Technical

- The auth token needs to be mirrored both on the client and server. It should only be pointed to a storage location, not stored in the source code.
- The auth token should be generated on the client and transferred to the server manually (ssh or otherwise).
- The http connection will be over the tor network requiring a local tor node to be running. The client would need to choose what port the connection is on. Default on linux running a tor instance is 9050. A connection could also use the tor-browser port (9150). The default connection will therefor be to localhost:9050, with options to choose tor-browser(9150), or a custom port.
- The program will use the library OkHttp to handle the http connection.
- The program should normalize images and compress them down to a manageable size.
- The program should implement a standard for referencing images in the markdown file, so the server knows where to find the images.
- The upload should contain a fingerprint of the data to let the server validate the received data, and possibly respond with a success/failer.
- Auth token and fingerprint could be sent as a http header.
- The files that are being uploaded should be bundled in a zip or tar file.
- Proper error messages to help the user debug issues. Is the server down, is the tor-proxy not running?, did the authentication fail?
- Store program files in ./config, such as auth-tokens and url-addresses.

### Design

#### Program parts

- User interface (CLI)
- Auth service (Token generation)
- Bundle Service (zip or tarfiles + hash)
- Config Manager (Read/Write of url, port and token to ~/.config)
- Network Service: Http with tor-proxy
