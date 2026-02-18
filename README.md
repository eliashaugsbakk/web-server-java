## Simple web server written in Java + Java client to upload new web pages to the server

** A webserver and client program to upload pages, written in java, designed to be hosted over Tor. **

### Technical
- The http server is written using the standard Java web server API com.sun.net.httpserver.HttpServer.
- The server is designed to run on my Banana PI BPI-M5 server, and aims to be a relativly light weight http server.
- This Java server is designed to live behind another web server, like nginx. In that case, nginx would handle the static data like images and css as an example. The Java web server would handle the dynamic pages which stores its contents in a database and uses predetermened http templates.

- The Java client to upload pages is a locally run program which prepares the data by converting markdown to HTML and normalizing and compressing images. The client will also handle the connection over Tor as long as it has a local Tor-deamon to connecto to, like the Tor Browser running in the background.
