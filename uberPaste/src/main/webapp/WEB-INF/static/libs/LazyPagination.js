/*
---

script: LazyPagination.js

description: Automatically sends ajax requests as the user scrolls an element.

license: MIT-style license.

authors: Ryan Florence [rpflorence@gmail.com, http://ryanflorence.com]

docs: http://moodocs.net/rpflo/mootools-rpflo/LazyPagination

requires:
 core/1.2.4:
  - Request.HTML

provides: [LazyPagination]

...
*/



var LazyPagination = new Class({
	
	Extends: Request.HTML,
		options: {
			buffer: 1000,
			maxRequests: 5,
			pageDataIndex: 'page',
			data: { page: 2},   
                        afterAppend: {},
                        beforeLoad: {},                        
			navigation: false,
			inject: false // {element: 'foo', where: 'before'}
		},

	initialize: function(element,options){
		this.parent(options);
		this.element = document.id(element);
		this.bound = this.measure.bind(this);
		this.requests = 0;
		
		this.addEvent('onComplete',function(response,html){
			(this.options.inject) ? this.inject(html[0]) : this.adopt(html[0]);
			this.increment();
			this.measure();
		});
		
		if(this.options.navigation) document.id(this.options.navigation).destroy();
		
		this.attach();
		this.measure();
	},
                
	measure: function(){
		var scrollHeight = this.element.getScrollSize().y, 
			height = this.element.getSize().y,
			scroll = this.element.getScroll().y;
		if(scrollHeight-height - this.options.buffer <= scroll) this.send();
		return this;
	},
	
	send: function(){
		if(this.check && this.requests != this.options.maxRequests ) {
                    this.options.beforeLoad(); 
                    this.parent();
                }
	},
	
	increment: function(){
		this.requests++;
		this.options.data[this.options.pageDataIndex]++;
		return this;
	},
	
	attach: function(){
		window.addEvent('resize',this.bound);
		this.element.addEvent('scroll',this.bound);
		return this;
	},
	
	detach: function(){
		window.removeEvent('resize',this.bound);
		this.element.removeEvent('scroll',this.bound);
		return this;
	},
	
	adopt: function(html){
		(this.element === document || this.element === window) ? 
			$(document.body).adopt(html) : this.element.adopt(html);
                        
                this.options.afterAppend(html);        
		return this;
	},
	
	inject: function(html){
		html.inject(this.options.inject.element, this.options.inject.where);
		this.options.afterAppend(html);
                return this;
	}

});