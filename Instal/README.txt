Laboratory Notebook AddIn for Microsoft Word

/*
* Copyright (c) 2018, Dominik Madea
* All rights reserved.
* 
* Permission to use, copy, modify, and/or distribute this software for any 
* purpose with or without fee is hereby granted, provided that the above 
* copyright notice and this permission notice appear in all copies.
* 
* THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
* WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
* MERCHANTABILITY AND FITNESS.IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
* ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
* WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
* ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
* OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/


---------Compatibility------------
.NET Framework 4.5 needed

Win XP (with SP3), Vista (with SP2) and higher
Office 2010, 2013 and 2016
works with ChemDraw 12 and higher, should work probably on any version of ChemDraw which implements ChemDraw Document 6.0 OLE object for MS Office

works with legal and cracked versions as well


Source code available (C#)


---------Instalation and updates------------

1. Copy Instal folder to disk and run setup.exe (path to instalation folder must not contain diacritics)
2. Restart Word (all opened word documents have to be closed)
3. Enjoy, Lab Notebook tab should appear in the Word

Note: Don't change the folder location (required for updates, otherwise you have to remove LabNotebookAddIn manually in control panels and reinstall - then all settings are reseted to default values)


Test this product and enjoy new features. Please let me know of any hidden errors and your suggestions. Updates will be available.



---------Release notes--------------

v 1.7.0.2
	-Open NMR spectrum button added
	-Problem with settings removed, it is now permanently saved


v 1.6.0.0
	-Target .NET Framework was changed from 4.0 to 4.5 so AddIn will not work on win XP 
	-Added Formatte document dialog, available in the formatting section as little arrow
	-Added Structure and substructure search functionality, saving and viewing LN files, reading and saving to ftp server


v 1.5.3.3
	-solved problem with parsing numbers from the data table when the system default setting for number delimiter was dot, it is now invarinat to the default system settings
	-sum of height default value was changed to 610 pt
	-fixed some minor errors


v 1.5.2.0
	-added option to publish tab to choose how to publish document (docx and/or pdf)
	-some other minor updates
	

v 1.5.0.0
	-added Swap cells button
	-changed "Format heading" button to "Format data table" which now includes borders reset


v 1.4.3.0
	-removed errors caused by working in multiple documents
	-added Insert template button


v 1.4.2.0
	-removed serious bugs


v 1.4.0.0
	-calculation of molecular weights from text and structure should work on any version of ChemDraw (12, 14 and 15.1)
	-added option to settings, whether user want to close ChemDraw application after performing Transfer compounds or Calculate weights commands
	-some other really not important things
	

v 1.3.2.0
	-should work on any ChemDraw version
	-calculation of molecular weights from text still doesn't work


v 1.3.0.0
	-added Transfer compounds, Calculate weights and Formula buttons to Formatting tab
	-added Automatically increment reaction code checkbox (in Settings)
	
!!NOTE: New features (transfering compounds and calculating molecular weights) works only on computers equiped with ChemDraw 12.0


v 1.2.0.0
	-added fancy icons
	-added Appropriately change units checkbox to Export panel
	-removed \n (new line character) bug when performing calculations in data table
	-changed reaction scheme and data table row height to auto
	-optimized Fit to page command, no spacing is necessary, Sum of heights should be now around 620 pt