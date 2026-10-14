## CMDBuild 4.3 (released 2026-10-14)

### REQUIREMENTS:
* Require Java 25+
* Update Spring version to 7.0

### NEW FEATURES:
* Added AI integration, with AI provider setup in the administration module
* Added AI chatbot widget
* Added AI-powered global search in the homepage
* Added homepage structure, routing and settings for administration module
* Added homepage routing, settings and new components for management module
* Added new dashboard components as list, tile group, calendar and pareto chart
* Added support for global dashboard parameters and parameter-based filters in the management module
* Added dashboard clone functionality
* Introduced a new visualization mode for the relationship graph, based on a dependency tree model
* Added activity log for classes or processes, including CRUD event and listing tracking
* Session: added configuration page for operator info in session
* Added the possibility to show only the icon in a lookup or lookup array attribute, hiding its description, if an icon is set
* Automatically drop a marker on the map after a search is performed
* Added Print in the Details section of a card
* Allow customization of menu icon
* Added "Select all" button on attachments tab
* Added "Last update" column in classes

### IMPROVEMENTS:
* Allow selection of tenant attribute on view from join
* Allow selection of subtype attribute on view from join
* Updated MSAL4J, Azure Identity and Microsoft Graph SDK to the latest supported versions for improved security, stability and compatibility
* Updated libraries
* Added clear all button to delete all notifications
* Modified the operations performed during the advancement of a process
* Added possibility to view notes on grid
* Added possibility to set notes on advanced filter
* Added new configuration to hide refresh functionality on custom form widgets
* Updated logo in HDA skin
* Task manager - Added cron information in grid
* Set manage email widget behaviour on the first step of a process
* Plugin - Disabled apply patches button when patching is complete
* Plugin - Sort overview by description
* The status filter of processes grid is now saved in user preferences
* Added Skip new data checkbox in import template with import mode equal to merge
* Show attachment preview also on calendar events and emails
* Added author column in Attachments tab
* Font type in lookup values: it is now possible to write, copy and paste in the icon field
* Added check on file extension when dragging and dropping a file in system config logo field
* Added possibility to select all records of a column in clone card with relations
* Filtered the message field on process parameters by removing system attributes
* Attribute table: added alternating row background for better readability
* Restricted shape file selection to .zip files only and added an empty-state text placeholder
* Improved Link Cards widget behavior by automatically deselecting cards when the current filter no longer includes them
* Domains inline now respect both origin-editable and destination-editable config, consistent with the Relations tab
* Join views now support leaf class selection on superclasses
* Improved performance on history attributes changes
* Changed style of patch and database configuration page
* Bus Messages - Removed retry action
* Modified and standardized the style of the elements
* Added rounded borders to message boxes and detail windows
* Refactored style of dashboard

### BUGFIX:
* Date values are now set correctly on column, advanced and CQL filters
* Interface now does not break if you use a custom form of type grid
* Enabled report refresh button when closing parameter dialog
* Removed extra whitespace at the bottom of inline linkCard widgets
* Fixed error when creating filters on attachments metadata
* Fixed error when opening bus messages records
* Reports can be opened even if they contain square brackets in the description
* Fixed go to origin card widget in Processes and DMS Models
* Fixed menu closing after applying plugin patches
* Fixed view style for address field in Email account details tab
* Fixed message box on top inside processes
* Attribute binding now works correctly in the widget viewrule
* Disabled destination field of foreign key attributes in edit mode for simple classes, while keeping it active in create mode
* Fixed cards loading failure occurring when the "Keep Filter on Card Update" setting was disabled
* Fixed error when typing double quotes on class search
* Restored edit of task import IFC
* Fixed favourites menu collapse/expand animation
* Fixed sorting of menus inside clone button
* Plugin - Fixed the expiration warning message when the plugin has expired
* The plugin list is now always returned even if a plugin is in error
* Global search on special characters in administration now works correctly
* Restored lookups tabs in scheduler setting in administration
* Fixed base data not shown in attributes combobox of scheduler advanced filter
* Fixed clone of import/export templates
* Changed the icon to open the BIM Viewer
* Now only active BIM projects are returned in data management
* The relationship value is now always displayed correctly even if the domain is 1:1
* The correct domains are now displayed when a subclass is selected in the domains section of join views
* The image on the SSO button is now displayed correctly
* The email template editor now correctly switches between html or plain text editors based on its value
* Now emails with special characters are encoded correctly
* Account data is now correctly returned if using a template that has a default account
* Data is now imported correctly if the primary key of the import template is an IP address or inet
* Disabled layers tab in map view when no visible layers are configured for the class
* Fixed bugs on the translation icon and localization tab
* Fixed bugs in the administration domain section when in edit mode
* Fixed errors on domain attributes with domain key enabled
* XLSX import now works correctly even if the file contains blank lines
* Numbers are now validated using the correct decimal separator
* Routing now works correctly when cancelling the creation of a new custom page
* Improved email fields validation
* Fixed issue where attributes with ShowIf containing blank lines were not displayed in the grid
* Correct background of events on calendar widget
* It is now possible to successfully delete a class in administration
* Fixed wrong font when editing grants
* Fixed tooltip display issues for FieldContainer LabelToolIconQtip
* Fixed text overlay issue when editing email metadata and import/export attributes
* Fixed picker opening when editing a card
* Fixed close icon color on dark background in administration module
* Disabled double-click on dashboard layout tiles list
* Editing lookups in expanded rows now always works
* Some configurations are now correctly cleared on recreate without backup restore
* Fixed upload CLI command
* Correct translations

## CMDBuild 4.2 (released 2026-03-27)

### NEW FEATURES:
* Superclasses - Set add card button disabled if no subclass can be added
* Introduced new webservice rest v4
* GIS - Added the possibility to set the style of polygon type elements
* Implemented automatic webservice rest documentation with open API standard
* Added possibility to set visualization rules for widgets
* Plugin - Added page to view and select all plugins
* Plugin - Added possibility to add custom components
* Plugin - Manage localizations for custom components
* Added a new configuration to allow automatic anonymization of text and strings attributes
* Added a new configuration that allows string attributes to be automatically trimmed
* Added a new dashboard on administration home to display active plugin information
* Added notification polling if websocket is disabled
* Added a global configuration to disable notification sound
* Added a configuration in user preferences to change the notification volume
* Move ifc and dwg converters on external services

### IMPROVEMENTS:
* Improve UI accessibility
* Improvements on relation graph
* Improve performances on load navigation trees on administration
* GIS - Added new coordinate projection when get coordinates on map
* Plugin - Added notification for plugin expiration
* Plugin - Added expiration date metadata to plugins endpoint
* Plugin - Hide custompage section or plugin configs section when there are patches to apply
* Added possibility to translate the value of the "Go to button label" field in mobile and in-app notifications
* Added realtime cron expression validation
* Added possibility to map errors and translate them
* Added possibility to update tenant massively
* Added a new button to download chart data as CSV
* Added copy to clipboard button and fixed layout on notifier errors
* Added the possibility to bulk update the tenant value
* Added notes field in bulk update if it is an inline field
* Added possibility to add odt and rtf reports to menu items
* Moved print schema button to the model statistics graph
* Update layout of mobile configurations
* Removed redundant name information in the thematism popup
* Removed null parameters on requests with filters
* Update Xeokit version

### BUGFIX:
* Moved graalJSS library to Rhino
* Fixed redirect to card when classes are ordered by non-unique attribute
* Added possibility to create a filter based on domains defined on Class
* Added check to allow password reset only if there is a default account and the email queue is active
* Restored tab domain functionality in the processes section in administration
* Restored functionality of ifc and database templates
* Grid search on domains in the administration now correctly search data in all fields
* Correctly handled permissions for inline relations
* Schedules on a card are now always updated correctly
* Attribute selection on bulk edit now works correctly
* Now scheduler is visible also when multitenant is active
* Plugin - Update all administration data and plugin data after patching
* Restore row editing in master detail tab
* Fixed cross-site scripting vulnerability in login page when entering incorrect data too many times
* Always load system monitor chart on home page
* Now if you delete the last card of a class the interface does not break
* Now the foreign key attribute is correctly valued even if you select the record from the popup opened via the magnifying glass
* Processes are now correctly advanced even when they have a lookup array field
* Cards now save correctly even when they have a disabled lookup array field and a data field with schedules
* Reference values are now updated correctly when they have ecql filters
* Interface now does not break if you drag and drop users when there are many of them
* Display generic localized message on generic error message when details are missing
* Show tenants after enabling multitenant
* Display error message if a folder has empty description when manage menu
* Fixed error when go to bus messages section in administration
* Bulk delete can now be performed if a search filter is active
* The interface does not break when you are on the plugin's custom page and refresh the page
* Now is possible to delete filter on an import template
* Restored the error state on the widget button
* It is now correctly possible to add relationships in the case of a 1:N domain from a class to itself and a reference attribute created on a subclass
* Subgrid notes now display correctly if there are notes on the main tab
* Disabled checked only button on linkcard widgets if noSelect is true
* Disabled save button if search filter is not configured correctly
* Deleting the entire chart configuration row in the dashboards section in administration works correctly
* Bus descriptor parameters values can now be encrypted after changes
* Now if process view has a filter it always shows the filtered data
* Standardized the state of the of translation popup in administration based on the form mode
* Standardized the length of the description field when adding or editing a user account
* It is now possible to save a card without changing a password field that is visible to no one
* Added scroll on login page
* Fixed display bug on bar chart origin
* Correct layout of calendar events on list week mode
* Fixed layout of configuration page buttons
* Fixed layout of checkbox fields and columns filter on grids in administration
* Fixed layout of report parameter value on template email
* Fixed translations of geo attributes title fieldset in GIS menu
* Correct translations


## CMDBuild 4.1 (released 2025-09-26)

### NEW FEATURES:
* GIS - Add possibility to keep position and zoom when moving between classes
* GIS - Add possibility to get coordinates by clicking on the map
* Handle error translation

### IMPROVEMENTS:
* Security improvements on REST webservices
* GIS - Added possibility to define a thematism as private
* GIS - Added filter for grid columns on list section
* Improvements on the plugin page
* Improvements on kubernetes cluster
* Improvements on import GIS template
* Improvements grids loading time
* You can now manage the selected card in the navigation tree via the info button in the title
* Now it is possible to create a relation between same cards with different attribute
* Now you can download all attachments when there are a very large number of them
* Add possibility to select tenant attribute on grid for superclasses
* Added button on login page to view application information
* Improved management of the selection of languages ​​enabled in administration
* Save import statistics

### BUGFIX:
* Restored SAML SSO
* Restore change password on login page
* Restore correct creation of geoattributes
* The navigation tree is now updated after cloning a card
* Now you can select the tenant attribute for the master class in join views
* The import template now works correctly even when using idtenant as the import key
* All domains are now displayed correctly in the navigation trees section on administration
* Now associated class and card or parent value are mandatory in a BIM project
* Bulk delete functionality restored
* You can now view old filters that have hidden attributes
* Disable sorting on grid columns for formula attributes
* The interface no longer breaks when sorting data on the main grid when a subgrid is also open or sometimes when navigating the application
* Set correct end date of events on widget calendar
* Recalculating the date on a scheduler field now generates the correct date
* Card management buttons now work even when accessing cards via the navigation tree
* Change password on login page is now hidden when is active only sso
* Fixed error when accessing the bus messages page in a cluster environment
* Restored reactivation of GIS GeoServer shape from map levels menu
* Notes widget now does not retain values ​​of previously opened processes
* Save and close button on popup now open correctly card after creation
* Restored the possibility to edit other permission tab on group permission module on administration
* Now it is possible to save changes on password policy on administration module
* Hide type export combo if type filter is domain on export template
* File names with special characters are now also handled when uploading files to alfresco
* Filter from selection on relations tab now show correctly records when add or remove data
* Now the reference value is always displayed correctly even if set by autovalue
* The dashboard button used to show/hide parameters now changes correctly icon when clicked
* Save button on the CreateModifyCard widget now works even if the field it references is in read mode
* Restore dehighlight on charts
* Restore visibility of edit icon on section attribute for import/export template
* Fixed ifc import excluding disabled models
* Restore visibility of CMDBuild logo and favourite icon in filters menu on Firefox
* Correct visibility of icons used on lookups
* Restored functionality of displaying the location of the geographic attribute
* Restore field for modify master detail label on domain section on administration
* Fixed ldap login serialization
* Fixed string template in form widget of closed process
* Attribute localizations on administration module are now visible even in read mode
* Restored the functionality of deactivating users from the administration module
* Exclude inactive roles when configure an user
* Auto generated email notification template is no longer mandatory on email tasks
* Enable multigroup by default config now working properly
* Fixed filter popup on relation that sometimes was invisible
* Now it is possible to create correctly relations by two different inline relations
* Now the role in the header is translated if the login language and the user language are the same
* Now the main data of the card is visible correctly even after opening a card in the details tab and returning to the main card
* The interface no longer breaks when loading data into dashboards and the call fails
* Fixed bugs on filters in the attachment tab
* Fixed record selection in attachment grid
* Fixed email generation on processes email widget
* Fixed wrong metadata on type change when create an attribute
* Fixed the order of buttons in some sections of the administration
* Text area resizing now works correctly
* Fixed SQL view representation
* Correct translations


