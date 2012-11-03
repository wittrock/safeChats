John Wittrock, Greg Herpel

To build:
`   $ cd src/
   $ make `

To run the server:
`   $ make runserver`

To run a client:
`   $ make runclient`
   
Closing a client window will not exit the client program entirely. We did this to allow for multiple chat windows in future. To fully exit the client, you should kill it with Ctrl-C.

Note also that you can start multiple clients by simply opening another terminal in the same src directory and executing `make runclient` again.