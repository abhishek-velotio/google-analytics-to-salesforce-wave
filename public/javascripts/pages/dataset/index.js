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
		urlRoot : '/job',
		defaults : {
			name : "",
			googleProfile : null,
			googleAnalyticsProperties_profile : null,
			googleAnalyticsProperties_dimensions : null,
			googleAnalyticsProperties_metrics : null,
			googleAnalyticsProperties_startDate : "",
			googleAnalyticsProperties_endDate : "",
			salesforceProfile : null,
			startTime : null,
			repeatPeriod : null,
			includePreviousData : null
		},

		validation : {
			name : [{
				required : true
			},{
				pattern: /^[a-zA-Z0-9]*$/,
				msg: 'Not allow special characters, including spaces'
			}],
			googleProfile : {
				required : true
			},
			googleAnalyticsProperties_profile : {
				required : true
			},
			googleAnalyticsProperties_dimensions : {
				required : true,
				limit : 7
			},
			googleAnalyticsProperties_metrics : {
				required : true,
				limit : 10
			},
			googleAnalyticsProperties_startDate : {
				required : true
			},
			googleAnalyticsProperties_endDate : {
				required : true
			},
			salesforceProfile : {
				required : true
			}
		}
	});


	/* FORM COMPONENTS */

	Views.Accounts = Views.Select.extend({ // emulate DependSelect
		
		initialize : function (options) {
			Views.Select.prototype.initialize.call(this, options);
			this.bind("googleProfile.change", this.loadData);
		},

		loadData : function (profileId) {
			var self = this;
			this.collection.fetch({ 
				url : '/google/profile/'+ profileId + '/accounts',
				reset: true,
				success : function () {
					self.changeDependSelects(profileId);
				}
			});
		},
		
		changeDependSelects : function(profileId) {
			var dependSelects = this.options.dependSelects;
			_.each(dependSelects, function (select) {
				select.trigger(this.options._id + '.change', profileId, this.$el.find('select').val());
			}, this);
		},
		
		change : function () {
			this.changeDependSelects($('#googleProfile').val());
		}
		
	});
	
	Views.Properties = Views.Select.extend({
		
		initialize : function (options) {
			Views.Select.prototype.initialize.call(this, options);
			this.bind("googleAnalyticsProperties_account.change", this.loadData);
		},
		
		loadData : function (profileId, accountId) {
			var self = this;
			this.collection.fetch({ 
				url : '/google/profile/'+ profileId + '/properties/' + accountId,
				reset: true,
				success : function () {
					self.changeDependSelects(profileId, accountId);
				}
			});
		},
		
		changeDependSelects : function (profileId, accountId) {
			var dependSelects = this.options.dependSelects;
			_.each(dependSelects, function (select) {
				select.trigger(this.options._id + '.change',  profileId, accountId, this.$el.find('select').val());
			}, this);
		},

		change : function () {
			this.changeDependSelects($('#googleProfile').val(), $('#googleAnalyticsProperties_account').val());
		}
	});
	
	Views.Profiles = Views.Select.extend({

		initialize : function (options) {
			Views.Select.prototype.initialize.call(this, options);
			this.bind("googleAnalyticsProperties_property.change", this.loadData);
		},
		
		loadData : function (profileId, accountId, propertyId) {
			var self = this;
			this.collection.fetch({
				url : '/google/profile/'+ profileId + '/profiles/' + accountId + "/" + propertyId,
				reset: true
			});
		}
	});



	Views.Keys = Views.Select.extend({
		
		initialize : function (options) {
			Views.Select.prototype.initialize.call(this, options);
			this.bind("googleProfile.change", this.loadData);
//			this.collection.unbind('all', this.render);
//			this.collection.bind('sync', this.render);
		},
		
		loadData : function (profileId) {
			this.collection.fetch({ 
				url : '/google/profile/'+ profileId + '/' + this.options.keyName,
				reset: true
			});
		},
		
		render : function () {
			
			Views.Select.prototype.render.call(this);
			
			this.$el.find('select').select2({
				placeholder: "Select " + this.options.title.toLowerCase(),
				templateResult : function (state) {
					if (!state.id) return state.text;
					var $state = $(
						'<span>' + state.text + '</span><i class="source-value">' + state.element.value + '</i>'
					);
					return $state;
				} 
			})
			.on('change', null, this, function (event) {
				
				var self = event.data;
				var values = $(event.currentTarget).val() || [];
				var data = _.filter(self.collection.toJSON(), function (item) { return values.indexOf(item.id) !== -1; });
				
				self.$el.trigger('change:keys', { keys: data, id: self.options.keyName });
			});
			
			return this;
		}
	});
	
	Views.Sorter = Views.Select.extend({
		
		metrics : null,
		
		dimensions : null,
		
		initialize : function (options) {
			
			Views.Select.prototype.initialize.call(this, options);
			
			_.bindAll(this, 'render', 'update');
			
			this.collection.bind('reset', this.update);
			
			_.each(this.options.listenSelects, function (select) {
				select.$el.on('change:keys', null, this, function (event, data) { 
					
					var self = event.data;
					
					if (data.id === 'metrics') self.metrics = data.keys;
					else self.dimensions = data.keys;
					
					self.collection.reset(_.union(self.metrics, self.dimensions));
					
				});
			}, this);
		},
		
		update : function () {
			this.$el.find('select').select2('data', this.collection.toJSON());
		},
		
		render : function () {
			
			Views.Select.prototype.render.call(this);
			
			this.$el.find('select').select2({
				placeholder: "Select " + this.options.title.toLowerCase(),
				templateResult : function (state) {
					if (!state.id) return state.text;
					var $state = $(
						'<span>' + state.text + '</span><i class="source-value">' + state.element.value + '</i>'
					);
					return $state;
				} 
			});
			
			return this;
		}
	});
	
	Views.Scheduler = Backbone.View.extend({
		
		className : 'scheduler form-group job-settings__scheduler col-xs-12',
		
		attributes : {
			role : 'tabpanel'
		},
		
		template : _.template($("#scheduler").html()),
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template());
			
			this.$el.find('.scheduler__time')
				.datetimepicker({
					defaultDate: moment(),
					icons: {
						time : "fa fa-clock-o",
		                date : "fa fa-calendar",
		                up   : "fa fa-arrow-up",
		                down : "fa fa-arrow-down",
		                previous: 'fa fa-chevron-left',
		                next: 'fa fa-chevron-right',
		                today: 'fa fa-crosshairs',
		                clear: 'fa fa-trash'
		            }
				});
			
			return this;
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
			'[name=googleProfile]': {
				observe: 'googleProfile',
				setOptions: {
					validate: true
				},
				events: ['keyup', 'change', 'cut', 'paste', 'rendered', 'stickit'],
				getVal: function($el, event, options) {
					return $(event.target).val();
				}
			},
			'[name=googleAnalyticsProperties_profile]': {
				observe: 'googleAnalyticsProperties_profile',
				setOptions: {
					validate: true
				},
				events: ['keyup', 'change', 'cut', 'paste', 'rendered', 'stickit'],
				getVal: function($el, event, options) {
					return $(event.target).val();
				}
			},
			'[name=googleAnalyticsProperties_metrics]': {
				observe: 'googleAnalyticsProperties_metrics',
				setOptions: {
					validate: true
				},
				events: ['change', 'prechange', 'rendered', 'stickit'],
				getVal: function($el, event, options) {
					if (event.type == 'change') $(event.target).trigger('prechange');
console.log(event.type);
					return $(event.target).val();
				}
			},
			'[name=googleAnalyticsProperties_dimensions]': {
				observe: 'googleAnalyticsProperties_dimensions',
				setOptions: {
					validate: true
				},
				events: ['change', 'prechange', 'rendered', 'stickit'],
				getVal: function($el, event, options) {
					if (event.type == 'change') $(event.target).trigger('prechange');
					return $(event.target).val();
				}
			},
			'[name=googleAnalyticsProperties_startDate]': {
				observe: 'googleAnalyticsProperties_startDate',
				events: ['keyup', 'change', 'cut', 'paste', 'dp.change', 'dp.hide', 'blur'],
				setOptions: {
					validate: true
				}
			},
			'[name=googleAnalyticsProperties_endDate]': {
				observe: 'googleAnalyticsProperties_endDate',
				events: ['keyup', 'change', 'cut', 'paste', 'dp.change', 'dp.hide', 'blur'],
				setOptions: {
					validate: true
				}
			},
			'[name=googleAnalyticsProperties_sorting]': {
				observe: 'googleAnalyticsProperties_sorting',
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
		
		format : function (data) {
			var type = $('.job-settings__scheduler .nav li.active a').attr('href').replace('#', '');
			switch (type) {
				case "delayed": 
					data.startTime = !!(data.delayStart) ? moment(data.delayStart).valueOf() : null;
					break;
				case "repeated": 
					data.startTime = !!(data.repeatStart) ? moment(data.repeatStart).valueOf() : null;
					data.repeatPeriod = data.period;
					data.includePreviousData = data.previousData;
					break;
				default : 
					data.startTime = null; 
					break;
			}

			return data;
		},
		
		save : function () {
			this.model.set(this.format($('.job-settings__form').serializeObject()), { silent : true });
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
console.log('------------- MODEL CHANGED --------------');
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
			
			_.bindAll(this, 'render', 'parseInputDate');
			
			this.options = options;
			this.render();
		},
		
		parseRelativeDate : function (relativeDate) {
			switch (relativeDate) {
				case 'today' : return moment()
				case 'yesterday' : return moment().subtract(1, 'day');
				default : return moment().subtract(Number(relativeDate.replace("days ago", "").trim()), 'days');
			}
		},
		
		parseInputDate : function (inputDate) {
			var relativeDatePattern = /today|yesterday|[0-9]+\s+(days ago)/,
				resultDate;
			
			if (moment.isMoment(inputDate) || inputDate instanceof Date) {
                resultDate = moment(inputDate);
            } else {
            	var relativeDate = inputDate.match(relativeDatePattern),
            		parseDate = null;
            	
            	if (relativeDate !== null) parseDate = this.parseRelativeDate(inputDate.match(relativeDatePattern)[0]);
            	else parseDate = moment();
            	
            	resultDate = moment(parseDate, "YYYY-MM-DD");
            }
			
			return resultDate;
		},
		
		render : function () {
			
			this.JobNameView = new Views.Input({
				_id 		 	: 'name',
				title 	 		: 'Job name',
				type			: 'text',
				classes			: 'job-settings__name col-xs-12',
			});


			this.ProfilesView = new Views.Profiles({
				_id 		 	: 'googleAnalyticsProperties_profile',
				title 	 		: 'Analytics Profile',
				classes			: 'job-settings__analytics-profile col-xs-4',
				multiple 		: false,
				groupped 		: false,
				collection 		: new Backbone.Collection()
			});

			this.PropertiesView = new Views.Properties({
				_id 		 	: 'googleAnalyticsProperties_property',
				title 	 		: 'Property',
				classes			: 'job-settings__property col-xs-4',
				multiple 		: false,
				groupped 		: false,
				collection 		: new Backbone.Collection(),
				dependSelects	: [this.ProfilesView]
			});

			this.AccountsView = new Views.Accounts({
				_id 		 	: 'googleAnalyticsProperties_account',
				title 	 		: 'Account',
				classes			: 'job-settings__account col-xs-4',
				multiple 		: false,
				groupped 		: false,
				collection 		: new Backbone.Collection(),
				dependSelects	: [this.PropertiesView]
			});


			this.MetricsView = new Views.Keys({
				_id 		 	: 'googleAnalyticsProperties_metrics',
				title 	 		: 'Metrics',
				classes			: 'job-settings__metrics col-xs-6',
				multiple 		: true,
				groupped 		: true,
				groupField		: 'group',
				keyName			: 'metrics',
				collection 		: new Backbone.Collection()
			});

			this.DimensionsView = new Views.Keys({
				_id 		 	: 'googleAnalyticsProperties_dimensions',
				title 	 		: 'Dimensions',
				classes			: 'job-settings__dimensions col-xs-6',
				multiple 		: true,
				groupped 		: true,
				groupField		: 'group',
				keyName			: 'dimensions',
				collection 		: new Backbone.Collection()
			});

			this.EndDateView = new Views.Input({
				_id 		 	: 'googleAnalyticsProperties_endDate',
				title 	 		: 'End date',
				type			: 'text',
				classes			: 'job-settings__end-date col-xs-4',
			});

			this.StartDateView = new Views.Input({
				_id 		 	: 'googleAnalyticsProperties_startDate',
				title 	 		: 'Start date',
				type			: 'text',
				classes			: 'job-settings__start-date col-xs-4',
			});

			this.Sorter = new Views.Sorter({
				_id 		 	: 'googleAnalyticsProperties_sorting',
				title 	 		: 'Sorting',
				classes			: 'job-settings__sorting col-xs-4',
				multiple 		: true,
				groupped 		: false,
				listenSelects	: [this.MetricsView, this.DimensionsView],
				collection 		: new Backbone.Collection()
			});


			this.GoogleProfilesView = new Views.DependSelect({
				_id 		 	: 'googleProfile',
				title 	 		: 'Google Profile',
				classes			: 'job-settings__google-profile col-xs-12 no-label',
				multiple 		: false,
				groupped 		: false,
				collection 		: Collections.GoogleProfiles,
				dependSelects	: [this.AccountsView, this.MetricsView, this.DimensionsView],
				changeAfterInit : true
			});
			
			this.SalesforceProfilesView = new Views.Select({
				_id 		 	: 'salesforceProfile',
				title 	 		: 'Salesforce Profile',
				classes			: 'job-settings__salesforce-profile col-xs-12 no-label',
				multiple 		: false,
				groupped 		: false,
				collection 		: Collections.SalesforceProfiles,
			});

			
			this.Scheduler = new Views.Scheduler();

			
			$(this.StartDateView.el).find('input')
				.datetimepicker({
					defaultDate: moment(),
					format: "YYYY-MM-DD",
					parseInputDate : this.parseInputDate,
					keepInvalid: true,
					keyBinds : {
						t : null
					},
					icons: {
						time : "fa fa-clock-o",
		                date : "fa fa-calendar",
		                up   : "fa fa-arrow-up",
		                down : "fa fa-arrow-down",
		                previous: 'fa fa-chevron-left',
		                next: 'fa fa-chevron-right',
		                today: 'fa fa-crosshairs',
		                clear: 'fa fa-trash'
		            }
				});

			$(this.EndDateView.el).find('input')
				.datetimepicker({
					defaultDate: moment(),
					format: "YYYY-MM-DD",
					parseInputDate : this.parseInputDate,
					keepInvalid: true,
					keyBinds : {
						t : null
					},
					icons: {
		                time : "fa fa-clock-o",
		                date : "fa fa-calendar",
		                up   : "fa fa-arrow-up",
		                down : "fa fa-arrow-down",
		                previous: 'fa fa-chevron-left',
		                next: 'fa fa-chevron-right',
		                today: 'fa fa-crosshairs',
		                clear: 'fa fa-trash'
		            }
				});


			this.$el.append(this.JobNameView.el);

			$('<div>', {'class':'panel panel-default clearfix'})
				.append('<div class="panel-heading">Google Profile</div>')
				.append(this.GoogleProfilesView.el)
				.append(this.AccountsView.el)
				.append(this.PropertiesView.el)
				.append(this.ProfilesView.el)
				.append('<div class="clear"></div>')
				.append(this.MetricsView.el)
				.append(this.DimensionsView.el)
				.append('<div class="clear"></div>')
				.append(this.StartDateView.el)
				.append(this.EndDateView.el)
				.append(this.Sorter.el)
				.appendTo(this.$el);

			$('<div>', {'class':'panel panel-default clearfix'})
				.append('<div class="panel-heading">Salesforce Profile</div>')
				.append(this.SalesforceProfilesView.el)
				.appendTo(this.$el);

			$('<div>', {'class':'panel panel-default clearfix'})
				.append('<div class="panel-heading">Scheduler</div>')
				.append(this.Scheduler.el)
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
			
			if (Collections.GoogleProfiles.isEmpty() || Collections.SalesforceProfiles.isEmpty()) {
				BootstrapDialog.alert({
		            title: 'Error',
		            message: "You don't have google or salesforce profiles",
		            type: BootstrapDialog.TYPE_DANGER,
		        });
				return;
			}
			
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
				url : "/job/cancel/" + this.model.get('id'),
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
		
		headers : [ "ID", "Name", "Google Profile", "Salesforce Profile", "Start date", "Status", "User", "Actions" ],
		
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