## CMDBuild 4.0 (released 2025-03-28)

### NEW FEATURES:
* UI restyling
* Added new plugin manager
* Added compatibility to Kubernetes
* GIS - Added satellite view
* GIS - Allow clustering on point type geographic attributes
* GIS - Added possibility to set the enabled/disabled value for layers on map opening
* GIS - Load geoattributes only for enabled layers
* GIS - Added layer icon in layers menu on map
* DMS - Added application authentication for SharePoint
* DMS - Changed Alfresco protocol connection from CMIS to REST API
* DMS - Added configuration on administration to resize image before uploading it on dms
* Import template - Added the ability to filter rows based on one or more columns
* Added search on navigation on administration module
* Added dynamic configs on waterWAY gates
* Added the possibility to disable relations edit from relations tab with a configuration in domain definition
* Now relations managed with reference are no longer editable from relations tab
* Allowed possibility to handle widgets on closed process instances

### IMPROVEMENTS:
* Upgrade Sencha ExtJS to 7.0.0 gpl
* Update Spring version to 6.0
* Updated version of frontend libraries
* Updated IFC to XKT converter
* Added custom validators on bulk edit
* Added configuration to limit preview generation in attachments tab
* Added possibility to choose behaviour on target delete on foreignkey attributes
* Added message that blocks the operation when deleting a card that has a foreign key in restricted mode
* Added linkCard widget in form widgets available on administration
* Improved the load of bim projects on management
* Improved retry failure policies on websocket connection
* Don't show ifc import templates when ifc functionality is disabled
* Added last checkin date on Xeokit projects
* The presence of the flag for the addition of translations in the fields in administration has been standardized
* Handle rejection of afterTargetSave or beforeTargetSave on widget cards
* Improve performances on load geoserver layers
* Change default BIM viewer to Xeokit
* Removed unnecessary parameters on view from join request
* Removed tooltip on collapse header on attachments

### REMOVED:
* BIMServer integration
* SOAP webservices
* SOAP protocol management

### BUGFIX:
* Now search on bus descriptor and etl gates works correctly
* Fixed history print report
* Remove formula fields from thematism
* Show correct values on thematism legend even when it includes whitespaces
* Now advanced filter on superclasses works correctly also on map
* Set input parameters fields as mandatory on filters
* Fixed show all labels for superclasses on map
* Set correct filter for tenant attribute on row filter permissions
* Load data also when click cancel on filter popup with input parameters
* IP attributes and lookup arrays are now rendered correctly in grid columns for inline relations
* Correct the export of a view when it has lookuparray attribute
* Removed possibility to select superclasses on import template
* Data format fieldset is now displayed correctly in import/export template
* Fixed error when cloning or creating an import/export template
* Client variables on templates email now works correctly
* Fixed the functionality of abort process with right click on row grid
* Clear email fields from html special characters
* Password with special character is displayed correctly in view mode
* The ~ character also displays correctly when you change the browser zoom
* Other permissions on administration are now correctly editable
* Correct icons and functionalities on extended data relations
* View correct data on attribute column on import gis template
* See correctly status of action columns on custom form widget
* Show correctly records on relations grid when have same domain and different destination class
* Custom routes now working also for process instance view in row
* Hide widgets implemented only for mobile version
* Sorting feature now works correctly
* Now opening tasks always works even when there are many tasks
* Correct checks on attachments with same extension
* Now is possible to download correct attachment version also on DMS PostgreSQL
* Handle superclass on view from join with filters
* Restore filters on email template attachment
* The action and actionLabel are now present even if created from the template
* Fixed error when using an after edit and execute form trigger where the record is saved
* In the default filters tab on the Groups and permissions section the default filters set remain even if not modified


## CMDBuild 3.4.4 (released 2024-08-02)

### NEW FEATURES:
* Add foreign key management in classes and processes
* Added the possibility to create and use standard filters when adding or editing relations
* Manage grants on waterWAY gates
* Added error handling in waterWAY plugins
* Added only selected button in some grids

### IMPROVEMENTS:
* Added possibility to use parameters on cql filter on domains
* Added possibility to define a export template on superclass
* Added available attributes on delete script on waterWAY imports
* Improved time to print csv on class with tenants and large amount of data
* Now formula attributes are correctly visible also on superclasses
* Improve fulltext search when have lookup array attributes
* Added HTML code handling in mobile push notifications
* Added ability to filter data on timestamp field on bus messages page
* User admin now can change users password on administration
* Change default value on export field in export data popup
* Format grid counter number with thousand separator
* Added api to update record values on form trigger
* Added configuration for cluster failure timeout
* Disabled card selection on administration when loading a bim project that has a parent
* Bim model tree root renamed with project description
* Added properties to set popup size on context menu component
* Hide calculated attributes on scheduler filter
* Improved layout of import/export template
* Added loading mask for email tab
* Updated libraries

### BUGFIX:
* Added scrollbar in login module on administration authentication section
* Fixed context menu functionalities that appears on right click menu
* Fixed behaviors of task manager in opening tasks
* Show always at least one language on login page
* Added the possibility to set a decimal attribute as key on import template
* Set description translation for trigger fieldset in scheduler date popup
* Select correctly element on map navigation tree when have multilevel active
* PDF/CSV printing with char, file or formula attributes now works correctly
* Do not consider the label value when it is hidden in link attributes
* Restore query filter on scheduler in administration
* Fixed multilevel lookup in edit mode
* Edit relation now work correctly when edit reference relation
* Value of lookup array is view correctly when field have a CQL filter and edit data
* Now gis navigation tree item is removed when delete card
* Now attachment load works also on inline attachment widget on processes
* Center always position of spinner icon on BIM viewer Xeokit
* Fixed display of processes on inline relations
* Fixed cross site scripting vulnerability on error popup
* The grid now doesn't break if you update its data via the context menu
* Revised the layout of jobrun errors
* The inline widget is now properly hidden if it is disabled
* Fixed error on add relations when domains have same code
* Interface now doesn't break when viewing or editing import/export template that has a field with description equals to Status
* Hide FlowStatus lookup in search bar on lookup types page in administration
* Fixed classes endpoint when getting processes data
* Fixed key attribute setting on import gate mapping template
* Fixed traduction of actioncolumn tooltips
* Re-enabled the sorting of columns in the scheduler
* Now it is possibile to see all notifications on popup notification
* Correct loading of scheduler events
* Fixed problems with fields features on DMS models
* Fixed sharepoint DMS error on CMDBuild startup
* Improved loading of relations
* Now is possible to manage correctly all type of relations
* Fixed error that occurs when saving domain data
* Correct closure of filter menu after create attachment filter
* Now in the notification popup you will see all existing notifications
* Column filter is now applied correctly even after applying and removing a class filter
* The error on tasks type stats widget on admin home page is now handled

## CMDBuild 3.4.3 (released 2023-12-15)

### NEW FEATURES:
* Added compatibility with PostgreSQL 15
* Allow auto value on card for multiple attributes
* Scheduler management with multitenant
* Added possibility to import/export lookups with session translation
* Added right click menu on join view
* Implemented handling of lookup array and file attributes in email template
* Added persistence on database for mobile app notification messages
* Added possibility to restore database with single job
* Restored start and stop of system services from administration module
* Added possibility to add all classes attributes at once on import/export template
* Added possibility to see multiple models on same project on BIM viewer Xeokit
* Added possibility to configure the selected only button as default in linkCards widget
* Allow group clonation
* Added Thai language

### IMPROVEMENTS:
* Added possibility to see history for Notes on history tab
* Added possibility to manage case sensitivity for reference attributes in the import template
* Added possibility to select multiple elements on BIM viewer Xeokit
* Added description of tenant on print class cards
* Added a filter to show only choosable domains on dropdown selection for a reference field creation
* Added loading mask in master detail tab
* Added loading mask in task run messagges page
* Show edit button for row on linkCard widget only if it refers to a class
* Improve the error show on user on forgot password procedure
* Disabled the ability to add relations if one already exists when the user is on the relation side with one cardinality
* Hide "show in main form" property in attributes on administration when domain is N:N
* Improved handling of subreports
* Improved display of html attribute value when in view mode and its value is too long
* Improve performances for chat notification
* Improve performances on csv generation
* Updated libraries

### BUGFIX:
* Clear the user search in the chat when you perform a new search
* Correct translations
* Removed the delete button on the user tab in administration
* Corrected the management of flags for translation in administration
* Bulk delete is now working correctly when delete all elements
* Now sort configurations on views from join are always present
* Correct view from join layout when have multiple groups
* Import/Export template attributes are now correctly associated with values on error report
* Address search on map now work correctly
* Now interface does not break when creating reference attribute with 1:1 domain
* Card relations are now loaded correctly when redirecting from an inline relationship of a subclass
* Correct errors on login when group is not selected
* Interface doesn't break if modify master class alias on view from join
* Fixed oauth type login
* Source and destination filter configuration now display regardless of the domain used
* Now the title of the view is correctly translated
* Fixed import/export template clone
* Enabled scrolling on the class permissions grid
* Search box is visible for all users in views from join
* Navigation tree now doesn't break if it contains a deleted domain or class
* Correct visualization of months of inactivity property on administration after page reload
* Correct permissions on inline relations buttons
* Fixed preview and attachment download if its name contains special characters
* Fixed lookup array attribute on processes
* Correct process layout activity description
* Fixed layout of simple classes when no attribute has True value in "show in grid"
* Hide favourites root folder in menu navigation on page refresh
* Correct active session button visualization on administration
* Correct configuration for disable auto login SSO
* Now the email generated by the manageEmail widget is sent correctly
* The manageEmail widget now works correctly even with file attributes
* Abort email now work correctly even if the email queque is active
* LinkCard widgets now work correctly when opening a new process
* View from join with lookup array attribute now work correctly
* Fixed display of permission tab in administration for read only admin group
* All duplicated rows are now displayed correctly in the extended data view of relations
* Fixed cross site scripting vulnerability on notification error popup
* Fixed data update for relations between relations tab and master detail tab
* Fixed errors on database template
* Scheduler trigger is now visible also in datetime fields
* Clear rows selection after bulk edit
* Now also events referred to a class open correctly on calendar
* Now interface doesn't break if domain filter is used but is empty
* See always buttons for edit group when add new join view
* Geometries are correctly assigned to geoattributes GIS during import DWG
* Data on tab permissions on administration are always displayed correctly
* Description translations are now correctly editable in views
* Hide column filter on subtype column when filtering is not allowed on the grid
* Fixed display of categories on scheduler rule when there are more than twenty-five
* Change cursor on task grid status column in administration
* Added schedule when changing date field if it doesn't exist and a valid rule exists
* Now calendar trigger rule should have at least one mandatory template
* Cql filter on domain master detail now works if logged in with a role other than SuperUser
* Now is possible to manage cql filters for lookup attributes on a domain
* Now data in help tool is correctly visible on boolean fields
* Import/Export template for domains now work correctly even when in tenant mode
* Fixed all admin roles for groups to show admin sections correctly
* String template now works when retrieving data from lookup array attribute
* Fixed sorting for formula attributes
* Filter classes to avoid assigning a superclass to a BIM project
* The mandatory nature of file fields is managed
* Emails are generated in email template even if a file type attribute is present
* The date field is now evaluated correctly in the email template
* Correct validation on email service task
* Fixed history for file attributes
* Removed all filters of the grid when you redirect to an element of the same displayed class and it is not present in the filtered data
* Correct view of login page when use header authentication
* Now if you select a subclass for selected domain in a view from join it is correctly saved
* Fixed opening detail window in workflow task with double click
* The CSV encoding value in the preferences popup is now displayed correctly

