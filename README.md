**Artamus** is a music analysis client/server application.

### Background
For quite some years I've been thinking about what Photoshop for symbolic music (that is, sheet music) would look like. What would it mean to manipulate musical symbols in a meaningful way, and be able to let the computer assist you in making musical decisions.

After a couple of failed attempts in other languages I decided I wanted to further develop my scala skills, and I might as well give the project a new try. To make for an optimal learning experience I decided to use as few libraries as possible, write the program from scratch and just see how far I would get.

Thus **Artamus** was born. 

### Structure

The application is separated into several subprojects and packages. These subprojects are outlined below.
- **Client** Contains the client application with the input and viewing logic
- **Server** Contains the application state and does the heavy lifting
- **Common** Contains the core and some shared packages
- **Network** Contains socket communication (which might be split off entirely into a separate package eventually)
- **Storage** Contains a lightweight memory/file storage (which might be split off entirely into a separate package eventually)

#### Core

Artamus core contains the main data structures and operations. It's design is outlined in the diagram.
![core-design](doc/img/core-design.png)

#### Requirements

The server application requires `Lilypond` which can be downloaded at:
http://lilypond.org/download.html



[Keyboard Reference](docs.keyboard.md)
