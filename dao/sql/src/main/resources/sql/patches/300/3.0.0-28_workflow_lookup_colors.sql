-- lookup colors for workflow status


UPDATE "LookUp" SET "IconType" = 'font', "IconFont" = 'x-fa fa-square', "IconColor" = '#008000' WHERE "Status" = 'A' AND "Type" = 'FlowStatus' AND "Code" = 'open.running';
UPDATE "LookUp" SET "IconType" = 'font', "IconFont" = 'x-fa fa-square', "IconColor" = '#FFCC00' WHERE "Status" = 'A' AND "Type" = 'FlowStatus' AND "Code" = 'open.not_running.suspended';
UPDATE "LookUp" SET "IconType" = 'font', "IconFont" = 'x-fa fa-square', "IconColor" = '#800000' WHERE "Status" = 'A' AND "Type" = 'FlowStatus' AND "Code" = 'closed.aborted';
UPDATE "LookUp" SET "IconType" = 'font', "IconFont" = 'x-fa fa-square', "IconColor" = '#C0C0C0' WHERE "Status" = 'A' AND "Type" = 'FlowStatus' AND "Code" = 'closed.completed';
UPDATE "LookUp" SET "IconType" = 'font', "IconFont" = 'x-fa fa-square', "IconColor" = '#C0C0C0' WHERE "Status" = 'A' AND "Type" = 'FlowStatus' AND "Code" = 'closed.terminated';
