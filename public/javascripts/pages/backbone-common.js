/**
 * This document is a part of the source code and related artifacts
 * for GA2SA, an open source code for Google Analytics to 
 * Salesforce Analytics integration.
 *
 * Copyright © 2015 Cervello Inc.,
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

$(function () {
	if (!window.Models) 		window.Models = {};
	if (!window.Collections) 	window.Collections = {};
	if (!window.Views) 			window.Views = {};
	
	var Models 		= window.Models,
		Collections = window.Collections,
		Views		= window.Views;
	
	Views.Input = Backbone.View.extend({
		
		className : 'form-group',
		
		template : _.template($("#input").html()),
		
		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.$el.addClass(this.options.classes);
			this.render();
		},
		
		render : function () {
			
			this.$el.html(this.template({ 
				id 	 	: this.options._id,
				title 	: this.options.title,
				type	: this.options.type
			}));
			
			return this;
		}
	});
	
	Views.Select = Backbone.View.extend({
		
		className : 'form-group',
		
		template : _.template($("#select").html()),
		
		events : {
			'change select' : 'change'
		},
		
		data : null,
		
		initialize : function (options) {
			
			_.bindAll(this, 'render', 'change');
			
			this.options = options;
			this.collection.bind('reset', this.render);
			this.render();
		},
		
		change : function () {
			return this;
		},
		
		render : function () {
			this.$el.removeClass().addClass(this.className).addClass(this.options.classes);

			this.data = this.options.groupped ? _.groupBy(this.collection.toJSON(), this.options.groupField) : this.collection.toJSON();
			this.$el.html(this.template({ 
				id 		 : this.options._id,
				title 	 : this.options.title,
				multiple : this.options.multiple,
				groupped : this.options.groupped,
				items	 : this.data
			}));
			this.$el.find('select').trigger('rendered');

			if (this.options.changeAfterInit) this.change();
			
			return this;
		}
		
	});
	
	Views.DependSelect = Views.Select.extend({
		change : function () {
			var dependSelects = this.options.dependSelects;
			_.each(dependSelects, function (select) {
				select.trigger(this.options._id + '.change', this.$el.find('select').val());
			}, this);
		}
	});
	
	Views.Table = Backbone.View.extend({
		
		tagName : 'table',
		
		className : 'table',
		
		template : _.template($("#table").html()),
		
		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.collection.bind('add remove reset', this.render);
			this.$el.addClass(this.options.classes);
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template({ headers : this.headers }));			
			return this;
		}
		
	});
	
	
	Views.Modal = Backbone.View.extend({
		
		className : 'modal fade',
		
		template : _.template($("#modal").html()),
		
		events : {
	      'hidden.bs.modal' : 'teardown',
	      'shown.bs.modal'	: 'showComplete',
	      'click .button_type_save' : 'save',
	      'click .button_type_cancel, .close' : 'destroy'
	    },

		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.$el.addClass(this.options.classes);
			this.render();
		},
		
		show : function() {
			this.$el.modal('show');
	    },
	    
	    showComplete : function () {
	    	this.$el.find('.form-group:first-child .form-control').focus();
	    },

	    teardown : function() {
	    	//this.$el.data('modal', null);
	    	this.destroy();
	    },
	    
	    save : function () {
	    	this.$el.modal('hide');
	    	//this.destroy();
	    },
		
		render : function () {
			this.$el.html(this.template({ 
				title : this.options.title
			}));
			this.$el.modal({show:false});

			if (this.width !== undefined)
				this.$el.find('.modal-dialog').css({
					'width':'auto',
					'max-width':this.width+'px'
				});
		},
		
		destroy : function() {
			// COMPLETELY UNBIND THE VIEW
		    this.undelegateEvents();
		    this.$el.removeData().unbind(); 
		    // Remove view from DOM
		    this.remove();  
		    Backbone.View.prototype.remove.call(this);
		}
	    
	});
	
	Views.Alert = Backbone.View.extend({
		
		className : 'alert',
		
		template : _.template($("#alert").html()),

		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.$el.addClass('alert-' + this.options.typeAlert);
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template({ 
				title : this.options.title,
				text : this.options.text
			}));
		}
	});
})


Backbone.Validation.configure({
    forceUpdate: true
});

_.extend(Backbone.Validation.validators, {
	limit: function(value, attr, customValue, model) {
		if ($.isArray(value) && value.length > customValue) {
			return 'You have exceeded the number of elements in array';
		}
	}
});

_.extend(Backbone.Validation.callbacks, {
    valid: function (view, attr, selector) {
//console.dir(view.model);
console.log('VALID: '+attr);
		if (view.model.hasChanged(attr)) {
			hideError(view.$('[name="' + attr + '"]'));
//			formState(view);
		}
    },
    invalid: function (view, attr, error, selector) {
//console.dir(view.model);
console.log('INVALID: '+attr);
		if (view.model.hasChanged(attr)) {
			showError(view.$('[name="' + attr + '"]'), error);
//			formState(view);
		}
	}
});

function showError(el, message) {
	$group = el.closest('.form-group');
	$group.addClass('has-error');

	if ($group.find('.help-block').size() > 0)
		$group.find('.help-block').html(message);
	else
		$('<span>', {'class':'help-block'}).html(message).appendTo($group);
}
function hideError(el) {
	$group = el.closest('.form-group');
	$group.removeClass('has-error');
	$group.find('.help-block').remove();
}

function highlightErrors(view, errors) {
   	_.each(errors, function (message, attr) {
		showError(view.$('[name="' + attr + '"]'), message);
	});
}

function formState(view) {
	view.$el.find('.button_type_save').prop('disabled', !(view.model.isValid() && view.model.hasChanged()) );
}
