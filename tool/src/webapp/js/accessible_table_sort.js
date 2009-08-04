/*
Copyright 2007 - 2008 University of Toronto

Licensed under the Educational Community License (ECL), Version 2.0 or the New
BSD license. You may not use this file except in compliance with one these
Licenses.

You may obtain a copy of the ECL 2.0 License and BSD License at
https://source.fluidproject.org/svn/LICENSE.txt
*/

var fluid = fluid || {};

fluid.accessiblecolumn =  function () {
    // Private functions.
    var selectColumn = function (boxToFocus) {
        jQuery ("p", boxToFocus).addClass ("focussed");
    };

    var unselectColumn = function (boxToBlur) {
        jQuery ("p", boxToBlur).removeClass ("focussed");
    };

    var leaveColumns = function (selectedBox) {
        unselectColumn (selectedBox);
    };

    var toggleBox = function (box) {
		var colNum = box.attr('colNum');
		var colStatus = box.parent().attr('class');
		switch(colStatus){
			case 'header headerSortDown':
				$("#user_alias").tablesorter({sortList: [[colNum,1]]});
				break;
			default:
				$("#user_alias").tablesorter({sortList: [[colNum,0]]});
		}   
   };

    var selectColumnHandler = function (boxToCheck) {
        var boxLabel = jQuery ("p", boxToCheck);
        toggleBox (boxLabel);
    };

    var clickable = function (boxes) {
        boxes.mousedown (function (evt) {
            boxes.activate (this);
        });
    };

    // Public members.
    return {
        initializeColumns: function (columnContainerId) {
            // Make the overall container tab-focussable.
            var columnContainer = jQuery ("#" + columnContainerId);
            columnContainer.tabbable(columnContainer);

            // Find all checkboxes and make them fancy.
            var boxes = columnContainer.children ("#surname,#name,#id,#aliastFirst,#aliasLast");

            // Make them key navigable and activatable.
            var options = {
                selectableElements: boxes,
                onSelect: selectColumn,
                onUnselect: unselectColumn,
                onLeaveContainer: leaveColumns,
				direction: $.a11y.orientation.HORIZONTAL
            };
            columnContainer.selectable (options);
            boxes.activatable (selectColumnHandler);
            clickable (boxes);
        }
    }; // End public return.
}(); // End fluid.accessiblecolumn namespace.
