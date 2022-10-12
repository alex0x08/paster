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

Alex:
Was rewritten for standalone usage

...
*/


class LazyPagination {
	options = {
		maxRequests: 5,
		pageDataIndex: 'page',
		idKey: 'id',
		data: { page: 2, modelId: 0 },
		skipMeasure: function() { return false },
		afterAppend: function () { },
		beforeLoad: function () { },
		idSet: new Array(),
		idMode: false,
		navigation: false,
		inject: false
	}
	initialize(options) {
		this.options = { ...this.options, ...options };
		Logger.debug('merged options ', this.options);
		const xhr = new XMLHttpRequest();
		this.xmlhttp = xhr;
		const mainThis = this;
		xhr.onload = function () {
			if (xhr.readyState == XMLHttpRequest.DONE) {
				if (xhr.status == 200) {
					const html = '' + xhr.responseText;
					(mainThis.options.inject) ? mainThis.inject(html) : mainThis.adopt(html);
					mainThis.increment();
					mainThis.measure();
				} else {
					Logger.warn('request failed');
				}
			}
		};
		this.bound = this.measure.bind(this);
		this.requests = 0;
		this.attach();
		this.measure();
	}
	getSizes(el) {
		const h = parseInt(el.offsetHeight) + 10,
			w = parseInt(el.offsetWidth);
		return [h, w];
	}
	measure() {
		if (this.options.skipMeasure() == true) {
		//	console.log('skipping measure..')
			return this;
		}
		var scrollHeight = document.documentElement.scrollHeight,
			height = document.documentElement.clientHeight,
			scroll = window.pageYOffset;
	//	console.log("scrollHeight=" + scrollHeight + " height=" + height + " scroll=" + scroll);
		if (scroll + height >= scrollHeight - 10)
			this.send();
		return this;
	}
	send() {
		Logger.debug('attempt to send.. ');
		if (this.requests != this.options.maxRequests) {
			//console.log('data:', this.options.data[this.options.idKey])
			this.options.beforeLoad(this.options.data[this.options.idKey]);
			if (this.options.idMode) {
				this.options.data[this.options.idKey] = this.options.idSet[this.options.data[this.options.pageDataIndex]];
			}
			//
			this.xmlhttp.open(this.options.method, this.options.url + '?id=' + this.options.data[this.options.idKey], true);
			this.xmlhttp.send();
		}
	}
	increment() {
		this.requests++;
		this.options.data[this.options.pageDataIndex]++;
		return this;
	}
	attach() {
		window.addEventListener('resize', this.bound);
		document.addEventListener('scroll', this.bound);
		return this;
	}
	detach() {
		window.removeEvent('resize', this.bound);
		document.removeEvent('scroll', this.bound);
		return this;
	}
	adopt(html) {
		(this.element === document || this.element === window) ?
			document.body.append(html) : this.element.parentNode.append(html);
		this.options.afterAppend(html, this.options.idMode ?
			this.options.idSet[this.options.data[this.options.pageDataIndex]] : this.options.data[this.options.pageDataIndex]);
		return this;
	}
	inject(html) {
		const div = document.createElement('div');
		div.innerHTML = html;
		const el = document.getElementById(this.options.inject.element);
		el.parentNode.insertBefore(div, el);
		this.options.afterAppend(div, this.options.idMode ?
			this.options.idSet[this.options.data[this.options.pageDataIndex]] : this.options.data[this.options.pageDataIndex]);
		return this;
	}
}
