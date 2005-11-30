ENABLING ROSTER

By default, Roster is included in the release but hidden from the list of available tools in the Worksite Setup. Roster will be available to adminsitrators using the "Sites" adminsitration interface as one of the tools Roster (sakai.site.roster). You have one of two choices in terms of enabling Roster.

If you want to let a few selected users use Roster to test it out, you can have the adminstrator selectively add it by hand to the sites for those users who you want to use Roster.

If you want to make it so that any user can add Roster to their site using WorkSite Setup, edit webapps/sakai-roster-tool/tools/sakai.site.roster.xml in a deployed instance, or roster/roster-app/src/webapp/tools/sakai.site.roster.xml in a source tree. Uncomment the <category ...> entries in the <tool> section.


KNOWN ISSUES

There is not standard way to provision the pictures in the Roster tool in this release. If you would like information on how to provision the pictures, contact Lance Speelmon (lance@indiana.edu).