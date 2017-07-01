# ShoppingCart-Desktop-Java
A shopping cart implementing the **HEOM sorting algorithm** for matching items.

<br>

**For installation, follow the steps below**

<br><br>
# Setting up the database
* **Ensure you have at least version 5.6.17 of sql on your system**
* Got to your taskbar, and right click either your wamp or xammp icon
* Select mysql and click MySql console
* A black screen will open asking for your mysql details
* After logging in, ececute the code below on **mysql console**

```mysql 
CREATE USER 'root'@'localhost' IDENTIFIED BY '';

GRANT ALL PRIVILEGES ON * . * TO 'root'@'localhost';
```


* Create a new db called **sarpestein_db**
* Open the just created database, and click the import button displayed on the page
* Upload the ```database.sql``` file **in the root of this project folder** into your mysql database.
* Select ```sql``` as the format
* Click the **Go** button to create all of the database
* Change the username and password in the HeaderFile.jsp and other file

<br>

# Setting up the project
* Open this folder with net beans
* Run the project **[Click the green play button]**


<br><br>
# Contribution
In caseof any challenge during the running of this project, please mail me at nkemdilimtolimtochukwu@gmail.com
<br><br>
In order to fix any bug or make contribution, simply send a pull request.
<br><br>
Feel free to message me for any issues  [@christ_dev](https://twitter.com/christs_dev)
