# Roleplay
## Description:
Roleplay (aka "user alias") is a tool and service for Sakai 2.6.0 and later which allows users to be known as a name other than their default Sakai name within a site. It was originally developed at UCT for an international trade bargaining simulation game in which students were known by country names representing their teams (e.g. "Brazil 2A") rather than realm names.
## Where to go?
* Source: https://github.com/cilt-uct/roleplay/
* JIRA (bug reports and feature requests): http://jira.sakaiproject.org/browse/RPLAY
* Community wiki: https://confluence.sakaiproject.org/display/RPLAY/Home
## Using the tool
1. Add the useralias project to your source tree (2.6.0, 2-6-x or later). If you use an .externals file to manage your source tree, add a line such as this:
`roleplay        https://github.com/cilt-uct/roleplay/branches/sakai-11.x`
2. To "stealth" the tool so that it does not show up in the regular list of available tools for a site, add it to your hiddenTools list in sakai.properties, e.g.
hiddenTools@org.sakaiproject.tool.api.ActiveToolManager=sakai.useralias
3. Add the tool to the site(s) in which you wish to use it through Site Info / Edit Tools, or the Admin Sites editor if the tool is stealthed.
4. Add the site reference for each site for which you wish to enable username aliasing into the USERALIAS_SITE table, e.g.:
insert into USERALIAS_SITE (siteId) VALUES ('/site/22b54b43-a00d-46c0-8087-d9535b9059e4');
5. Use the tool to update the alias names of users in the site. Other tools and services which use the full name (display name) of these users will then show the aliased name rather than their "real" name. Typically these are the collaboration and communication tools such as Chat, Forums, Announcements. However, tools which use the "Lastname, Firstname" format (e.g. Gradebook, Site Info) will not show aliased names. These are typically administrative tools
## sakai.properties
`userAlias.xlxs=true`
