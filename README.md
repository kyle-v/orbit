#Orbit
READ THIS DOCUMENT! 
The way of importing the project into Eclipse has changed.


##How to Import the  into Eclipse
1. Go to the Eclipse Marketplace (Menubar under Help) and then search for EGit and install it and restart eclipse
2. In the menubar choose Window->Show View ->Other -> Git -> Git Repositories. This will open a eclipse tab that shows repos in your work.
3. In the menubar under Help -> Install new software
4. Enter in the "Work with" field:  http://dist.springsource.com/release/TOOLS/gradle
5. Select All, click next, next, etc finish.
6. In the project manager, right-click, Import - Gradle. Click next. Select the "orbit" folder in the repository (not the repository itself) and then click **Build Model**. Select the all the projects.  Next next etc. 
7. Now there are two projects, core and desktop. Desktop is everything desktop specific, so all the Swing and GUI stuff will be here. Everything else is in orbit-core. 
8. To test it works, right click on the orbit-desktop project and choose Run as -> Java Application and choose DesktopLauncher (because it has the main). It should pop up a window and not give any exceptions or anything weird.

##How to use GIT with Eclipse


##Guidelines for code you commit.
1. Comment your fucking code. At least one comment at the top of each block of code or function that says what it does (besides the obvious ones). I think eventually we are going to need to make real documentation, for which we can use javadocs so it'll be helpful to at least have basic descriptions for now.
2. Use System.out.println("Something") for anything major like loading or launching something. Don't print stuff in loops that make the console really messy and confusing. You can do it for your own tests but remove it before pushing.
3. Before you work on code, check the GroupMe to see if anyone else is working on that piece of code. Either wait until they finish to start working or deal with merges together.

Feel free to work on whatever code you see fit. Here is the plan I have in mind for the first steps, but don't feel like you have to follow it:

1. Basic client (Orbit) and server classes that interact locally. 
2. Basic database class to interact with a database. As we add tables to the database we will need everyone to get the new version of the database. So we should generate the database in script (so anyone can run the script to create the database) and then add in some default testing users/data. There is also someway to send entire databases if we think that's easier.
3. Set up the client GUIs
4. Test being able to authenticate user by entering info in GUI, writing to server, reading from client, accessing database value, writing to client, reading from server, and displaying on GUI.

