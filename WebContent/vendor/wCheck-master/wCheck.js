(function($) {
    
    function Check(el, options) {
        this.$el = $(el);
        this.options = options;

        this.mode = this.$el.prop('type').toLowerCase();
        this.name = '';
        this.checked = false;
        this.focus = false;

        this.generate();
    }
    
    Check.prototype = {
        generate: function() {
            if (!this.$check) {
                var _self = this;

                if (this.mode ===  'radio') {
                    this.name = this.$el.attr('name');
                }

                this.$check = $('<div class="wCheck wCheck-off wCheck-mode-' + this.mode + ' ' + (this.mode === 'radio' && this.name ? 'wCheck-name-' + this.name : '') + '"></div>');
                this.$selector = $('<div class="wCheck-selector"></div>');
                this.$disabled = $('<div class="wCheck-disabled"></div>').hide();
                this.$focus = $('<div class="wCheck-focus"></div>').hide();
                this.$check.append(this.$selector).append(this.$focus);
                this.$el.addClass('wCheck-el').hide().after(this.$check);
                this.$check.append(this.$el.show().css({opacity:'0'})).append(this.$disabled);

                
                this.$el
                .change(function() { _self.onChange(); })
                .focus(function() {
                    if (_self.focus) { _self.$focus.show(); }
                })
                .blur(function() { 
                    if(_self.focus) { _self.$focus.hide(); }
                });

                this.$check
                .hover(
                    function() { _self.onFocus(); },
                    function() { _self.onBlur(); }
                );

                if (this.$el.prop('checked')) { this._check(true); }

                this.createLabel(); // make sure this is run before setTheme()
                this.setTheme(this.options.theme);
                this.setSelector(this.options.selector);
                this.setDisabled(this.options.disabled || this.$el.prop('disabled'));

                // focus is only triggered after `tab` key is pressed: seems to be the way the browser does it
                $(document).keydown(function(e){
                    _self.focus = true;
                });
            }
            return this.$check;
        },

        createLabel: function() {
            var text = this.$el.attr('data-label'),
                id = this.$el.attr('id'),
                _self = this;

            if (text && text !== '') {
                id = id || Math.random() * 100;

                this.$el.attr('id', id);
                this.$label = $('<label for="' + id + '" class="wCheck-label"></label>');
                this.setLabel(text);
                this.$check.after(this.$label);
            }
            else if (this.options.useExistingLabel && id) {
                this.$label = $('label[for="' + id + '"]').addClass('wCheck-label');
            }

            if (this.$label) {
                this.$label.hover(
                    function(e){ _self.onFocus(); },
                    function(e){ _self.onBlur(); }
                );
            }
        },

        onChange: function() {
            this._check(this.$el.is(':checked'));
        },

        onFocus: function() {
            if (!this.options.disabled) {
                this.$check.addClass('wCheck-hover');
            }
            
            if (this.$label && this.options.highlightLabel && !this.options.disabled) {
                this.$label.addClass('wCheck-label-hover');
            }
        },

        onBlur: function() {
            this.$check.removeClass('wCheck-hover');

            if (this.$label && this.options.highlightLabel) {
                this.$label.removeClass('wCheck-label-hover');
            }
        },

        setCheck: function(checked) {
            this._check(checked);
            this.$el.change();
        },

        setLabel: function(text) {
            this.$label.html(text);
        },

        setTheme: function(theme) {
            var i, ii;

            theme = theme.split(' ');

            this.$check.attr('class', (this.$check.attr('class') || '').replace(/\s?wCheck-theme-(\S*)\s?/, ''));
            for (i = 0, ii = theme.length; i < ii ; i++) {
                this.$check.addClass('wCheck-theme-' + theme[i]);
            }

            if (this.$label) {
                this.$label.attr('class', (this.$label.attr('class') || '').replace(/\s?wCheck-label-theme-(\S*)\s?/, ''));
                for (i = 0, ii = theme.length; i < ii ; i++) {
                    this.$label.addClass('wCheck-label-theme-' + theme[i]);
                }
            }
        },

        setSelector: function(selector) {
            this.$selector.attr('class', this.$selector.attr('class').replace(/wCheck-selector-.+\s|wCheck-selector-.+$/, ''));
            this.$selector.addClass('wCheck-selector-' + selector);
        },

        setDisabled: function(disabled) {
            this.options.disabled = disabled;
            this.$el.prop('disabled', disabled);
            this.$disabled[(disabled ? 'show' : 'hide')]();
            
            if (this.$label) {
                this.$label[(disabled ? 'add' : 'remove') + 'Class']('wCheck-label-disabled');
            }
        },

        _check: function(checked) {
            this.checked = checked;

            if (this.mode === 'radio') {
                $('.wCheck-name-' + this.name).removeClass('wCheck-on').addClass('wCheck-off');
            }

            if (this.checked) {
                this.$check.removeClass('wCheck-off').addClass('wCheck-on');
                this.$el.prop('checked', true);
            }
            else {
                this.$check.removeClass('wCheck-on').addClass('wCheck-off');
                this.$el.prop('checked', false);
            }
        },
    };
    
    $.fn.wCheck = function(options, value) {
        if (typeof options === 'string') {
            var values = [];
            var elements = this.each(function() {
                var wCheck = $(this).data('wCheck');

                if (wCheck) {
                    var func = (value !== undefined ? 'set' : 'get') + options.charAt(0).toUpperCase() + options.substring(1).toLowerCase();

                    if (wCheck[options]) {
                        wCheck[options].apply(wCheck, [value]);
                    } else if (value !== undefined) {
                        if (wCheck.options[options]) { wCheck.options[options] = value; }
                        if (wCheck[func]) { wCheck[func].apply(wCheck, [value]); }
                    } else {
                        if(wCheck[func]) { values.push(wCheck[func].apply(wCheck, [value])); }
                        else if (wCheck.options[options]) { values.push(wCheck.options[options]); }
                        else { values.push(null); }
                    }
                }
            });

            if (values.length === 1) { return values[0]; }
            else if (values.length > 0) { return values; }
            else { return elements; }
        }

        options = $.extend({}, $.fn.wCheck.defaults, options);
        
        function get(el) {
            var wCheck = $.data(el, 'wCheck');
            if (!wCheck) {
                var _options = jQuery.extend(true, {}, options);
                wCheck = new Check(el, _options);
                $.data(el, 'wCheck', wCheck);
            }

            return wCheck;
        }

        return this.each(function() { get(this); });
    };

    $.fn.wCheck.defaults = {
        theme: 'square-classic blue',   // theme
        selector: 'checkmark',          // selector
        disabled: false,                // toggle enabled/disabled
        useExistingLabel: true,         // if there is a for="id" matching use it
        highlightLabel: false           // toggle highlighting active/hover label
    };

    $.fn.wRadio = function(options, value) {
        if (typeof options !== 'string') {
            options = $.extend({}, $.fn.wRadio.defaults, options);
        }

        return $.fn.wCheck.apply(this, [options, value]);
    };
    
    $.fn.wRadio.defaults = {
        theme: 'circle-classic blue',   // theme
        selector: 'circle-dot-blue',    // selector
        disabled: false,                // toggle enabled/disabled
        useExistingLabel: true,         // if there is a for="id" matching use it
        highlightLabel: false           // toggle highlighting active/hover label
    };
    
})(jQuery);
