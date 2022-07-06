/*
---
description: Gets word and character count

license: MIT-style

authors:
- Virtuosi Media

requires:
- core/1.2.4: '*'

provides: [WordCount]

Note: rewritten to work without Mootools

...
*/
class WordCount {

	options = {
                countWordsTo: null,
                countSymbolsTo: null,                
		inputName: null,				//The input name from which text should be retrieved, defaults null
		countWords: true,				//Whether or not to count words
		countChars: true,				//Whether or not to count characters
		charText: 'characters',			//The text that follows the number of characters
		wordText: 'words',				//The text that follows the number of words
		separator: ', ',				//The text that separates the number of words and the number of characters
		liveCount: false,				//Whether or not to use the event trigger, set false if you'd like to call the getCount function separately
		eventTrigger: 'keyup'			//The event that triggers the count update
	}


	initialize(targetId, options){
		this.options = options;
		this.target = document.getElementById(targetId);
		
		if ((this.options.liveCount)&&(this.options.inputName)){
			var input = document.getElementsByName(this.options.inputName);
			input.addEventListener(this.options.eventTrigger, function(){
				this.getCount(input.getAttribute('value'));
			}.bind(this));
		}
	}
	
	getCount(text){
		var numChars = text.length;
		var numWords = (numChars != 0) ? text.split(' ').length : 0;
                
                if (this.options.countWordsTo!=null) {
                    this.options.countWordsTo.value =  numWords;
                }
                
                 if (this.options.countSymbolsTo!=null) {
                    this.options.countSymbolsTo.value =numChars;
                }
                
		if ((this.options.countWords) && (this.options.countChars)) {
			var insertText = numWords + ' ' + this.options.wordText + this.options.separator + numChars + ' ' + this.options.charText;
		} else {
			var insertText = (this.options.countWords) ? numWords + ' ' + this.options.wordText : numChars + ' ' + this.options.charText;
		} 
		if (insertText){ this.target.innerHTML = insertText; }
	}
};