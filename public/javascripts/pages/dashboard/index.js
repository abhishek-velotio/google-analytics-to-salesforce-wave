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
	var Collections	= window.Collections;
	var Views 		= window.Views;
	
	var jobSettings = null;
	var job 		= null;

	
	/* MODEL */
	
	Models.JobSettings = Backbone.Model.extend({
		urlRoot : '/dashboard_job',
		defaults : {
			name : "",
			datasetName : "",
			dashboardType : "",
			salesforceProfile : null
		},

		validation : {
			name : [{
				required : true
			},{
				pattern: /^[a-zA-Z0-9]*$/,
				msg: 'Not allow special characters, including spaces'
			}],
			
			datasetName : {
				required : true
			},
			
			dashboardType : {
				required : true
			},
			
			salesforceProfile : {
				required : true
			}
		}
	});
	
	/* POPUP */

	Views.JobPopup = Views.Modal.extend({
		width : 780,
	
		initialize : function (options) {
			Views.Modal.prototype.initialize.call(this, options);
			_.bindAll(this, 'render', 'successSaving', 'errorSaving');
			Backbone.Validation.bind(this);
		},
		
		bindings: {
			'[name=name]': {
				observe: 'name',
				setOptions: {
					validate: true
				}
			},
			
			'[name=datasetName]': {
				observe: 'datasetName',
				setOptions: {
					validate: true
				}
			},
			
			'[name=dashboardType]' : {
				observe: 'dashboardType',
				setOptions: {
					validate: true
				},
				events: ['keyup', 'change', 'cut', 'paste', 'rendered', 'stickit'],
				getVal: function($el, event, options) {
					return $(event.target).val();
				}
			},
			
			'[name=salesforceProfile]': {
				observe: 'salesforceProfile',
				setOptions: {
					validate: true
				},
				events: ['keyup', 'change', 'cut', 'paste', 'rendered', 'stickit'],
				getVal: function($el, event, options) {
					return $(event.target).val();
				}
			}
		},


	    successSaving : function (model, response) {
			$('.content__main').prepend(new Views.Alert({
				typeAlert : 'success',
				title : '',
				text  : 'Job is created'
			}).el);

			this.model.trigger('change');
	    	Collections.Jobs.fetch()
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
		
		render : function () {
			Views.Modal.prototype.render.call(this);
			this.$el.find('.modal-body').append(new Views.JobForm().el);

			var view = this;
			this.model.on('change', function(){
				formState(view);
			});
			this.stickit();
			this.$el.find('.form-control').trigger('stickit');
			formState(this);

			return this;
		}
	});
	
	Views.JobForm = Backbone.View.extend({
		
		tagName : 'form',
		
		className : 'job-settings__form row',
		
		initialize : function (options) {
			this.options = options;
			this.render();
		},

		render : function () {
			
			this.JobNameView = new Views.Input({
				_id 		 	: 'name',
				title 	 		: 'Job name',
				type			: 'text',
				classes			: 'job-settings__name col-xs-12',
			});
			
			this.JobDatasetNameView = new Views.Input({
				_id 		 	: 'datasetName',
				title 	 		: 'Dataset name',
				type			: 'text',
				classes			: 'job-settings__dataset-name col-xs-12',
			});
			
			this.DashboarTypeView = new Views.Select({
				_id 		 	: 'dashboardType',
				title 	 		: 'Dashboard Type',
				classes			: 'job-settings__dashboard-type col-xs-12',
				multiple 		: false,
				groupped 		: false,
				collection 		: Collections.DashboardType
			});

			this.SalesforceProfilesView = new Views.Select({
				_id 		 	: 'salesforceProfile',
				title 	 		: 'Salesforce Profile',
				classes			: 'job-settings__salesforce-profile col-xs-12 no-label',
				multiple 		: false,
				groupped 		: false,
				collection 		: Collections.SalesforceProfiles
			});

			this.$el.append(this.JobNameView.el)
					.append(this.JobDatasetNameView.el)
					.append(this.DashboarTypeView.el);

			$('<div>', {'class':'panel panel-default clearfix'})
				.append('<div class="panel-heading">Salesforce Profile</div>')
				.append(this.SalesforceProfilesView.el)
				.appendTo(this.$el);

			return this;
		}
		
	});

	/* BUTTONS */

	Views.AddJobButton = Backbone.View.extend({
		
		className : 'button button_type_add btn btn-primary',
		
		tagName : 'button',
		
		events: {
			'click' : 'openSettings',
		},
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.render();
		},
		
		popup : null,
		
		openSettings : function () {
			
			job = new Models.JobSettings();
			
			this.popup = new Views.JobPopup({ 
					id   	: 'job-settings',
					title 	: 'Create job',
					classes : 'job-settings',
					model 	: job
			});
			
			this.popup.show();
			
			return this;
		},
		
		render : function () {
			this.$el.html("Add job");
		}
	});
	
	Views.RefreshButton = Backbone.View.extend({
		className : 'button button_type_refresh btn btn-primary',
		tagName : 'button',
		popup : null,
		events : {'click': 'refresh'},
		initialize : function () {
			_.bindAll(this, 'render');
			this.render();
		},
		refresh : function() {
			Collections.Jobs.fetch({reset: true})
		},
		render : function () {
			this.$el.html('<i class="fa fa-refresh"></i>');
		}
	});



	/* JOBS TABLE */

	Views.JobRow = Backbone.View.extend({
		
		tagName : 'tr',
		
		className : 'job table__row',
		
		model : Models.Job,
		
		events : {
			'click .job__edit-btn' 		 : 'edit', 
			'click .job__cancel-btn' 		: 'cancel',
			'click .job__delete-btn' 		: 'delete'
		},
		
		template : _.template($("#job").html()),
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.render();
		},
		
		edit : function() {
			//SLegostaev, this functional does not completed yet
			var popup = new Views.JobPopup({ title 	: 'Edit job', model : this.model });
			popup.show();
			return this;
		},
		
		cancel : function() {
			$.ajax({
				method : 'post',
				url : "/dashboard_job/cancel/" + this.model.get('id'),
				context: this
			}).done(function (data) {
				this.model.set(data);
				this.render();
			});
		},
		
		delete : function () {
			this.model.collection.remove(this.model);
			this.model.destroy();
			return this;
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		}
	});
	
	Views.Jobs = Views.Table.extend({
		
		headers : [ "ID", "Name", "Dataset Name", "Dashboard Type", "Salesforce Profile", "Status", "User", "Actions" ],
		
		render : function () {
			
			Views.Table.prototype.render.call(this);

			this.collection.each(function (job) {
				$(this.$el.find('thead th')[5]).addClass('table_align_center');

				this.$el
					.find('tbody')
					.append(new Views.JobRow({ model: job }).el);
			}, this);
			
			return this;
		}
	});
	
	var jobs = new Views.Jobs({ 
		collection  : Collections.Jobs,
		classes 	: 'jobs'
	});
	
	var addJob = new Views.AddJobButton();
	var refreshButton = new Views.RefreshButton();
	
	$('.content__main').append(refreshButton.el).append(addJob.el).append(jobs.el);
	
	
});