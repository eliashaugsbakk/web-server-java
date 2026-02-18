# Client program for uploading pages to the website

### User stories (As a user, I want to...)

- Run a local program to upload data to the website.
- Configure the url and authentication once. (Shoul persist)
- The program should handle:
  - preparation of data (markdown file and images).
  - Connection to the webserver (via http)
  - Authentication via Authentication token.
- As a user I want to know if a file uploaded successfully.


## Description 
Local Java program to upload new pages, including images, to the web server.
To beguinn with, a manually distributed authentication token will be used to authenticate the client.
Later, user name + passphrase will be used. (Could also include a 2FA like TOTP)
The client will send the data to the endpoint: server-url/upload/.
The data will be sent as a JSON file containing preprossed data (Normalized and compressed images + html).
**The client will take input:**
- Markdown text file
- File/article name
- Image files
Since the images will be compressed by the client, and since the expected text files are limited in size,
compression or bundeling will not be needed for the time being.

A GUI could be implemented in the future, probably using JavaFX.

Would a fingerprint of the uploaded content be usefull for the server to validate the data?

## Technical

- The auth token needs to be mirrored both on the client and server. It should only be pointed to a storage location, not stored in the source code.
- The auth token should be generated on the client and transferred to the server manually (ssh or otherwise).
- The http connection will be over the tor network requiring a local tor node to be running. The client would need to choose what port the connection is on. Default on linux running a tor instance is 9050. A connection could also use the tor-browser port (9150). The default connection will therefor be to localhost:9050, with options to choose tor-browser(9150), or a custom port.
- The program will use the library OkHttp to handle the http connection.
- The program should normalize images and compress them down to a reasonable format and size.
- The program should validate that the images referanced in the markdown file is aslo being uploaded.
- The upload should contain a fingerprint of the data to let the server validate the received data, and possibly respond with a success/failer.
- Auth token could be sent as a http header.
- Proper error messages to help the user debug issues. Is the server down, is the tor-proxy not running?, did the authentication fail?




To beguinn with, the client will feature a flag based interface:

```
java -jar webServerUploader.jar --url http://url.onion --port 9150 --token hh23kjf --name "Page name" page.md image1.png image2.png  
java -jar webServerUploader.jar -u http://url.onion -p 9150 -t hh23kjf -n "Page name" page.md image1.png image2.png  
```
The order of the flags would probably not matter, and the distiction between markdown and images would probably be done by reading the file extentions.
The settings flags (--url, --port, --token) would be stored in between sessions in .config/webServerUploader
The port would be the port towards the local tor deamon, likley run by Tor-deamon or the Tor Browser.
File pats could be relative from where the program is ran, or an absolute path.

