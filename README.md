Storing small musical ideas and playing around with modularization using Guice.

Project structure overview
```
Without dependencies:
music.util  - Contains the musical 'Business logic'
protocol    - Contains an abstract client-server protocol

Dependent on 'music' and 'protocol':
client      - Contains the input and viewing logic
server      - Contains the application state
```