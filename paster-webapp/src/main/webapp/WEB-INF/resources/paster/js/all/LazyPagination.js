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


class LazyPagination {
	
	options = {
			buffer: 1000,
			maxRequests: 5,
			pageDataIndex: 'page',
                        idKey: 'id',
			data: { page: 2},   
                        afterAppend: {},
                        beforeLoad: {}, 
                        idSet: new Array(),
                        idMode: false,
			navigation: false,
			inject: false, 
                        
		}
              
	initialize(element,options){
		//this.parent(options);


		/*const xmlhttp = new XMLHttpRequest();
        const cb = document.getElementById('newPastasCountBlock');

        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == XMLHttpRequest.DONE) {
               if (xmlhttp.status == 200) {

                  const obj = JSON.parse(xmlhttp.responseText, true);
                        const pcount = obj['count'];
                        if (pcount>0) {
                            cb = document.getElementById('newPastasCountBlock');
                            cb.style.display='';
                            cb.text = pcount;
                          	Tinycon.setBubble(pcount);
                        }

               } else {
                    cb.text= 'Sorry, your request failed :(';
               }
            }
        };

        xmlhttp.open("GET", '${ctx}/main/paste/count/form/'+new Date().getTime()+'.json', true);
        xmlhttp.send(); */

		this.element = document.id(element);
		this.bound = this.measure.bind(this);
		this.requests = 0;

		this.addEventListener('onComplete',function(response,html){
         	(this.options.inject) ? this.inject(html[0]) : this.adopt(html[0]);
			this.increment();
			this.measure();
		});
		
		if(this.options.navigation) document.id(this.options.navigation).destroy();
		this.attach();
		this.measure();
	}
                
	measure(){
		var scrollHeight = this.element.getScrollSize().y, 
			height = this.element.getSize().y,
			scroll = this.element.getScroll().y;
        
         console.log("scrollHeight="+scrollHeight+" height="+height+" scroll="+scroll+" calc="+(scrollHeight-height - this.options.buffer));
                
		if(scrollHeight-height - this.options.buffer <= scroll) this.send();
		return this;
	}
	
	send(){
		console.log('attempt to send..')
		if(this.check && this.requests != this.options.maxRequests ) {
                  
                this.options.beforeLoad(this.options.data[this.options.idKey]);
                    
                    if (this.options.idMode) {
                        this.options.data[this.options.idKey] = this.options.idSet[this.options.data[this.options.pageDataIndex]];
                    }
                    
                   //
                   //  alert( this.options.data[this.options.idKey]);
                    this.parent();
                }
	}
	
	increment(){
		this.requests++;
		this.options.data[this.options.pageDataIndex]++;
		return this;
	}
	
	attach(){
		window.addEventListener('resize',this.bound);
		this.element.addEventListener('scroll',this.bound);
		return this;
	}
	
	detach(){
		window.removeEvent('resize',this.bound);
		this.element.removeEvent('scroll',this.bound);
		return this;
	}
	
	adopt(html){
		//(this.element === document || this.element === window) ? 
	//		$(document.body).adopt(html) : this.element.adopt(html);
						
			(this.element === document || this.element === window) ? 
			document.body.append(html) : this.element.parentNode.append(html);
			
                this.options.afterAppend(html, this.options.idMode ? 
                this.options.idSet[this.options.data[this.options.pageDataIndex]] : this.options.data[this.options.pageDataIndex]);        
		return this;
	}
	
	inject(html){
		html.inject(this.options.inject.element, this.options.inject.where);
	
        
                           this.options.afterAppend(html, this.options.idMode ? 
                this.options.idSet[this.options.data[this.options.pageDataIndex]] : this.options.data[this.options.pageDataIndex]);        
	
                return this;
	}

}