## CMDBuild 3.4.2 (released 2023-07-05)

### NEW FEATURES:
* Added webhooks management on classes and processes
* Added favourites items section on navigation menu
* Added possibility to search items in navigation menu
* Added configuration to disable search field in grids
* Added configuration to exclude attributes from grid
* Added column filter on subtype attribute on superclasses
* Added new pages to configure mobile configs in administration and management modules
* Added push notification for mobile apps
* Added mobile notification templates management
* Manage mobile notifications in scheduler on administration
* Manage add relations filter in domains management
* Added extended data in attachments tab
* Handle custom widgets statics actions and inline features on DMS models
* Allow configuration of limited administrator permissions
* Manage new Service bus task type
* Handle auto generated email in "Read emails" task
* Added possibility to handle code or description for tenant in import/export templates
* Allow attachments upload on email templates
* Added new page for administrators to manage emails in error state
* Added possibility to impersonate another email user on Microsoft Graph
* Full handle email reply on Microsoft Graph
* Added possibility to use encrypted data on waterWAY
* Added configuration to disable websockets
* Added web service to extrapolate the grants of each group of a given resource
* Added configuration to disable deprecated Rest v2 and SOAP webservices
* Added Romanian language

### IMPROVEMENTS:
* Added detailed history based only on single field changes
* Added button to export history data on CSV
* Removed checkbox change on label click
* Changed style for reference domain attributes
* Improved email list load time
* Improved Bus messagges list load time
* Moved language selection in login page to the top bar
* Updated layout of attachments warning on card attachment tab
* Update Xeokit version and IFC to XKT converter
* Improved administration module home dashboard
* Added possibility to assign permissions directly from resources pages
* Improved schedule rule definition form
* Removed disabled classes from Class/Domain field in import/export template management page
* On Group creation form set "Visible tabs" property to true to all tabs
* Added refresh button on Bus messages and Task run messages pages
* Seted error color for failed statistics on Bus messages and Task run messages pages
* Added reset trigger on attribute group cell editor on view from join management
* Added configurable cache size on waterWAY
* Added possibility to send multiple attachments with the same name to waterWAY gates
* Improved waterWAY import performance
* Moved system template notification to yaml
* Made improvements on PostgreSQL DMS service
* Added possibility to delete realtime jobs
* Set default for CQL filter in restricted mode
* Manage attachments on task email notification
* Added possibility to attach a sum of files greater than 3MB on Microsoft Graph
* Removed vertical solution static name from application info popup
* Added retention for etlmessage table
* Added possibility to disable email template for any class
* Added session configuration on schema waterWAY
* Return always yaml on etl configs
* Handle creation of missing lookup from description
* Remove log4j references in pom
* Updated restassureed libraty to 5.3.0

### BUGFIX:
* Fixed navigation tree refresh on changes
* Correct translations
* Corrected display of the tree in the enabled classes tab in the administration domain section
* Now related card button on views on administration work
* Fixed error when sorting users by "Language" or "Multigroup" in the administration module
* Fixed text display on task run messages
* Hide system attributes on join views layout definition
* Correct query filter on tenant menu selection and import/export template
* Labels on header UI are now translated correctly
* Now tab relations show relations when open item from right click contestual menu
* Fixed system report
* Fixed loading mask when upload second bim project on administration in same session
* Correct layout tenant field when create filter
* Denied possibility to create an attribute on superclass if already exists on some subclass
* Fixed relations sorting by begin date
* Fixed users sorting on administration by default group
* Set the email as read when you reply to it
* Fixed error when editing a relation on certain domains
* Load all lookup values to grid filter
* Fixed displaying inline relations and extended data on relations when domains have attributes in hidden mode
* Denied possibility to create an attribute on superclass if already exists on some subclass
* Handle blank search
* Required option on widget now working
* Disabled load of chat users if chat is disabled
* Remove service users from peers chat
* Correct view of inline widget on processes
* Added Java 8 date/time compatibility
* Set specific index as unique on N:N domains on relations
* Fixed issues on email template localization
* Now saving a new menu don't freeze the interface
* Remove loader and active button on failure when save/advance process instance
* Correct data returned for involved cards on views
* Now interface doesn't break if current user has roles with space in code
* Scheduler generate correctly date from rules
* Fixed issues on bus import circular references handling
* Added possibility to add a zero byte attachment
* Handle redirect on interface on SAML
* Correct card popup position when dimensions set to 100%
* Correct behaviour input parameters filters defined from administration module
* Added print view default permission when user is superuser
* On "Read emails" task the reply emails now are correctly processed and not rejected
* Added possibility to ignore references that not exist on waterWAY
* Now login button with SSO is present in login form after session timeout
* Disabled long press functionality on map when modify geographical attributes
* Filter is now properly cleared when filter mode is changed
* Updated possible values for link attribute
* Correct errors on database recreate
* Inline relationships are now correctly displayed when using a domain between a class and its superclass
* Notification in-app now work also with scheduler
* Fixed error on safe CQL management when use non admin roles
* Correct loader on lookup array column filter on superclasses
* Correct wrong reference attribute binding on import template
* See always ifc model on BIM viewer Xeokit after Xeokit version upgrade
* Fixed error on extended data in tab relations when have disabled classes
* Update description reference when editing linked data on create/modify widget
* Card event trigger now work with event card_create_after and filter on waterWAY
* Fixed cross site scripting vulnerabilities on relation graph, svg files and map
* Fixed ui crash when do compulsive row clicks on process instance
* Fixed fulltext search on filter attachment
* Fixed filter based on function on email reader job
* Correct style of mandatory boolean fields
* Show thousand separator on decimal field also when scale propriety is set to zero
* Bugfix report list on deprecated rest v2
* Correct API setLinkValue on autovalue
* Bulk delete now work also when selecting a big number of cards
* Interface doesn't break if create card and relations and extended data is active
* Now is possible to start or finish view rule or autovalue with a comment
* Now is possible to select gate on database and ifc tasks
* Now deselect items from grid if click outer of an element on map
* Set always lookup attribute value if present
* Bulk actions can now be used properly when they are disabled at system level and enabled at group level
* The edit and open tool on parallel activities from workflow now works correctly
* Now immutable fields are saved on attachment creation
* Now scheduler is updated also when remove data value on field
* Fixed view of PDF reports on Safari browser
* Fixed processes creation on process view
* Added missing padding on card/process popup
* Fixed modify action on right click menu on grids

## CMDBuild 3.4.1 (released 2022-09-30)

### NEW FEATURES:
* Added search box on add attachments from the document archive on emails
* Added the possibility to alphabetically sort the list of available grid columns
* Added new state unread for emails
* Added classname into event trigger on waterWAY
* Added notification when the size of uploaded attachments in the email is too large
* Added right click menu on cards and process instances grids
* Added new configuration for define which attributes must be in read-only mode on tab master detail
* Added bulk action delete on attachments
* Integrate PostgreSQL extensions
* Added the possibility to create the same relation between the same two elements on the same domain
* Allow connection to clustered database
* Configure the log retention in administration module
* Show involved cards as relations in items on views from join
* Manage user not found message on SSO authentication
* Add configuration to add custom text into login page
* Added in-app notifications on scheduler
* Manage configuration to disable auto login on SSO
* Allow edit of interval ranges on thematism
* Added possibility to set permission read or write for the single tabs
* Added possibility to filter email templates by class
* Added option on grid to disable ordering on one column
* Added modern authentication for account emails
* Added system function to know if the service is enabled or not
* Added configurations for mobile RFID reader
* Add possibility to get waterWAY schema from command line
* Report file with errors in waterWAY
* Added possibility to exclude create feature on import templates arbitrarly on waterWAY
* Added column filter on administration users grid
* Added tool to navigate to related card on scheduler
* Added possibility to define as keys for the reference multiple attributes on waterWAY
* Added possibility to create dynamic filters for waterWAY templates
* Added the possibility to manage system events in gate events
* Added flag on emails to switch between chronological view and status view
* Added client API to abort processes or resume suspended processes
* Added in filter the possibility to select the operator and create complex filters
* Added new fieldset in views from join to customize selected attributes
* Added possibility to define order of columns in views from join by drag and drop
* Added ecql compatibility in rest V2
* Added exit confirmation popup when saving data and close window
* Added the possibility to translate email templates
* Added abort send email action
* Clone also translations when clone menu items on administration
* Added configuration to manage also time in calendar component
* Added default sorting on subtype for superclasses on administration

### IMPROVEMENTS:
* Keep button select on add card map when add geoattribute
* Added config validators on widget calendar configurations
* Support foreignkey on dmsmodels
* Highlight grid preferences when it is active
* Change bus description editor between read and write mode
* Improve change password on administration
* Allow drag and drop to remove nodes of menu elements
* Added tool in administration to sort lookup list
* Show relation attributes in relation box on relation graph
* Show all relations between two cards in relation box on relation graph
* Remove blank spaces and set max height in inline relations
* Improved the view of entity name in layer and properties tab on BIM
* Group field in login form is now marked as required
* Set correct favicon for openmaint
* Hide checkbox show in grid and show in reduced grid if hide in filter is true on administration
* Added possibility on calendar to add multiple sources that create events
* Added columns to email accounts grid on administration
* Added hidden columns on email templates grid on administration
* Set with original description the empty text on menu item
* Added message for hidden tree nodes on menu tab on localization
* Added tool in title navigation tree pages to show main card information
* Added loader during layout generation
* Add type of action in detail window title
* In administration used linkable display fields for references, foreign key and lookup fields
* In administration manage validation of runTime property on search filter
* Added popup to manage tenants when user has many tenants availables
* Improved the generation of cron expressions
* Now the attachments in a process are saved before the execution of a certain step
* Added help for keyboard shortcuts in BIM viewer
* Now when you delete a thematism is show a confirmation popup
* Auto clean of reports saved on internal tomcat temp
* Added scrollbar on login tenant menu selection
* Added active flag on notification templates and accounts on administration module
* Reduce requests made on custom form widget with reference fields
* Added possibility to select infowindow text
* Show loading icon on bulk edit
* Sort the entries in the domain section by description
* Chat avatar image is limited to 2MB
* Now is possible to update multiple attributes when delete a record from import template
* Added possibility to handle service user on waterWAY
* Now in lookup column filters are visibile only assigned values lookup
* Now resfresh grid on emails tab is always clickable
* Update Xeokit version and IFC to XKT converter

