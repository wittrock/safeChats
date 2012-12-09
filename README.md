John Wittrock, Greg Herpel

To build: ` $ cd src/ $ make `

To run the server: ` $ make runserver`

To run a client: ` $ make runclient host=<HOST>` where <HOST> is the
IP address of the server. If you do not specify this argument, the
client will default to connecting to localhost.

Note also that you can start multiple clients by simply opening
another terminal in the same src directory and executing `make
runclient` again.

---------------------------------------------

Some instructions on how to use the program:

The initial window you see will be a login screen. You can either
login with an existing account (you may not have one on the machine
you're testing on), or you can create a new account by clicking on the
button at the top of the window. 

Once you've logged in or created a new account, you'll see the user
list frame, which holds a list of the people online (likely only you),
and a "Create Chat" button. You can create a new chat with this button. 

If you open another client and log in as another user, you can invite
the second user from the first chat window by typing their name in the
text field above the "Invite" button in the chat window, and pressing
the "Invite" button. A window from the second client session will then
pop up, and you can chat between the users. If you create further chat
rooms, you will not be able to see the messages sent in them in any
other chat room. 

When you close the "user list" window, you'll exit the client, and no
more messages of any sort will be sent to you.