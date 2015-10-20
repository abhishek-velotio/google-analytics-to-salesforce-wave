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

$(function() {
	
	var Models 		= window.Models;
	var Collections = window.Collections;
	var Views 		= window.Views;

	Views.Profiles = Views.Table.extend({
		
		headers : [ "Name", "Actions" ],
				
		render : function () {
			
			Views.Table.prototype.render.call(this);
			
			this.collection.each(function (profile) {
				this.$el
					.find('tbody')
					.append(new Views.ProfileRow({ model: profile }).el);
			}, this);
			
			return this;
		}
		
	});
	
	Views.ProfileRow = Backbone.View.extend({
		
		tagName : 'tr',
		
		className : 'profile table__row',
		
		model : Models.Profile,
		
		apiSettings : Models.ApiSettings,
		
		template : _.template($("#profile").html()),
		
		events: {
			'click .profile__edit-btn' 		: 'edit',
			'click .profile__delete-btn' 	: 'delete'
		},
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.model.bind('change', this.render);
			this.render();
		},
		
		edit : function () {
			var popup = new Views.ProfileSettings({ model : this.model.clone(), title : 'Edit profile' });
			popup.show();
		},
		
		delete : function () {
			this.model.collection.remove(this.model);
			this.model.destroy();
			this.$el.remove();
			return this;
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		}
	});
	
	Views.AddProfile = Backbone.View.extend({
		
		tagName : 'button',
		
		className : 'button button_type_add btn btn-primary',
		
		events: {
			'click' : 'openSettings',
		},
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.render();
		},
		
		openSettings : function () {
			var popup = new Views.ProfileSettings({ model : new Models.Profile(), title : "Add profile" });
			popup.show();
			return this;
		},
		
		render : function () {
			this.$el.html("Add profile");
		}
	});
	
	Views.SalesforceProfileForm = Backbone.View.extend({
		
		tagName : 'form',
		
		className : 'profile-settings__form',
		
		template : _.template($("#profile-form").html()),
		
		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()))
		}
	});
	
	Views.ProfileSettings = Views.Modal.extend({
		
		initialize : function (options) {
			Views.Modal.prototype.initialize.call(this, options);
			_.bindAll(this, 'render', 'successSaving', 'errorSaving');
			Backbone.Validation.bind(this);
		},

		bindings: {
			'[name=name]': {
				observe: 'name',
				setOptions: {
//					silent: true,
					validate: true
				},
				updateView: false
			},
			'[name=username]': {
				observe: 'username',
				setOptions: {
//					silent: true,
					validate: true
				}
			},
			'[name=accountType]': {
				observe: 'accountType',
				setOptions: {
//					silent: true,
					validate: true
				},
				events: ['change'/*, 'click'*/]
			},
			'[name=password]': {
				observe: 'password',
				setOptions: {
//					silent: true,
					validate: true
				}
			},
			'[name=applicationName]': {
				observe: 'applicationName',
				setOptions: {
//					silent: true,
					validate: true
				}
			}
		},
	    
	    successSaving : function (model, response) {
			$('.content__main').prepend(new Views.Alert({
				typeAlert : 'success',
				title : '',
				text  : 'Data saved successfully.'
			}).el);

	    	this.model.trigger('change');
			Collections.Profiles.add(this.model, {merge: true});
			Views.Modal.prototype.save.call(this);
		},
		
		errorSaving : function (model, response) {
			highlightErrors(this, JSON.parse(response.responseText));
		},
		
		save : function () {
	    	if (this.model.isValid(true)) {
    			this.model.save(null, { success : this.successSaving, error : this.errorSaving });
    			this.$el.find('.button_type_save').attr('disabled', true);
	    	}
		},
		
		teardown : function() {
			Views.Modal.prototype.teardown.call(this);
	    	this.remove();
	    },
		
		render : function () {
			Views.Modal.prototype.render.call(this);
			this.$el.find('.modal-body').append(new Views.SalesforceProfileForm({ model : this.model }).el);


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
	
	var profiles = new Views.Profiles({collection: Collections.Profiles});
	
	var addProfile = new Views.AddProfile();
	
	$('.content__main').append(addProfile.el);
	$('.content__main').append(profiles.el);
	
});
