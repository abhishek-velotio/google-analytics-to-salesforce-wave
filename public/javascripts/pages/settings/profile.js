$(function () {
	
	var Models 		= window.Models;
	var Collections = window.Collections;
	var Views 		= window.Views;

	
	Views.Profile = Backbone.View.extend({
		tagName : 'form',
		
		className : 'user-form',
		
		model : Models.Profile,
		
		template : _.template($("#profile").html()),
		
		events: {
			'click .button_type_save' : 'save'
		},

		bindings: {
			'[name=firstName]': {
				observe: 'firstName',
				setOptions: {
					validate: true
				}
			},
			'[name=lastName]': {
				observe: 'lastName',
				setOptions: {
					validate: true
				}
			},
			'[name=username]': {
				observe: 'username',
				setOptions: {
					validate: true
				}
			},
			'[name=emailAddress]': {
				observe: 'emailAddress',
				setOptions: {
					validate: true
				}
			},
			'[name=password]': {
				observe: 'password',
				setOptions: {
					validate: true
				}
			}
		},

		initialize : function () {
			_.bindAll(this, 'render', 'successSaving', 'errorSaving');
			Backbone.Validation.bind(this);
			this.render();
		},

	    successSaving : function (model, response) {
	    	this.model.trigger('change');

			$('.content__main').prepend(new Views.Alert({
				typeAlert : 'success',
				title : '',
				text  : 'Data saved successfully.'
			}).el);
		},
		
		errorSaving : function (model, response) {
			this.highlightErrors(JSON.parse(response.responseText));
		},

		save : function (event) {

			event.preventDefault();
			
			var data = this.$el.serializeObject()
			
			this.model.set(data, { silent : true });
	    	
	    	if (this.model.isValid(true)) {
    			this.model.save(null, { success : this.successSaving, error : this.errorSaving });
    			this.$el.find('.button_type_save').attr('disabled', true);
	    	}
			
			return this;
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()));
			this.stickit();
			formState(this);
			return this;
		}
	});
	
	var profile = new Views.Profile({ model: new Models.Profile() });

	$('.content__main').append(profile.el);
	
});