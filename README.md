# Markdown server
### A lightweight server and client for serving and viewing web pages written in markdown. Used for a wiki or blog type website.
This project includes:
* Web-Server written in Java
* Terminal client for uploading new pages, written in Java.
* A simple HTML template for the web pages.

The server is designed with Tor and I2P hosting in mind, keeping resource usage and page weight minimal.

## Future improvements
* Make the Web-Server agnostic to what content is being hosted.
* Frontend using Vite(probably).
* Home/Landing page.
* Improvments to page uploads: either by improving the CLI tool or moving to a web based one. Automatic key exchange, with username and password, instead of manually distributed tokens would be an important improvement.
* Admin panel to manage page content (will probably include deleting pages and do small edits).
* Client-side search.