### BUGFIX:
* Clear counts on clone card
* Correct behaviour tools on BIM viewer Xeokit
* Fixed tab properties on BIM viewer Xeokit
* Set email status in draft on save form on first activity process
* Added loading mask in regenerate template on emails
* Added clear filter in tab email
* Relations are not remove from grid if deletion is not successful
* Fixed error that appears when remove two cards one after the other on the map
* Fixed error when canceling or executing query search while loading request
* Relations on same class are show correctly in card relations tab on relation graph
* Views in menu generation are sorted
* Column filter now work correctly
* Default filter is now set in view from join
* Reference now contains values if operator in edit filter is null or not null
* Update correctly filter and sorting attributes in view from join if modify value alias
* Gis menu is always visible in layers tab on map
* Correct selection GIS levels in layers tab on map if childs are folders
* DMS category is now available in file attribute configuration after creation
* Added check of already existent attachment in attribute file
* Set attribute file invalid if DMS is disable
* The names of the filter attributes in the scheduler is now translated
* Now is possible to reselect original value in edit relation
* Set DMS category order in file attribute configuration on lookup order
* Now signs is not removed when modify value in numeric fields
* Check permissions on doubleclick row in administration notification templates
* Hidden some fields in the configuration of domain 1:1
* Fixed typing process attributes in job configuration
* Now it is possible to sort correctly attributes in configuration mapping import DWG
* Filtered the insertion of multiple dots or minus signs in integer fields
* Fix translation for action column tooltips in administration
* In the view mode of the dashboards isn't possible to edit the default field for lookups
* Remove default filter in map navigation when selected geometry is outside it
* In import DWG now is possible to set not delete option if previously is update card
* Without cron expression is not possible to save tasks
* If the widget isn't implemented, the interface doesn't break
* Now when use menu navigation tree the map button is hidden
* Now when navigate to view from filter section, the interface doesn't break
* Set field card mandatory in IFC import based on the value of some parameters
* Correct manage of secure CQL on navigation tree, widget and dashboard
* Remove possibility to create view on scheduler
* Add the possibility to create new view in model statistics on administration
* Correct errors on old api when use new type of attributes
* Now send date attributes as JSON format on request to generation email from template
* Correct filter on message type on administration bus messages
* Added user avatar validator
* Now default print CSV on the class grid export all text content
* Now HTML editor always apply changes in source mode
* Fixed error on delete function filter in row privileges
* Now wrong cron expression in one task don't block all tasks
* Correct the columns headings of the email template grid in administration
* Corrected various translations
* Infowindow isn't visible if the geoattribute is hidden
* Hide tab infoWindow in view in administration if the geometry type is shape or geotiff
* Asynchronous reports now works also if there is an invalid session on db
* Filter on join views now manage attribute date
* Fix view report with parameters input on task send email
* Added patch to convert id to code in card metadata
* Attachments preview now working everywhere
* Tenant config now is visible in mode read only
* Fixed conflicts with card counters if you are in the card scheduler tab
* Keep map elements selected if multiselection is active and you show or hide elements with zoom in or out
* Attribute file now check the allowed extensions
* Disable classes that are disabled from the domain in the master detail tab add card
* Fix function tags
* Now expired password work correctly
* Now gates without valorized description are visible in bus descriptor menu
* Now use groovy reserved words as attribute names in process don't break the logic of workflow
* Date tooltip on chat is now correctly visible
* Default value on boolean attribute is now evalutead correctly
* File attribute don't accept not existing dms category on creation
* Now linkcard widget works also if filter is null
* Now attribute is correctly visualizated if in view rule is set a comment
* Hide in filter propriety now work also on attachments
* Dms model is hidden in the choice in the category if it is disabled
* Parse name used for advanced filter
* Attribute dateTime now show data correctly in custom Form widget
* Inner join in view from join now is saved correctly
* Added localization of class in card panel on BIM viewer Xeokit
* Attribute date is now correctly visible on edit data relation on card edit
* System status chart now show all nodes on cluster if they have same hostname
* Cluster riconnect to the system if they lose the connectivity
* Dashboards are now visible only to groups that have permissions for them
* Fixed error on embedded ecql filter when it contains commas
* Fixed error on reports with certains fonts
* In lookup array data are filtered correctly
* It is not possible to obtain superuser permissions with cql
* Navigation to reference instance from relations show the correct layout of custompages
* Update of existing card in script groovy on simple classes now works
* Correct problems on current user on waterWAY and etl gates
* Operator `is null` is now supported on etl filter on waterWAY
* Disable save button after created a new relation from relations tab and none is selected
* Correct problems on calculated events on scheduler
* GIS import DWG configuration is now possible in waterWAY
* Correct error when defining an IFC import template
* Added possibility to set decimal separator in GIS import export
* Correct bug on keyattribute combo in gate import template
* Fix translation for master detail label when referencing simple classes
* Fix functions outputs localizations on dashboards
* Now is possible to add card in master detail tab for simple classes
* Correct history of events in scheduler
* On class validation error disappear when form become valid
* Set the execution mode on runtime in reports when it is not setted
* Bus import now handling correctly circular references
* Load correct data in lookup and lookup array when use form triggers
* View always record data on inline relation
* Remove the applied parameter filter when clicking on the navigation tree menu item
* Variable called Number in groovy script now doesn't make errors
* The report selected on task send email now is attached to it
* Grid on linkCards widget now doesn't load infinitely
* Correct scale, orientation and position for elements in import DWG
* Fixed error on getting cards using a relation filter when the target class has a formula attribute
* Manage function validation on filters
* Now dashboard with view source type are correctly filtered
* Import database task works correctly again
* Now selection of attachments in principal grid work correctly
* Redirect user to management when access to dbconfig page or patches page and db or patches is already configured
* Split attachments are now also converted to json or string
* Correct error when create app notification template
* Now disabling the template works on database import
* Fixed infinite loading when have a GIS tree navigation on subclasses with superclass nodes
* Correct manage of CQL when using lookup values
* Source card widget on scheduler now show the card also if the event is created from an attachment
* Correct permissions group when have a foreign key attribute on class with only read permissions
* Now search in all templates on administration works
* Now lookup array attribute is printed correctly
* Now report parameters are correctly auto valorized
* Fixed error when creating card in import DWG
* Correct the view of processes on the linkcard widget
* Fixed error on export report when file format is RTF
* Fixed error on lookuparray attribute filter
* Fixed error when delete lookuparray attribute
* On custom form widget function call is always made and the modifiability of the field based on parameter read only has been corrected
* Now on BIM viewer Xeokit the switch from 2D to 3D and vice versa always works
* Fixed errors with PostGIS 3
* Now interface doesn't break when navigation tree is set to subclasses and no classes are selected
* Allow new domains in an email address
* Create events from scheduler even if a validation rule is set in the field
* Attributes not in template are now not updated on waterWAY
* Now search filters on administration don't lose some parameters
* Hide import key attribute on import export template when import mode is add
* Sort group and user items on scheduler rule
* Hide arrow on add task btn on process tasks tab on administration
* Now geovalues center on map is calculate only on visible points
* Fixed refresh data and infoWindow position when modify geovalue
* Fixed data update on relation and master detail tab when performing actions on these tabs

## CMDBuild 3.4 (released 2022-01-28)

### NEW FEATURES:

* Removed legacy shark code
* Removed legacy schemas support: quartz, bim, shark schemas will NOT receive any special treatment any more
* Update jasperreports lib to 6.17.0
* Require java 17+
* Update maximum version of postgres to 12 and minimun to 10
* Deprecation of beanshell: use groovy as default in more places (eval ws, etc)
* Implemented chat notifications and user chat framework
* Add new modular platform waterWAY to manage data
* Manage CMDBuild Service BUS and job logs
* Add Biginteger attribute
* Add multi selection lookup attribute
* Add link attribute
* Handle geoattributes permissions
* Add new property in administration for mapping on documental
* Manage load file DWG directly from the map
* Add the possibility to organize map layers in a menu
* Allow configuration of Info Message on Geo Attributes
* Allow card creation from map
* Add support for markdown on field text
* Add password attribute
* Add formula attribute
* Add file attribute
* Add icon to navigate on field reference to associated card if have permissions on this
* Add domains 1:1
* Manage Groovy scripts
* Add group configuration to manage bulk_update, bulk_delete and bulk_abort for all classes
* Show labels on map elements (all o just a few)
* Add column user's description in history grid
* Allow custom menu configuration for views from join
* Allow inline widgets
* Add button to print the map on pdf
* Added reference relations, other relations and system history in history tab
* Allow geoattributes multi-selection on map
* Asynchronous generation of reports
* Add new BIM viewer Xeokit
* Allow navigation tree as default page
* Added attachments preview in emails
* Add possibility to set layout for closed processes
* Add possibility to exclude attributes from filter creation
* Added function filter for filters created from the administration module
* Added per-join configuration to enable user privileges filter
* Show widgets on attachment creation

### IMPROVEMENTS:
* Add help field in view when new propriety helpAlwaysVisible is true
* Improved field features editors in administrator module
* Add number of grid records in linkCard widget
* Add hierarchical view in class and process permission management
* Open popup to load file when click on drag area in attachments widget
* Disable forgotten password if allow change password is disabled in administration
* Add save button grid preferences in scheduler page
* Hide SSO authentication buttons if the session exists
* Avoid show simple classes in schedule rule definition class list
* Add order legend grid thematism
* Improve email address validation
* Enable double click on long press popup in map
* Hide button save grid preferences when user is on map
* Add the status of the scheduler service in task list
* Allow negative delay period in schedule rules definition
* Show class name also in extended data relations
* Allow custom filters in UI filters management
* Open all card actions as detail window on map
* Add properties HideInCreation e HideInEdit in widgets configuration
* Improve tabs in BIMsurfer Viewer
* Select all geoattributes creates on the map for a card
* Reorganize menu in administration module
* Add loading mask on calendar widget
* Added error directly on add attachments if it has the same name as the present attachments
* Show always labels input parameter and value on filter
* Add binding in view rules
* Remove localization flag in context menu and form widgets in administration
* Improved speed on menu creation
* Restrict file list based on import template filetype
* Add visual indicating on download loading report

