Bruno Silva up201503818
Fábio COsta up201604607

Compile the programs:
    -Be in the same directory as the "ds" folder
    -run: javac ds/trabalho/parte#/Peer.java
        Note: # is replaced by 1, 2 or 3 depending on the program to compile

Run parte1:
    java ds.trabalho.parte1.Peer [IP] [IP]
        Note: The second [IP] is the next one to send the token
Commands:
    "start" - The token start do circulate
    "lock" -  Lock the token
    "unlock" - Unlock the token 

Run parte2:
    java ds.trabalho.parte2.Peer [IP]
Commands:
    "register [IP]" - regista o IP na sua lista de ips
    "push [IP]" - IP update the dictionary
    "pull [IP]" - The localhost updates the dictionary
    "pushpull [IP]" - Execute the push and pull commands
    "see" - Get the content of the dictionary

Run parte3:
    java ds.trabalho.parte3.Peer [IP] [IP] [IP] [IP]
    Note: The first argument is the [IP] of the localhost machine
Commands:
    Type the messages that want to send to the others