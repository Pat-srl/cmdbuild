Ext.define('CMDBuildUI.util.administration.helper.CronValidatorHelper', {
    singleton: true,
    // This comes from the fact that parseInt trims characters coming
    // after digits and consider it a valid int, so `1*` becomes `1`.
    safeParseInt: function (value) {
        if (/^\d+$/.test(value)) {
            return Number(value);
        } else {
            return NaN;
        }
    },

    isWildcard: function (value) {
        return value === '*';
    },

    isQuestionMark: function (value) {
        return value === '?';
    },

    isInRange: function (value, start, stop) {
        return value >= start && value <= stop;
    },
    isLastDayOfMonth: function (value) {
        return value === 'L';
    },
    isLastWeekDayOfMonth: function (value) {
        return value === 'LW';
    },
    isLastXWeekDayOfMonth: function (value) {
        return value.indexOf('L') == 1 && !isNaN(Number(value[0]));
    },
    isLastXdaysBeforeEndOfMonth: function (value, start, stop) {
        var rule = value.split('-');
        var day = this.safeParseInt(rule[1]);
        return rule[0] === 'L' && !isNaN(day) && this.isInRange(day, start, stop);
    },
    isWeekdayNearDay: function (value, start, stop) {
        if (!/W/g.test(value) && value.indexOf(value) !== value.length - 1) {
            return false;
        }
        return this.isInRange(this.safeParseInt(value.replace('W', '')), start, stop);

    },
    isValidRange: function (value, start, stop) {
        var me = this;
        var sides = value.split('-');
        switch (sides.length) {
            case 1:
                return me.isWildcard(value) || me.isInRange(me.safeParseInt(value), start, stop);
            case 2:
                var safeSides = sides.map(function (side) {
                    return me.safeParseInt(side);
                });
                return safeSides[0] <= safeSides[1] && me.isInRange(safeSides[0], start, stop) && me.isInRange(safeSides[1], start, stop);
            default:
                return false;
        }
    },
    isXOccurrencyOfWeekly: function (weekdays) {
        if (weekdays.search(/#/) !== 1 && weekdays.length !== 3) {
            return false;
        }
        var wd = weekdays.split('#');
        return this.isInRange(wd[0], 1, 7) && this.isInRange(wd[1], 1, 7);

    },

    isTheLastWeekDayOfMonth: function (weekbays) {
        if (weekdays.search(/#/i) !== 1 && weekdays.length != 2) {
            return false;
        }
        return this.isInRange(weekbays[0], 1, 7);
    },
    isValidStep: function (value) {
        return value === undefined || (value.search(/[^\d]/) === -1 && this.safeParseInt(value) > 0);
    },

    validateForRange: function (value, start, stop) {
        var me = this;
        if (value.search(/[^\d-,\/*]/) !== -1) {
            return false;
        }

        var list = value.split(',');
        return list.every(function (condition) {
            var splits = condition.split('/');
            // Prevents `*/ * * * *` from being accepted.
            if (condition.trim().endsWith('/')) {
                return false;
            }

            // Prevents `*/*/* * * * *` from being accepted
            if (splits.length > 2) {
                return false;
            }

            // If we don't have a `/`, right will be undefined which is considered a valid step if we don't a `/`.
            return me.isValidRange(splits[0], start, stop) && me.isValidStep(splits[1]);
        });
    },

    hasValidSeconds: function (seconds) {
        return this.validateForRange(seconds, 0, 59);
    },

    hasValidMinutes: function (minutes) {
        return this.validateForRange(minutes, 0, 59);
    },

    hasValidHours: function (hours) {
        return this.validateForRange(hours, 0, 23);
    },

    hasValidDays: function (days, allowBlankDay) {
        return (allowBlankDay && this.isQuestionMark(days)) || this.validateForRange(days, 1, 31) || this.isLastWeekDayOfMonth(days) || this.isLastDayOfMonth(days) || this.isLastXdaysBeforeEndOfMonth(days, 1, 31) || this.isWeekdayNearDay(days, 1, 31);
    },


    hasValidMonths: function (months, alias) {
        var monthAlias = {
            jan: '1',
            feb: '2',
            mar: '3',
            apr: '4',
            may: '5',
            jun: '6',
            jul: '7',
            aug: '8',
            sep: '9',
            oct: '10',
            nov: '11',
            dec: '12'
        };
        // Prevents alias to be used as steps
        if (months.search(/\/[a-zA-Z]/) !== -1) {
            return false;
        }

        if (alias) {
            var remappedMonths = months.toLowerCase().replace(/[a-z]{3}/g, function (match) {
                return monthAlias[match] === undefined ? match : monthAlias[match];
            });
            // If any invalid alias was used, it won't pass the other checks as there will be non-numeric values in the months
            return this.validateForRange(remappedMonths, 1, 12);
        }

        return this.validateForRange(months, 1, 12);
    },


    hasValidWeekdays: function (weekdays, alias, allowBlankDay, allowSevenAsSunday) {

        var weekdaysAlias = {
            sun: '0',
            mon: '1',
            tue: '2',
            wed: '3',
            thu: '4',
            fri: '5',
            sat: '6'
        };
        // If there is a question mark, checks if the allowBlankDay flag is set
        if (allowBlankDay && this.isQuestionMark(weekdays)) {
            return true;
        } else if (!allowBlankDay && this.isQuestionMark(weekdays)) {
            return false;
        }

        if (this.isLastXWeekDayOfMonth(weekdays)) {
            return true;
        }
        // Prevents alias to be used as steps
        if (weekdays.search(/\/[a-zA-Z]/) !== -1) {
            return false;
        }

        if (alias) {
            var remappedWeekdays = weekdays.toLowerCase().replace(/[a-z]{3}/g, function (match) {
                return weekdaysAlias[match] === undefined ? match : weekdaysAlias[match];
            });
            // If any invalid alias was used, it won't pass the other checks as there will be non-numeric values in the weekdays
            return this.validateForRange(remappedWeekdays, 0, allowSevenAsSunday ? 7 : 6) || this.isXOccurrencyOfWeekly(weekdays);
        }

        return this.validateForRange(weekdays, 0, allowSevenAsSunday ? 7 : 6) || this.isXOccurrencyOfWeekly(weekdays);
    },

    hasCompatibleDayFormat: function (days, weekdays, allowBlankDay) {
        return !(allowBlankDay && this.isQuestionMark(days) && this.isQuestionMark(weekdays));
    },

    split: function (cron) {
        return cron.trim().split(/\s+/);
    },



    isValidCron: function (cron, options) {
        var defaultOptions = {
            alias: false,
            seconds: false,
            allowBlankDay: true,
            allowSevenAsSunday: false
        };
        options = Ext.applyIf(
            options || {},
            defaultOptions
        );

        var splits = this.split(cron);

        if (splits.length > (options.seconds ? 6 : 5) || splits.length < 5) {
            return false;
        }
        // We could only check the steps gradually and return false on the first invalid block,
        // However, this won't have any performance impact so why bother for now.
        var minutes = this.hasValidMinutes(splits[0]);
        var hours = this.hasValidHours(splits[1]);
        var days = this.hasValidDays(splits[2], options.allowBlankDay);
        var months = this.hasValidMonths(splits[3], options.alias);
        var weekDays = this.hasValidWeekdays(splits[4], options.alias, options.allowBlankDay, options.allowSevenAsSunday);
        var daysWeekDays = this.hasCompatibleDayFormat(splits[2], splits[4], options.allowBlankDay);

        return minutes || hours || days || months || weekDays || daysWeekDays;
    },

    taskCronValidation: function (vm, invalid) {
        if (vm.get('isAdvancedCron')) {
            var cronExpression = Ext.String.format('{0} {1} {2} {3} {4}',
                vm.get('advancedCronMinuteValue'),
                vm.get('advancedCronHourValue'),
                vm.get('advancedCronDayValue'),
                vm.get('advancedCronMonthValue'),
                vm.get('advancedCronDayofweekValue')
            );
            var cronValid = CMDBuildUI.util.administration.helper.CronValidatorHelper.isValidCron(cronExpression);
            if (!cronValid) {
                invalid.push({ name: CMDBuildUI.locales.Locales.administration.tasks.cron });
            }
        }
        return invalid;
    }
});