### BUGFIX:
* Fixed form validation on classes
* Add dashboard in administration from section model statistics now work
* In custom menu now is possible to set name elements with spaces
* Ifc import no longer fails on empty attribute values
* Add specific error in bulk edit when no attribute is selected
* Resize window editor script edit now works
* Corrected various translations
* Fixed break map cards list
* Expand card in grid list when select card on navigation tree gis
* Fixed visibility custom pages on custom routing from admin module
* Languages configuration on administration work now correctly
* Expand scheduler record also after refresh
* Group permission header checkbox (select all) if no longer slow if current column is sorted
* Remove geo attributes panel from process in administration
* Correct infinite loop on login page caused by default language not mandatory in administration
* Card deletion after incompatible filter deletion now doesn't cause infinite loading
* Disable sorting column Type in scheduler grid
* Navigation tree loads the correct page when moving through the tree while a card creation panel is open
* Field "Attribute message" in administration for process now filter data
* The query filter on grid is now applied also when the search field is fulfilled
* User default Group is don't clear after selection change on group list
* Correct visibility deletion option fields domains in administration
* Remove support of html and markdown on text attribute of domains
* At login module pressing enter multiple times to perform login now don't break UI
* Dynamic images now working also in subreports
* Fixed error when editing form widgets and window height is small
* Remove notification filter not valid on attribute filter attachments card
* Correct appearance of customForm attributes
* Save button now work correctly in administration for attribute creation
* Clicking on grant column header for processes select all elements
* Now is possible to change direction of recursive domains in menu navtrees
* Active column for lookup values in grid is now sortable
* Add validation input on lookup type and dms category type name
* Email sign placeholder is visible on fullscreen editor mode
* Disable add button also if user has read only permissions in one category in attachments
* Cancel button now work correctly in administration GIS Navigation
* Tree in administration GIS Navigation don't break if change origin class
* Fixed strange behaviour when deleting context menus on classes
* Correct values of ecql filter in linkCard widget
* Correct selection type attribute on administration attribute creation
* Fixed import/export error on retrieve class attributes
* Keep selected elements when updating an advanced filter with a condition on relations
* Grid reload now deselects all elements
* Don't show process without XPDL
* Add possibility to use timeout in file download
* Process notes are now saved even at creation
* Disable possibility to remove attributes from list on import/export domain template
* Task send email don't break UI
* Fixed error on save filters from relation caused by cards attributes
* Enabled classes on domain are empty if "enabled classes" tab is selected before navigation
* Fixed problems on disable classes on domains that weren't working properly
* Link on change module (admin/management) user menu now working
* Select first menu voice on login if default page group isn't set
* Order rules in user menu by traduction
* Remove invalid filter message on filter on column
* At login set the preferred language as the language to use
* Fixed error on calendar rule definition button on date attribute
* Fixed missing configuration content analysis in job read email
* Fixed elimination value on button delete on default page in administration section group and permissions
* Correct fieldset style in views
* Correct ordering columns on relation tab with extended data enabled
* Relation inline are no longer visible in create pop up
* Correct dashboard visualization in administration
* Fixed problem on safari that when the session was expired weren't show login but was empty
* Fixed problem on firefox that custom permissions don't appear the first time you open a grant configuration
* Add tooltip on administrations tool that didn't have it
* Remove infinite loading mask on BIMsurfer Viewer when BIM Project don't have load file IFC
* Add confirm message to delete draft message when refresh grid in manageEmail
* Fixed error when creating a view from filter with filter on relation attribute
* Attribute maxLength is disabled on inherited attribute instead of hidden
* Add class help when create a new card
* Correct position relation graph node description at open popup
* Fixed error when create new process from layouts tab
* Attachment filter is in read only mode on administration if form mode is view
* Add ignore tenant when have set a single tenant
* Fixed error that ignore rows grant on view join not shared
* Show active toggle tool in join view
* Update process form status in administraton if xpdl upload fail
* Fixed error in views from filter that not filtering on attribute used to create view
* Add control in clone card with relations where exist destination domain
* Add pgsql mode to ace editor
* Fixed problem on scheduler that doesn't handle negative delays correctly
* Email templates now show correctly the value of the delay field if equal 0
* Enable search attachment text
* Set etl gates search empty text less generic
* Fixed error in view inline relations that set scrollbar in grid always on top
* Starting url now work if link is to administration
* Fixed error that break interface in administration in tab Disable Classes on domains
* Show key import also when is composed by more than two attributes
* Show correctly delete icon on Safari in schedules attachments
* Fixed clone valor Value on clone import/export template
* Hide view on class if this class is disable
* Fixed loading of nested reports on CMDBuild
* Disable classes not selectable in add card button in relations popup
* Fixed problem on template import for timestamp
* Fixed handling of time attributes in xlsx export
* Manage others HTML symbols in report
* Uploading a lot of documents no longer causes CMDBuild to loses metadata
* Add possibility to filter data on field class in multitenant configuration
* Fixed validations error on import template in administration
* Add possibility to disable editing cell on grid in widget customform
* Add possibility to order columns inside grids when creating relations
* Remove edit button in system on server management administration
* Fixed problem that save the edit mail already present also you click on cancel
* Fixed problem on cron expression on card visualization
* Delete notify popup without message after creating an email account
* Fixed click on translation button on lookupValue in administration

## CMDBuild 3.3.3 (released 2021-12-23)

### BUGFIX:

* Fixed wrong behavior of join view filter with permission
* Fixed issue affecting attachments when querying card from superclass
* Uploading a lot of documents no longer causes cmdbuild to freeze
* Filter request tracking payload (strip sensible auth data)
* Decoded report name in routing to prevent problems with reports which have spaces in name
* Create/modify widget works properly again
* Attributes on domains work again inside inline relations
* Fixed view join permission handling
* Fixed decimal/double printing in card report
* Added context menu handling for superclasses
* Dropped cache after localizations import
* Added basic management for row grant on user join view
* Fieldsets toggle again on Safari
* Added decimal digits as admin definition for double in card print
* Fixed serialization of widgets in card details with no permission
* Fixed translation preload to include file translations
* Infinite loading mask when creating a BIM project with a deleted name no longer happens
* Fixed null serialization of idcardcqlselector in rest v2
* Timestamps in relations are now parsed
* Fixed relation filtering in geovalues
* Fixed issue that prevented processing cad element set with no entity
* Fixed error on customform widget refresh button
* Fixed reference processing for email for cross tenant references
* Deleted bimobjects related to bimproject when removing a project
* State attribute in administration no longer shows system lookups
* Redirected to activity with same subset in process execution
* Value 7 on Day of the week can now be used
* LinkCards widget now opens details on popup
* LinkCards widget now shows inline attachments
* Fixed issue with SMTP configuration with ssl
* Email attachments are now saved before the process advance
* Dropped user class cache after adding/changing xpdl in a process
* Handled attribute permission in card serialization rest v2
* Handled rotation of dwg elements in dwg import
* Avoided serialization of dms categories in class structure when disabled
* Deleting process layout now sets record to null
* After changing password the page now returns to precedent page correctly
* Patched gis tables to set IdClass default value
* Added possibility of sorting for card relations
* Shown user messages when file download fails
* Fixed attachments serialization with null category when dms category is disabled
* Add attachments from the document archive doesn't break the UI anymore
* Added support for multiline strings
* Added filtering based on _type for card relations
* Disable widget if class is undefined
* Fixed rest v2 dms category id serialization
* Handle comma in email reference header
* Reloaded menu item after card creation on nav tree root
* Manage Attachments on closed activity now works if active in the configuration system and disable on the process
* Double click on attachments row now opens the attachment if user hasn't write permissions on that category
* Added custom fieldsets in custom dms models
* Fixed issue when processing reference values within cql expressions
* Custom pages on custom routing are now visible from admin module
* Fixed error on attachments preview if name contains []
* Import domain tamplate validation no longer fails
* Updated language cookie before user preferences save
* Fixed issue when processing inactive processes with invalid wf provider
* Added check on custom components loading to get js files only in management module
* Always renew activity instance id after activity is completed
* Ui no longer crashes on compulsive domain change
* Added translations description for Category and FileName fields
* Included widgets and contextmenus in class/process translation tab
* Detailed view is now serialized on view update
* Refreshed grid view in row on active toggle click
* Added missing padding in Notes tab
* Domains tab is no longer enabled on simple classes
* Email signatures can now be deleted
* Multi-selection is again deselectable
* Opening last grid card after deleting last card loads again inline on Firefox
* Navigation trees no longer shows disabled domains on administration
* Fixed update of only one custom component
* Fixed behavior of search user in All Users list
* FormWidget now handles correctly cql variables and expressions
* Edit card button is now enabled for inline relations inside "Create / Modify Card"
* Removed error template from import export template configuration
* Fixed geoserver layer upload errors
* Added active attribute to gis geoserver layer
* CreateModifyCard no longer throws error if reference is not set
* Restored missing geo style rules params
* Attachment counter now handles dms permissions
* Fixed grants sorting list by mode
* Fixed sorter handling when attachment filter is applied
* Firefox no longer generates error on saving attribute twice
* Fixed gis and relation navtree circular domains
* Removed disabled classes from domains
* In custom components and custom page, files fieldset are now visble in view mode
* Enable/Disable button for dashboard now works as expected
* Fixed issue when processing cad import context filter
* Error downloading report in CSV format has been solved
* Incoherent behaviour when deactivating GIS layers visibility has been fixed
* Changed csv encoding default value
* Disabled reports are now shown in admin module
* Fixed minor cad import issues
* UM position values are now translated
* Promoted caches to system to avoid hourly refresh
* Added session refresh when active requests on session cleanup
* Cql for masterdetail domains is no longer ignored
* Ok button in admin view on filter now works as expected
* Calendar widget values no longer disappear
* N:N domain now supports inverse relation
* Database import are now shown in task manager
* Clone dwg template from detail window is now supported
* Fixed generic Errors on Groups and permissions
* Fixed attachment category permission handling
* Removed detailed=true when making events request
* Fixed error shown when advancing process when in superclass
* Domain name is visible again when editing reference attribute
* added support for external dumps with env folder
* Fixed issue in join view query when processing out of order table join
* Gis navigation now manages view mode
* Polygon geometry are now shown on GIS map
* Added composite filter validation
* Enabled contextual menu button items on maps
* Roles are now shown translated
* Added auto cleanup of attachments missing on dms
* Removed load mask affter the loading of the first page
* Added auto cleanup of attachments missing on dms
* Default template for data import config no longer makes the template unselectable
* Administration lookup search is now working as expected
* Changing session timeout no longer breaks the interface
* Descriptions of objects under administration are no longer considered valid if they contain only spaces
* Import management combo no longer crashes the ui
* Fixed generic error when deleting class
* Fixed start new workflow with soap updateWorkflow function
* Added support for different APM in soap attribute list serialization
* Added language header in rest preload function
* Sanitized notes/metadata of card attachments
* Join view loader is no longer stuck after save if attribute grouping contain spaces
* Fixed bug in menu folder translation saving
* Added fulltext attachment search support for SharePoint dms
* When typing in view with filter atrtibute dropdown in administration fields no longer get reset
* Fixed permission handling for relation graph
* Set multitenant mode on processes is now working as expected
* Fixed regexp used to display tousands separator
* Expand view record is no longer slow
* Dasbhoard pop-up is now working properly
* Menu is now ordered correctly again
* User activation botton is working again as expected
* Restored missing foreignkey triggers
* Fixed issue with relative location gis import, when reference points to superclass
* Disabled cad hatch parsing

## CMDBuild 3.3.2 (released 2021-05-04)

### NEW FEATURES:

* Added search possibility on the categories and on the metadata associated with the documents attached to the data cards
* Possibility to set permissions on attached documents different from those of the class they belong to
* Added download all button for cards attachments
* Added email signatures
* Added possibility of exporting process data as well as class data
* Added possibility of filtering processes based on the current step
* Possibility of saving in the user preferences the sorting of the columns in the grids
* Enabled the use of contextual menu in the map view
* Added column filters on Simple classes
* When adding content from navigation menu the relation will be created by the system and the menu will refresh
* Added plugin management of the different SSO authentication available (CAS AD/LDAP, SAML 2.0, ADFS 4, OAUTH2 KEYCLOAK, etc.)
* Added the possibility for the user to choose one of the enabled authentication methods at the login
* Added SSO authentication at webservice level
* Added the possibility of consulting in the Administration Module the active authentication methods and the parameters set for each
* Added loading spinner in content header and in form body
* Performance improvements

### BUGFIX:

