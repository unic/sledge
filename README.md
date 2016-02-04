Sledge - "One-Click Rollout"
============================

Sledge is a Sling Application Manager which provides a simple user interface to easily deploy, uninstall, monitor and configure your applications in one step. 
Furthermore it provides a command line interface, best suited for remote management of your application in large enterprises. This allows for a nice integration into existing Continuous Delivery environments.


Installation
------------

Install and run Sling 8

Configure the _sledgeUser_ in the Service User Mapper Service: com.unic.sledge.core=sledgeUser

Install core, connectors and webapp packages

Configure the read/write permissions for the _sledgeUser_ for /etc/sledge

    curl -u admin:admin -FprincipalId=sledgeUser -Fprivilege@jcr:all=granted http://localhost:8080/etc/sledge.modifyAce.html