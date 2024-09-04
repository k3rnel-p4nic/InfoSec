InfoSec Project
===============
This was the final project of the university course "Information Security".
This project aims to demonstrate and address known vulnerabilities in a simulated vulnerable webmail system. The primary objectives were to:
1. Showcase common security vulnerabilities
2. Implement fixes to close these vulnerabilities

## Purpose
The purpose of this project is educational, designed to:
- Provide hands-on experience in identifying vulnerabilities
- Demonstrate the process of securing a web application

## Setup
The project is setup to require Eclipse as an IDE: the full guide is in ```WebApp setup.txt```.
After that, the following two options are provided:
1. The project was born with MSSQL in mind, so a helpful guide is provided: ```SQL Server setup.txt```.
2. Otherwise, if you choose (like me) to use Postgres, you'll need to run ```psql -U <user> < sqlScript.txt && psql -U <user> < insertion.sql```. The Postgres Java connector is also provided in the root of the project.

I also worked on IntelliJ IDEA actually, by importing it as an Eclipse project: it wasn't that difficult to setup.

## Attacks
The attacks I found out are in the ```attacks``` sub-directory, as text files.

## License
This educational project is released under 2-clause BSD license.