* Grid view in row on active toggle click now correctly refreshes
* Fixed multitenant message in administration module
* Domains tab enabled on simple classes is no longer enabled in administration module
* Improved email expr processing to use client/server card data
* Multi-selection is now again deselectable
* Opening last grid card after deleting last card now correctly loads inline on Firefox
* View with join now allows attribute named 'description'
* Fixed bug that prevented the update of max length for string attributes
* Navigation trees no longer shows disabled domains in administration
* Mananged tenant change when user has not enabled multi-tenancy
* Fixed bug that prevented search user in "All Users" list
* Tab details is no longer always enabled
* FormWidget now handles correctly cql variables and expressions
* Removed desktop custom pages in mobile menu serialization when using Default
* Edit card button is now enabled for inline relations inside "Create / Modify Card"
* In admin navigation trees skip node creation if source or destination class is disabled
* Removed error template from import export template configuration because never used
* Parameters for component context menus are now correctly handled
* Fixed geoserver layer upload issue
* Fixed processe instances ordering by tenant id
* Fixed error that prevented handling of domain attributes in join view
* Import/export database password field is now hidden
* Data export now fails if template contains disaled attributes
* Fixed internal cache refreshing mechanism to avoid periodic cache drops
* Fixed various navigation tree wrong behaviors
* Fixed Import/Export template clone errors
* Fixed error that prevented CQL for master/detail domains from being used
* Fixed bug that prevented ok button in admin view from working
* Calendar widget values no longer disappear
* Added widgetId where missing in widget data
* Database import templates are now shown in the task manager
* Files whose name contains [ ] no longer give bad request on download request
* Fixed error that prevented translation of geo-attribute from administration
* Fixed error that prevented Domain name to be shown when editing reference attribute
* Default Import/Export combos are now updated after add or remove template
* Fixed system config decimal separator validation
* Gis navigation now manages view mode
* Grant filter icon is no longer higlighted if column privileges are default
* Polygon geometries are now shown on GIS map
* Roles are now shown translated
* Fixed decimal comparison when checking changed attributes in import
* Default template for data import no longer makes the template unselectable
* Fixed error that prevented lookup search from working properly
* Changing session timeout no longer breaks the interface
* Object under administration are no longer considered valid if they contain only spaces
* Fixed lookup value in schema print to show translated description
* Fixed filter support for geoAttributes get center function
* Fixed start new workflow with soap updateWorkflow method
* Join view loader no longer gets stuck after save if attribute grouping contains space
* Fixed Bug in menu folder that prevented translation saving
* When typing in view with filter attribute dropdown in administration fields no longer reset
* Fixed cross site scripting related issues
* Added permission for relation graph
* Fixed error that prevented setting multitenant mode for processes
* Fixed total count in view with join card list
* Fixed issue that prevented open import/export template from class
* Fixed domain description translation in card report
* Dasbhoard pop-up now works properly
* Menu is now ordered correctly
* Restored missing foreignkey triggers
* Print csv grid no longer uses thousand separator for numeric fields
* Fixed Join view when handling domains with the same code of a class
* Fixed administration issue for import gis template validation
* Fixed issue with relative location gis import, when reference points to superclass
* Node creation if source or destination class is disabled is now skipped
* createModifyCard no longer throws error if reference is not set
* Avoided serialization errors in widget rest v2
* Fixed error that prevented geovalues from being loaded in particular cases
* Restored missing geo style rules parameters
* Fixed order by Tenant field in grids
* Fixed error that was generated on Firefox when saving attribute twice
* Removed disabled classes from domains
* Enable/Disable button for dashboard does no longer generate errors
* Menu nav tree active toggle button no longer generates error
* Calendare event description/content are now correctly translated
* Fixed regexp unexpected identifier on Edge and Firefox
* Improved administration history dashboard
* Fixed error that prevented downloading report in CSV format
* Fixed incoherent behaviour when deactivating GIS layers visibility
* Fixed add card in filter view
* Fixed default map zoom issues
* Relation is no longer created when card created from master detail with no reference field
* Disabled reports are now shown in admin module
* Fixed various cad import issues
* Admin module: UM position values are now translated

## CMDBuild 3.3.1 (released 2021-01-26)

### NEW FEATURES:

* Views with joins
* CSV import with multiple keys
* Attachment fulltext filter
* Dynamic images inside reports
* Contextual help for process steps
* Added support for new filter operators for lookup and references
* New operator "from filter" in relations filter
* Allow attachments and email widgets at first step of a process
* Included IFC data in BIM View
* Dasboard improvements: open charts in popup and chart download as image
* Added configuration to use a custom page as default view for classes or processes management
* Added Mongolian localization

### BUGFIX:

* Fixed multiple bugs related to dashboard in administration module
* Fixed attachment extension check to avoid extension case
* Fixed dashboard wrong order in output parameters bug
* Fixed getCardHistory soap function
* Changing dms model of a dms category no longer breaks the UI
* Fixed erroneous administration module grants preview bug
* Fixed check if filter with same name already exist in save filter
* Fixed configuring read email task bug that caused UI to not load
* Fixed tasklist serialization with usage of user permission
* Fixed ordering inconsistency by automatic sorting by Id if available
* Disabled selection for not available records in add and edit relations
* Fixed superclass ordering by subtype, used translated subtype description
* GIS selected item are now transparent
* Fixed filter on attribute name from bulk edit
* Added loadmask when loading localization
* Creating a new process in administration now refreshes the side menu
* Fixed attribute group translation inheritance
* Domain filter cql is no longer deleted on domain update
* Groupings of attributes is now be visible on create
* Applying schedules on DMS class no longer breaks UI
* Fixed issue in impersonate api/processing
* Dropping existing layout on process forces a page refresh
* Removed relations serialization of non active domains
* Fixed error that prevented scheduler rule to not be shown correctly
* Fixed error that prevented cloning IFC import template
* Fixed duplicated records in menu navigation trees
* Fixed nav tree direction processing in gis value service
* Filtered away geoattributes in classes without permission
* Fixed issue with default resizer for text area on Firefox
* Adding new attribute on Layout tab no longer removes all items
* Fixed issue for batch job run
* Fixed empty html in IE on report refresh
* Fixed date reading format bug
* Fixed issue in navigation tree when multiple domains on the same level
* Improved execution time of bimserver project upload
* Removed inactive class filters from filter list endpoint in management
* Fixed bug that prevented the removal of uncategorized attachments
* Fixed record not expanded correctly on inline attachments related bug
* Fixed issue in email image processing
* Normalized Associated Card field name in new BIM project
* Fixed email read job configuration regex parameter bug
* Selecting Report from menu when process is expanded no longer causes errors
* Fixed relations inline bug on map
* Fixed issue in embedded item loading
* Fixed bug on multitenant page
* Updated Batik libs from 1.7 to 1.8
* Fixed passive mode configuration parsing
* When group doesn't have a default page UI doesn't show a blank page anymore
* Fixed email processing for uncommon inline content disposition
* Fixed issues in stats query
* Fixed bug that prevented "Import Key attribute" to not be shown on Explorer
* Popup no longer closes at first scheduler event creation in management
* Card ws PUT Request now returns ref/lookup code and desc when no changes are made
* Widget LinkCards now updates the rows after edit when a trigger has delays
* Improvements in Import IFC
* Handled tab separator in class print as csv
* Fixed attribute group translation for process ws rest v2
* Administration attribute header bar title now uses Code instead of Description
* included _LookupType and value in ddlog whitelist
* Various ifc job configuration fixes
* BIM projects "Last checkin date" is no longer editable


## CMDBuild 3.3 (released 2020-09-16)

### NEW FEATURES:

* Added new administration dashboard
* Added logs administration panel
* Bulk update and delete of cards
* New IFC file connector
* New DWG import templates
* Added nested navigation tree in menus
* Import database and gis task
* Added mobile custom components handling
* Added possibility of handling cascade deletion for domains/relations
* New DMS system handling
* Permission handling for custom components
* Added form help text
* New extended realtion header
* Indonesian localization
* Polish localization


### BUGFIX:

* Fixed missing translation flag in Menu > Navigation tree > Description
* Fix issue that prevented bottom border of the view tab panel from showing
* Allow `a` tags (links) in strict html attr values
* Fixed issue that caused wrong sorting on menu navigation trees items
* History tab is no longer empty if fieldset is closed by default
* Fixed bug that prevented click on map point in superclass view
* Added possibility of updating style of system/protected lookup values
* Set tenantid to default user tenant when create/update with null tenantId
* UI no longer crashes after de-selecting "include Inherited" in admin module
* Fixed issue that prevented button "Send Email" from administration module to be used
* Translation panel now handles Activity and Group type
* Opening the filter of a view no longer crashes the UI
* Attachment file type check is now case insensitive
* Fixed issue with target device filtering
* Updated custom components constraint to include TargetType
* Deleting card without relations now consider relations with U/N status
* Added load mask in attribute creation
* Added notification tab in Database, GIS and IFC tasks
* Relation graph no longer crashes UI
* Order of relations shown in management module now reflects the order set in the admin module
* Fixed issue in user preferences processing
* Fixed issue related to format date on filters
* Import/Export template now also uses tab separator
* Fixed server processing of ecql with LookUps
* Added statistics for task Import IFC in administration home
* Prevented error if class or process is disabled
* Added step activity translations in translation panel
* Added Groups translation section server side
* CQL filter now works on GIS navigation tree
* Fixed cascade delete gis values on card delete
* Added support for float4 (float) column type
* Handle template expressions in cal event description, content
* Allowed users to list bim projects
* Handle relation attributes when updating a card with a reference
* Added configs for system jobs
* Ui no longer breaks when re-opening template after save
* Fixed serialization of calendar triggers with admin viewmode
* Added format config for import/export csv
* Fixed class relations get when without domains
* Fixed issue that prevented clone card to properly work
* Improved english core translations
* Added active/inactive item filter for class calendarTriggers, ctx menu, widget, formTriggers
* Fixed issue in embedded items processing
* Fixed issue that prevented "x" button to delete the default account in the email templates to be shown
* Calendar events don't lose values anymore
* Fixed Error in main routing condition code
* Various rest v2 response improvements/fixes
* Fixed various IE UI issues
* Added X-Mailer and other debug header to email sent from cmdbuid
* System config disable inactive user checkbox is now unchecked by default
* Added filter options in reference selection
* Search filters are now ordered
* Removed preset field from dms config page
* Reordering attributes of a DMSModel no longer throws an exception
* Removed limits in geoattributes request
* Added missing labelField translation in dashboard charts serialization
* Various dashboard related issues have been fixed
* Fixed issue that prevented customwidget tooltips to be hidden
* After deactivating domains grid is now refreshed to show new value
* Added ecqlfilter in charts
* Improved relations attributes management in add relation grid
* Fixed wrong grid date render
* Query relation now works with superclass
* Added handling of multiple application names in same entity for Dxf Reader
* ForeignKey in inline import now has code and description
* Fixed inline import/export param handling
* Attribute grouping no longer causes bug on save
* Fixed issue related to long filter marks
* Geoserver layer ws now accepts both id or code
* Fixed widgets id handling
* Added usage of componentId for custom context menu
* Search field in administration now resets on page change
* Added configuration to disable inactive users
* Session menu now filters custom pages without permission and avoids error
* Removed dms models from role grants endpoint
* Reduced default maxLoginAttempts time window to 60 seconds
* Fixed query for group user list
* Fixed issue related to calendar event notification
* Handled precision and scope attrs for function output of numeric type
* SubclassFilter is no longer ignored if subclassViewMode=cards
* Fixed cache issue when processing geoserver layers
* Sql patch now always sets process provider to shark for proper process migration
* Removed download button, upload xpdl for superclasses
* Added sorter/filter for email endpoint
* Save and apply filter no longer opens popup below filter panel
* Fixed issue with RiverFlowid inconsistency
* UI no longer breaks after click on reference attribute linked to the scheduler
* Prevented save of empty filter
* Fixed administrator layout panel bug when trying to remove column
* Fixed issue related to widget data serialization
* Autovalue is now working properly in "details" tab
* Added geoserver version check
* Textarea fields now expands correctly
* Removed old unique indexes on EmailTemplate and EmailAccount
* Geoserver workspace is now autocreated if not existing
* Refresh button on gis view now reload tiles
* Added drop cache event for domains after class delete
* Added relation information in card print
* Added missing domains in schema report
* Inline notes are now editable when create or modify a card
* Server now returns null when widget produces Lookup/reference with id -1
* Active toggle button no longer causes error on detailWindow
* Task workflow attribute list is no longer editable in VIEW mode
* Text parameter is now handled by dashboards
* Process instances list is no longer called twice
* During process creation the print button is now disabled
* Added attachments and email count for process instance webservice
* Scheduler's days advance are no longer ignored
* Fixed issue affecting permission filter returining null
* Added dms model print schema functionality
* Fixed issue with dms document update on alfresco
* Added full calendar trigger info within detailed attribute response
* Fixed issue when processing (duplicate) noactive references
* Fixed issue in cal event notification preview
* Fixed lookup type validation for dms cat
* Fixed model layout tool buttons permissions related issues
* Expanded process record is no longer blank under certain circumstances
* Added tenant check for import, using default tenant if required
* Removed permission check when executing impersonate from wf script
* Subclasses now inherit calendar triggers for inherited attributes
* Fixed cmis attachment preview related issues
* Deleting lookup value no longer breaks UI
* Fixed report permission check related issues
* Improved menu item description/targetDescription processing
* Added print schema button on processes like classes
* Added calendar event attachment management
* Master detail (MD) CQL filter is now working as expected
* Fixed error on file check in send import report
* Widget linkCard no longer breaks if query result is empty
* Added unique client identifier header for all REST requests
* Inline attachments now show attachments
* Added attributes filter on request to get only Id and Description
* Fixed error on reference editors inside grids
* CustomForm widget reference description is no longer empty
* Preselect if unique works again on LookUps
* Handled _status attribute in process instances filter
* Fixed issue in embedded items processing
* Added cascade info for domains
* Fixed Javscript error handling for schedules condition


