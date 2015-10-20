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
			highlightErrors(this, JSON.parse(response.responseText));
		},

		save : function (event) {
			event.preventDefault();
	    	if (this.model.isValid(true)) {
    			this.model.save(null, { success : this.successSaving, error : this.errorSaving });
    			this.$el.find('.button_type_save').attr('disabled', true);
	    	}
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()));

			var view = this;
			this.model.on('change', function(){
console.log('------------- MODEL CHANGED --------------');
				formState(view);
			});
			this.stickit();
			formState(this);
			return this;
		}
	});
	
	var profile = new Views.Profile({ model: new Models.Profile() });

	$('.content__main').append(profile.el);
	
});