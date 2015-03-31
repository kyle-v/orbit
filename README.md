#Orbit

We will need some sort of system of branches so that we dont fuck up each others' code and have merge conflicts.

Here is the plan I have in mind for the first steps:

1. Basic client (Orbit) and server classes that interact locally. 
2. Basic database class to interact with a database. As we add tables to the database we will need everyone to get the new version of the database. So we should generate the database in script (so anyone can run the script to create the database) and then add in some default testing users/data. There is also someway to send entire databases if we think that's easier.
3. Set up the client GUIs
4. Test being able to authenticate user by entering info in GUI, writing to server, reading from client, accessing database value, writing to client, reading from server, and displaying on GUI.