## CMDBuild 3.2.1 (released 2020-04-22)

### NEW FEATURES:

* Completion of the control page in the Administration Menu of the various services used by CMDBuild, and possibility of consulting/downloading the log files;
* Completion of the password management, with the possibility of blocking it in the event of repeated failed authentication attempts and not proposing its modification function in the case of centralized authentication;
* GIS improvements, with display of the list of elements around the “clicked” point and with an icon for the direct passage from the GIS Tab card to the corresponding card;
* Possibility to limit, in the Administration Module, the extensions allowed for the attached files;
* Possibility to configure, in the Administration Module, the list of languages ​​proposed at login.


### BUGFIX:

* Added missing fields in process rest v2
* Attribute types for standard and simple classes are not longer wrongly displayed
* Meta field is now always available in rest v2
* Fix error that prevented email to be attached to card after a scheduled event
* Fixed error on creating import/export template with merge criteria
* Translations template email in import csv
* Schedule definition rule grid is now updated after active change from detailWindow
* Added owner description in geolayers response
* StartWorkflow widget is no longer hidden on processes
* LinkCards widget no longer generates error for non-admin users when target class is a process
* When deleting dashboard used by menu in administration UI no longer keeps loading
* Mixed improvements for etl import notification processing
* Fixed send error email notification for import job, when file is missing related issues
* Fixed gis attribute processing related issues
* Fixed multitenant on simple class related issues
* After class delete, added cleanup grant and menu
* Fixed issue in log message processing (session expiration)
* Fixed issue in cluster message processing after rpc add
* Fixed server response of etl template
* Avoided fatal error when a menu item is missing in admin view
* Fixed issue in smart expr processing
* Fixed global script config for xpdl
* Fixed widget data serialization
* Enable/Disable button for geoattributes works again as expected
* Non active geoattributes are again shown in management
* Fixed issue that prevented a new attribute window from closing and refreshing
* Fixed formInRow of importExport record related issue
* Fixed various errors in rest v2 serialization
* fixed report endpoint rest v2
* Superclass grid is no longer hidden when user saves default filter and does not save columns preferences
* Calendar is no longer hidden for write permissions
* UI config are no longer saved inverted on change permissions in "other permissions" tab
* Widget openNote is again working as expected
* Fixed issue related to composite filter attribute mapping
* Added beginDate to geoserver layer serialization
* Fixed context menu label translation issue
* Fixed error when listing completed process instances
* Reorder grids no longer breaks UI
* Multi-level lookup no longer shows only last level in the grid
* Added instance name translation
* Deleting a filter just saved now correctly removes it
* Fixed issue with uploadreport cli command
* Boolean 3 phase checkbox now owrks on mobile
* Non SuperUser users now sees to whom the process is assigned after executing it
* Fixed sorting from superclass based on _type
* Reload button in dashboard graphs now works as expected
* Dashboard in demo database no longer causes error in function output
* Fixed error on opening #administration/domains/
* Grid on openAttachment widget now loads values when opening
* 'Clone from' in administration module now works as expected
* Fixed groovy dependencies
* Basedsp properly wors again for SQL views
* Default language for user is now correctly handled
* startWorkflow widget output variable is no longer always null
* Date column is now correctly displayed
* Import csv for domains without 'merge criteria' now properly works
* Fixed ecql/easytemplate expr processing related issues
* Enabled widgets on card creation
* Changing column privileges no longer alters row privileges
* Fixed Bad JSON in _Form Data section related issues
* Fixed issue that prevented the correct label to displayed while creating a new group
* It is now possible to delete the group if selected
* Fixed ui for new post-csvimport send report (bypass file attachment check)
* On process creation the ui no longer sends not needed GET call for related filters
* Admin ui now handles new config params for attachment file check
* When receiving error after email template deletion UI now refreshes correctly
* Tenant is no longer hidden for some classes
* Client now checks for attachment file types
* Fixed issue in etl processing when import key is a reference
* Widget linkCard is now correctly self-valued if the defaultValue is set
* Grid is now updated after active change from detailWindow
* Required link card now saves data correctly
* Fixed error generating mail for import job when file not found
* Remove geoattribute no longer causes not needed calls to other geoattributes
* Grid of assigned templates no longer hides the templates
* Default class import/export template can again be saved
* Class and Process category lookup no longer show id instead description
* Template for import csv for domains now has IdObj1/Idobj2 attributes
* Fixed "download report" button for InternetExplorer
* Error notifications no longer shows json instead of message
* Fixed issue when processing etl import with null values in key attr
* Fixed access denied for non admin users on context menu call related issues
* DefaultSelection on widget linkCard now works as expected
* Fixed mark processing for superclass query
* Attribute list columns of a class no longer get stuck
* Fixed query reference code/descr processing related issues
* Reference field is no longer hidden if its description is not in card data



## CMDBuild 3.2 (released 2020-02-06)

### NEW FEATURES:

* Management of the Dashboards, recovered from CMDBuild 2, with some improvement features (see next news);
* Management of the Scheduler, fed either automatically by operating on data cards or manually (see the complete description here);
* Extension of user preferences: home page, customization of lists of data cards (choice and width of columns and sorts), default filter for each class;
* Restoration of Wizard Connector tasks, for the visual definition of synchronization templates of external database tables with CMDBuild classes;
* New types of permissions on processes (in the case of using permissions on the lines of a class, possibility to specify the permission to be applied on extra filter lines);
* Checking of password definition criteria;
* New GIS implementations: completion of the thematism, display of objects present around a point on the map, search for an address;
* Display of the status of the various server services used by CMDBuild;
* New role of “limited” administrator with the possibility of creating users on his Tenant;
* Sign On authentication on LDAP systems through the use of the open source CAS service.
* Core security improvements
* Restws and core test additions
* Added Malaysian localization

### BUGFIX:

* Fixed disabled actions for permissions related issues
* Import export tasks now require an email account
* Fixed various url malformed issues
* Import export templates are now shown in the permission tab
* 'Remove filter' button on views now works properly
* Privileged user flag is now properly saved
* Ui configuration in permission tabs works properly again
* Fixed 'initial page' related issues
* Fixed issue that prevented a process to be executed even with all required fields set
* Reference arrays are now handled in custom widgets
* Fixed issues that prevented a reset of searchbars in the administration module
* Fixed import export templates clone related issues
* Restored missing sql functions for openmaint databases that prevented the usage of multitenancy
* Improved card detail panel UI behaviour
* Error messages during login phase are no longer displayed after succesful login
* Fixed UI issue that prevented a card from being open with a double click interaction
* Fixed process menu button interaction when workflow is disabled
* Fixed issue related to automatic email generation
* STARTTLS and SSL can no longer be activated together in email account creation
* Inline relation of N:N domain no longer shows only one direction
* Fixed issues related to superclass query filter processing
* Quicksearch in group permission now gets cleared after group change
* Fixed issues related to Add Group button in administration
* Fixed issues related to double expression processing in email templates
* Fixed issues related to UI interaction with IE 11
* Fixed issue related to date parameters in process widgets
* UI no longer breaks after attribute deletion on class
* Fixed inconsistency with multitenant class mode in administration
* Fixed process permission handling related issues
* Create/modify card widget no longer breaks
* Email account no longer gets removed following an error
* Fixed positionOf related issues
* Deleting the single card of a class no longer leaves the UI in a loading phase
* Fixed issues related to total metadata processing on last page
* Fixed issues related to starting group processing
* Workflow task process attributes are again correctly parsed
* Date attributes are no longer wrongly displayed when client timezone is changed
* Fixed endpoint rest v2 for attachments download
* Fixed validation rule not applied to text attributes with HTML editor
* TextArea for text fields can now be resized correctly
* Fixed issue that caused malformed date attribute in soap response
* AllowCardEditing for linkCard widget properly works again
* Prevented redirect if no url is defined after navigation render
* Fixed email delay processing value to be in seconds, not milliseconds
* Fixed issues related to attribute translation processing
* Process Hide save button works again properly
* UI no longer gets stuck when opening completed processes
* Lookup list with ecql properly returns the correct results again
* Fixed issue that prevented class context menu translate icon to not appear
* Fixed multiple attachments for email related issues
* Delete domain from class or process domain tab properly removes the domain from menu
* Deleting a superclass in administration now properly refresh the class inheritance dropdown
* Fixed issues related to header param CMDBuild-View
* Fixed issue that prevented multitenant mode translation to be shown
* Custom status of processes is no longer lost at every update in admin mode
* Fixed issues related to webapp installation under ROOT folder
* Fixed sql expression escaping when setting session variables
* Fixed issues related to filters on date fields
* Validation rules are no longer wrongly cancelled in search filters
* Basedsp properly works again for SQL views
* Fixed issues related to interaction with master/detail tab
* Fixed issues related to usage of reference parameters in reports
* Fixed issue that prevented import export administration menu voice to be shown
* UI no longer gets stuck when refreshing a grid with one card only
* Fixed issue related to multilevel lookups with three or more levels
* Fixed issues related to card put endpoints for rest v2
* Attribute grouping translation is no longer broken
* Fixed issues related to special characters handling in rest webserices
* User menu no longer displays an empty row
* Invalid search filters can no longer be saved by the ui
* Fixed issue that prevented IP_ADDRESS attribute type to handle IP range
* Data card sorting is no longer missing in processes
* Fixed issue that caused the interface to be stuck in loading after saving a csv report
* Fixed issue that prevented the UI to load when moving from a custom page to a process page
* Fixed issue that prevented time attributes to be displayed correctly in the history page
* Fixed issue that prevented emails to be processed with utf encoded file name
* Fixed issue that prevented email attachments to be processed when with same filename
* Fixed session login date default value
* Fixed timezone processing issue
* Fixed date/timestamp attribute issue for domains
* Edit button for class domains properly opens the correct panel again
* Fixed custom widget data processing for rest v2
* Fixed email processing after import job is completed
* Fixed ddl log regexp related issues
* UI now allows scrolling through filter list
* Relations tab with no relations no longer generates errors
* Attachment category description is again properly translated
* Validation rules are again working for lookup values
* LinkCard widget no longer opens a blank popup
* Removing default groups no longer freezes the UI
* Fixed issues related to wrong serialization of report attributes in rest v2
* Fixed issue that prevented filters to be removed from group and permission tab
* Fixed issue that caused the login page to display disabled groups
* Fixed issue that caused the UI to not refresh after adding a new user
* Fixed attribute search in class panel
* Fixed issue that caused the UI to freeze after multiple clicks
* Fixed email template pagination



## CMDBuild 3.1.1 (released 2019-09-25)

### NEW FEATURES:

* Added administration interface to create a personalized layout for the data cards of each class
* Cookie handling has been moved server side to improve the security of the application
* Added handling of Boolean null values
* Improved UI for handling processes with parallel activities
* Added form triggers on processes for the execution of personalized behaviours
* Added Bulgarian localization

### BUGFIX:

