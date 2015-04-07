#Orbit
READ THIS DOCUMENT! 
Feel free to edit this with additional info or guidelines.

##How to use GIT with Eclipse
1. In the project manager, right-click, Import - Git - Projects from Git. Click next. Clone URI - Protocol - HTTPS - Copy paste the repo URL. Next next etc. 
2. If Git is not an option - go to the Eclipse Marketplace (Menubar under Help) and then search for EGit and install it and restart eclipse
3. Once you have it in your eclipse workspace, you can right click the project and choose Show In -> Git Repositories. That will open up the Repos viewer and from that you can branch and commit etc.

##Guidelines for code you commit.
1. Comment your fucking code. At least one comment at the top of each block of code or function that says what it does (besides the obvious ones). I think eventually we are going to need to make real documentation, for which we can use javadocs so it'll be helpful to at least have basic descriptions for now.
2. Use System.out.println("Something") for anything major like loading or launching something. Don't print stuff in loops that make the console really messy and confusing. You can do it for your own tests but remove it before pushing.
3. Before you work on code, check the GroupMe to see if anyone else is working on that piece of code. Either wait until they finish to start working or deal with merges together.

Feel free to work on whatever code you see fit. Here is the plan I have in mind for the first steps, but don't feel like you have to follow it:

1. Basic client (Orbit) and server classes that interact locally. 
2. Basic database class to interact with a database. As we add tables to the database we will need everyone to get the new version of the database. So we should generate the database in script (so anyone can run the script to create the database) and then add in some default testing users/data. There is also someway to send entire databases if we think that's easier.
3. Set up the client GUIs
4. Test being able to authenticate user by entering info in GUI, writing to server, reading from client, accessing database value, writing to client, reading from server, and displaying on GUI.