* FlowStatusAttr send the description instead of Code
* Fixed Id tenant related issues
* Column filter now handles null booleans
* Added validation on reference attributes
* Fixed process abort related issues
* Added active field for search filters
* Denied creation of reference on non active domains
* Improved check before class deletion
* Fixed bim related issues
* Avioded errors when invoking sessions/current to check an existing session
* Fixed reports related issues
* Fixed process save button related issues
* Updated redirects when a page is not found
* Fixed datefield on Firefox browsers related issues
* Added custom widget components interface
* Fixed attachment mandatory description related issues
* Fixed search filter scrollbar related issues
* Fixed attribute grouping related issues
* Fixed page refresh related issues
* Avoided cache loading loop when processing grants on filters
* Fixed clone card and relations related issues
* Fixed 'Active' checknox of class domains related issues
* Fixed domain localization window related issues
* Added row count in user grid
* Fixed permissions for attribute 'Notes' related issues
* Added popup size properties in system configuration
* Added massive relation modification web service
* Fixed print csv from superclass related issue
* Improved execution time of domain list
* Added exception when email storing fails
* Fixed attribute group modification on Firefox browser related issues
* Fixed dms description attachment related issues
* Added optional task list informations in process instances webservice
* Fixed error popup in applypatches screen
* Normalized request id
* Fixed checkbox not appearing in card view
* Added options to improve flow migration performance
* Added parameter to set to null values on migration error
* Avoided description deletion for uploadcustompagedir rest command
* Forced save of only edited grants
* Handled single/multi group in Process Activities
* Added client api to open report
* Added Save and close button in processes edit form
* Fixed missing scroll on relation attributes
* Add condition in view model to prevent null parent errors
* Lookup codes can now start with a number
* Fixed class translation related errors
* Added segments field to geostylerules
* Mail queue scrollbar is now visible
* Allowed custom colors on lookups
* Fixed GeoServer layers loading related issues
* Reload flow status after task completion
* Added stored filter description translation in filter webservice
* Managed double click on inline domains
* Handled `@MY_GROUP` and `@MY_USER` in attribute filter values
* Impersonate with group only is now allowed
* Fixed csv domain import related issues
* Handled objects names containing spaces
* Search filters now shows the mandatory input marker
* The grid after proces closure is now refreshed
* Code, description data for client card when processing email template are now loaded
* Fixed widget related issues
* Menu now loads when all resources are loaded
* Form structure for classes are now correctly stored
* Implemented attachments.download() function
* Handled menu migration with faulty `null` childs
* Fixed date attribute in soap response
* Custom pages now handle active field
* Fixed password autocomplete=off
* Fixed null performer related issue
* Fixed report lookup parameter reset issue
* CQL filters now work inside reports
* Fixed open related card issue
* Added attachment api support
* Email queue administration page now refreshes when email are sent
* Fixed deletion of Import/Export template
* Reports no longer break when parameters have spaces
* Lookup values are now loaded on model generation
* Default class order in now used in csv export
* Fixed incorrect date format in xls export issue
* Fixed changed detection on history related issues
* Added task description translation
* Added task widget description translation
* Handled "Add attachments to closed activities" permission as global parameter and group-level privilege
* View search now affects grid elements
* Improved beanshell script error processing
* Improve handling of csv line number
* Handled status/FlowStatus attribute in card print webservice
* Fixed workflow permission filter on webservice `processes/x/instances`
* HideSaveButton system default is now used when retrieving single process hideSaveButton informations
* Removed binding on process add button
* Handled preselectIfUnique in rest webservice parameters
* Fixed and improved lookup handling in soap webservices
* Fixed cron expression interface related issues
* 'State attribute' value on Processes tab no longer changes from edit to view mode
* Fixed erroneous handling of metadata total field
* Views ParameterType runtime are now properly working
* Filters are now displayed correctly
* Added scrollbar on card's notes
* Handled pagination for views webservices
* Allowed duplicate participant definition while processing xpdl participants
* Unified custom component management on database
* Improved relation permission handling


## CMDBuild 3.1.0 (released 2019-07-10)

### NEW FEATURES:

* Csv/xls[x] import/export (manual or scheduled)
* Configurable sso integration module ( _services/custom-login_ )
* Improved security, support for _PBKDF2_ password protection
* Improved workflow upgrade
* Improved cli utils
* Added gis tematic mapping
* Restws and core test additions
* Possibility of displaying the attached files as an additional fieldset of the data card
* Possibility of displaying the relationships of a domain as an additional fielset of the data card
* Extension of the “Show if” feature as “View rules”
* New feature "Auto value" for fields
* Synchronization between the filter applied to the list of data cards of a class and the corresponding elements displayed on the map
* New custom components that can be used in context menus
* Possibility of uploading the customer's logo which is placed alongside the CMDBuild one
* Added Danish localization
* Added Norwegian localization

### BUGFIX:

* Recovery of the process startup task in the Task Manager
* Report are now translated in menu
* Added uploadcustompagedir command to upload a custom page via rest command
* Reserved domain relations are no longer being shown
* Added configuration to hide save button in workflow
* Added user sorting
* Added card ws distinct option
* Added system config to set relation limit
* Added detail parameter to menu ws
* Geo attribute deletion is no longer generating errors
* Added new feature to center a map when no items are selected
* Added print view function
* Removed unused sorters
* Removed screen flashes in login form
* Added ws to obtain a boundary box for attribute values
* Multiple filter/roles no longer generates a server error
* Added basic fulltext filter for geo style rules
* Added auto redirect to url on login if redirect config is not empty
* Forced usage of subclass instead of superclass model to create cards in reference combo popup
* Attachment widget various fixes
* Added possibility of resize of textareas in attributes administration
* SimpleClass.BeginDate is now reserved
* Processing of int values in numeric xls columns is no longer broken
* Various minor fixes for BIM administration
* Fixed skipunknowncolumns template processing related issues
* Apache poi has been upgraded
* PresetFromCard has been implemented
* Fixed LockNotAcquired error
* Button to remove filters in administration module is no longer broken
* Fixed filter processing related issues
* Gis icon preview now automatically refreshes when updated
* Minor fixes for Icon management
* Group and permission default filters are now saved
* Email widget serialization for rest v2 is no longer broken
* Allow more then one row expanded in history grid
* Default filter on Class are now saved
* Stop queue button is now disabled when queue is not enabled
* Ip type attributes are no longer forced to IPV4
* Multilevel and cql filter have been removed from bim layers
* Gisnavigationtree and bimnavigationtree now have active toggle button
* Added endpoint to obtain class domains
* Added geostyle rules put endpoint
* Fixed query builder related issues
* Changed filter format in rest v2 class attribute response
* Fixed errors in process instances rest v2
* Added configurable strong password algorythm
* Fixed permission of subclasses
* Litteral `{` is now supported in email template
* Fixed widget serialization for rest v2
* Added tenant column hidden in grids
* Fixed relation attributes related issues
* Fixed start activity related issues
* Uniformed tenant label to other labels
* Added geo style rules application without saving on db
* Fixed occasional errors on add form mode in permission page
* Lookup attributes in card relation ws are now handled
* Upload report is no longer broken
* Fixed rest v2 process instances response
* Class -> Domains -> Form are no longer missing 'View' tool button
* Fixed domain errors on new form
* Fixed advanced filter related bugs
* Added system configuration for default email account
* The form of navigation tree is now in view mode after edit
* Domain grids now hide 'M/D, Label M/D, Inline' columns
* Doubleclick on geoattribute grid no longer breaks ui
* Gis icon upload now refreshes the grid
* Input fields no longer make Firefox crash
* Added ws to query distinct values from a class
* LinkCards output is no longer null
* Auto skip email with no TO address (set directly to status `skipped`)
* It is now possible to create/edit workflow widgets in classes edit form
* Domain button from process tab is no longer broken
* Date filter no longer looses the date values
* Process history is no longer missing data
* Load activity in process instance is now specified
* Option to create a default menu is no longer missing
* Fixed calendar related issues
* Process instance history is no longer missing data
* Detail window in administration module is no longer staying open when changing context
* Fixed between filter related issues
* Menu list in administration module no longer shows Code instead of description
* Fixed icon related errors in administration module
* Improved class/process save/upload
* Reports no longer remain stuck in loading
* Nav tree with multiple nodes for the same class are now handled
* Multiple selection flags are now correctly updated
* Wf is now allowed to start with no-db performer
* Added full request log tracking
* Fixed csv stream processing related issues
* Fixed advanced filters with boolean attributes
* Removed popup error after logout
* Reduced system configuration changes processing time
* Combo for class/process/... now shows original description
* Sort option on permission page is no longer broken
* Reserved classes are no longer shown in grant ws
* Card filter for geo value query are now handled
* Localization page now ignores processes if workflow is not active
* Fixed class and process icon related issues
* Fixed geo attribute and gis icons related issues
* Menu shows again source folders in edit/add
* Tooltip no longer overlaps with custom validator
* Added support for function filter
* Now map reads initial configuration ZoomMaz, ZoomMin, InitialZoom, Longitudine, latitudine
* Mousewheel has been disabled on numeric fields
* Fixed hidden relations related issues
* Existing locales of class attribute description are no longer blank
* Process advancement no longer remains in loading
* Migration script now handles unique indexes
* Fixed class creation related issues
* Added system configuration endpoint
* Improved performance of attribute reorder
* Added missing SOAP functions
* Added cli tool to check dump
* Fixed master/detail tab simple class related issues
* Fixed fkdomain ws filter
* Added `isMasterDetail` filtrable attribute
* Added print schema
* Added, on db restore, option to freeze (not expire) existing sessions
* Added backup config files before update
* Attribute filters in report parameters are now handled
* Cmis selection is now forced in DMS settings
* Grid over window size in now scrollable
* Added card print for cards with `"` in attribute label
* Added new wd migration (from shark via db)
* Permission flags in relations are now handled
* Added view card print
* Added wf api for update/advance process
* Card access (tenant) is now checked for relation update/delete permissions
* Default for groups in filters is now saved
* Foreign keys are no longer shown as integer
* Fixed domain active field related issues
* Menus in administration navigation are now sorted
* Added geoserver file handling
* Added uptime to restws cli status report
* Added lookups migration with description for code
* Card display is no longer stuck on loading
* Select all in link card is now possible
* Added email template ws v2
* Fixed typeAhead related issues
* Added attachment category description translation
* Processes description is now translated
* Added client api to get remote lookup from type and code
* Added withCard() method to wf email api
* Added download zip CustomPages
* Modified date serialization on custom form widget
* Menu with already open popups now correctly closes
* Implemented newMail wf api method
* Removed unnecessary store filters
* Fixed custom form widget related issues
* Added missing file/dir error in job import
* Wf card api now returns boolean values
* Added default for showInGrid and writable properties in custom form widget
* Added user config for preferred office suite
* Added defaultSearchFilter in Reference popup instead of variable in viewmodel
* Added process stoppableByUser field in response
* Fixes various issues related to header auth
* Added navigation rule for views in menu after render event
* Implemented findAttributeFor for local wf api
* Fixed parallel gate wf processing related issues
* Clear model attributes in CustomForm widget panel
* Added edit possibility of card from linkCards widget
* Prevent special characters in dynamic model fields
* Added configuration to send always all fields to the server in PUT requests for cards and instances
* Fixed eval sql query related issues
* Functions with non-lowecase names are now handled
* Added add attachments to card via cmdb api
* Added case insensitive processing of `TYPE: function` comment value
* Fixed custom page component processing related issues
* Cleanup of user friendly messages for common import errors
* Fixed order grid details problems
* Fixed import of ipvAny attribute related issues
* Fixed editing relations related errors
* Added properties on grant model
* Email.delay param is now handled in seconds and not milliseconds
* Cm filter keys are now case-insensitive
* Req id is now included in popup messages
* Domain functions minor refactoring
* Added configuration parameter for column offset in import template
* Fixed ws v2 description translation related issues
* Default class filter are now correctly saved
* Added timezone in user config preferences
* Grant eventbus refactoring
* Added custompages active filed
* Card attribute reference fields now accept empty strings
* Fixed support for startsWith and endsWith in strings for IE11
* Added creation of fictitious menu


## CMDBuild 3.0.0 (released 2019-04-12)

### NEW FEATURES:
* Complete rewriting of the user interface, both for the Data Management and the Administration Module, with new layout and new functionalities.
* Complete refactoring of the server code.


## Previous versions

For the CHANGELOG versions up to 2.5.1 view http://www.cmdbuild.org/en/download/changelog-old
