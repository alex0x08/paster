;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;
	/**
	 * This is stripped & merged collection of syntax highlighing scripts.
	 * 
	 * 
	 */
        function BrushBash()
	{
		var keywords =	'if fi then elif else for do done until while break continue case function return in eq ne ge le';
		var commands =  'alias apropos awk basename bash bc bg builtin bzip2 cal cat cd cfdisk chgrp chmod chown chroot' +
						'cksum clear cmp comm command cp cron crontab csplit cut date dc dd ddrescue declare df ' +
						'diff diff3 dig dir dircolors dirname dirs du echo egrep eject enable env ethtool eval ' +
						'exec exit expand export expr false fdformat fdisk fg fgrep file find fmt fold format ' +
						'free fsck ftp gawk getopts grep groups gzip hash head history hostname id ifconfig ' +
						'import install join kill less let ln local locate logname logout look lpc lpr lprint ' +
						'lprintd lprintq lprm ls lsof make man mkdir mkfifo mkisofs mknod more mount mtools ' +
						'mv netstat nice nl nohup nslookup open op passwd paste pathchk ping popd pr printcap ' +
						'printenv printf ps pushd pwd quota quotacheck quotactl ram rcp read readonly renice ' +
						'remsync rm rmdir rsync screen scp sdiff sed select seq set sftp shift shopt shutdown ' +
						'sleep sort source split ssh strace su sudo sum symlink sync tail tar tee test time ' +
						'times touch top traceroute trap tr true tsort tty type ulimit umask umount unalias ' +
						'uname unexpand uniq units unset unshar useradd usermod users uuencode uudecode v vdir ' +
						'vi watch wc whereis which who whoami Wget xargs yes'
						;
		this.regexList = [
			{ regex: /^#!.*$/gm,											css: 'preprocessor bold' },
			{ regex: /\/[\w-\/]+/gm,										css: 'plain' },
			{ regex: SyntaxHighlighter.regexLib.singleLinePerlComments,		css: 'comments' },		// one line comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,			css: 'string' },		// double quoted strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,			css: 'string' },		// single quoted strings
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),			css: 'keyword' },		// keywords
			{ regex: new RegExp(this.getKeywords(commands), 'gm'),			css: 'functions' }		// commands
			];
	}
	BrushBash.prototype	= new SyntaxHighlighter.Highlighter();
	BrushBash.aliases	= ['bash', 'shell'];
	SyntaxHighlighter.brushes.Bash = BrushBash;
        function BrushSharp()
	{
		var keywords =	'abstract as base bool break byte case catch char checked class const ' +
						'continue decimal default delegate do double else enum event explicit ' +
						'extern false finally fixed float for foreach get goto if implicit in int ' +
						'interface internal is lock long namespace new null object operator out ' +
						'override params private protected public readonly ref return sbyte sealed set ' +
						'short sizeof stackalloc static string struct switch this throw true try ' +
						'typeof uint ulong unchecked unsafe ushort using virtual void while';
		function fixComments(match, regexInfo)
		{
			var css = (match[0].indexOf("///") == 0)
				? 'color1'
				: 'comments'
				;
			return [new SyntaxHighlighter.Match(match[0], match.index, css)];
		}
		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	func : fixComments },		// one line comments
			{ regex: SyntaxHighlighter.regexLib.multiLineCComments,		css: 'comments' },			// multiline comments
			{ regex: /@"(?:[^"]|"")*"/g,								css: 'string' },			// @-quoted strings
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },			// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },			// strings
			{ regex: /^\s*#.*/gm,										css: 'preprocessor' },		// preprocessor tags like #region and #endregion
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' },			// c# keyword
			{ regex: /\bpartial(?=\s+(?:class|interface|struct)\b)/g,	css: 'keyword' },			// contextual keyword: 'partial'
			{ regex: /\byield(?=\s+(?:return|break)\b)/g,				css: 'keyword' }			// contextual keyword: 'yield'
			];
		
		this.forHtmlScript(SyntaxHighlighter.regexLib.aspScriptTags);
	};

	BrushSharp.prototype	= new SyntaxHighlighter.Highlighter();
	BrushSharp.aliases	= ['c#', 'c-sharp', 'csharp'];

	SyntaxHighlighter.brushes.CSharp = BrushSharp;




        function BrushCPP()
	{
		// Copyright 2006 Shin, YoungJin
	
		var datatypes =	'ATOM BOOL BOOLEAN BYTE CHAR COLORREF DWORD DWORDLONG DWORD_PTR ' +
						'DWORD32 DWORD64 FLOAT HACCEL HALF_PTR HANDLE HBITMAP HBRUSH ' +
						'HCOLORSPACE HCONV HCONVLIST HCURSOR HDC HDDEDATA HDESK HDROP HDWP ' +
						'HENHMETAFILE HFILE HFONT HGDIOBJ HGLOBAL HHOOK HICON HINSTANCE HKEY ' +
						'HKL HLOCAL HMENU HMETAFILE HMODULE HMONITOR HPALETTE HPEN HRESULT ' +
						'HRGN HRSRC HSZ HWINSTA HWND INT INT_PTR INT32 INT64 LANGID LCID LCTYPE ' +
						'LGRPID LONG LONGLONG LONG_PTR LONG32 LONG64 LPARAM LPBOOL LPBYTE LPCOLORREF ' +
						'LPCSTR LPCTSTR LPCVOID LPCWSTR LPDWORD LPHANDLE LPINT LPLONG LPSTR LPTSTR ' +
						'LPVOID LPWORD LPWSTR LRESULT PBOOL PBOOLEAN PBYTE PCHAR PCSTR PCTSTR PCWSTR ' +
						'PDWORDLONG PDWORD_PTR PDWORD32 PDWORD64 PFLOAT PHALF_PTR PHANDLE PHKEY PINT ' +
						'PINT_PTR PINT32 PINT64 PLCID PLONG PLONGLONG PLONG_PTR PLONG32 PLONG64 POINTER_32 ' +
						'POINTER_64 PSHORT PSIZE_T PSSIZE_T PSTR PTBYTE PTCHAR PTSTR PUCHAR PUHALF_PTR ' +
						'PUINT PUINT_PTR PUINT32 PUINT64 PULONG PULONGLONG PULONG_PTR PULONG32 PULONG64 ' +
						'PUSHORT PVOID PWCHAR PWORD PWSTR SC_HANDLE SC_LOCK SERVICE_STATUS_HANDLE SHORT ' +
						'SIZE_T SSIZE_T TBYTE TCHAR UCHAR UHALF_PTR UINT UINT_PTR UINT32 UINT64 ULONG ' +
						'ULONGLONG ULONG_PTR ULONG32 ULONG64 USHORT USN VOID WCHAR WORD WPARAM WPARAM WPARAM ' +
						'char bool short int __int32 __int64 __int8 __int16 long float double __wchar_t ' +
						'clock_t _complex _dev_t _diskfree_t div_t ldiv_t _exception _EXCEPTION_POINTERS ' +
						'FILE _finddata_t _finddatai64_t _wfinddata_t _wfinddatai64_t __finddata64_t ' +
						'__wfinddata64_t _FPIEEE_RECORD fpos_t _HEAPINFO _HFILE lconv intptr_t ' +
						'jmp_buf mbstate_t _off_t _onexit_t _PNH ptrdiff_t _purecall_handler ' +
						'sig_atomic_t size_t _stat __stat64 _stati64 terminate_function ' +
						'time_t __time64_t _timeb __timeb64 tm uintptr_t _utimbuf ' +
						'va_list wchar_t wctrans_t wctype_t wint_t signed';

		var keywords =	'break case catch class const __finally __exception __try ' +
						'const_cast continue private public protected __declspec ' +
						'default delete deprecated dllexport dllimport do dynamic_cast ' +
						'else enum explicit extern if for friend goto inline ' +
						'mutable naked namespace new noinline noreturn nothrow ' +
						'register reinterpret_cast return selectany ' +
						'sizeof static static_cast struct switch template this ' +
						'thread throw true false try typedef typeid typename union ' +
						'using uuid virtual void volatile whcar_t while';
					
		var functions =	'assert isalnum isalpha iscntrl isdigit isgraph islower isprint' +
						'ispunct isspace isupper isxdigit tolower toupper errno localeconv ' +
						'setlocale acos asin atan atan2 ceil cos cosh exp fabs floor fmod ' +
						'frexp ldexp log log10 modf pow sin sinh sqrt tan tanh jmp_buf ' +
						'longjmp setjmp raise signal sig_atomic_t va_arg va_end va_start ' +
						'clearerr fclose feof ferror fflush fgetc fgetpos fgets fopen ' +
						'fprintf fputc fputs fread freopen fscanf fseek fsetpos ftell ' +
						'fwrite getc getchar gets perror printf putc putchar puts remove ' +
						'rename rewind scanf setbuf setvbuf sprintf sscanf tmpfile tmpnam ' +
						'ungetc vfprintf vprintf vsprintf abort abs atexit atof atoi atol ' +
						'bsearch calloc div exit free getenv labs ldiv malloc mblen mbstowcs ' +
						'mbtowc qsort rand realloc srand strtod strtol strtoul system ' +
						'wcstombs wctomb memchr memcmp memcpy memmove memset strcat strchr ' +
						'strcmp strcoll strcpy strcspn strerror strlen strncat strncmp ' +
						'strncpy strpbrk strrchr strspn strstr strtok strxfrm asctime ' +
						'clock ctime difftime gmtime localtime mktime strftime time';

		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'comments' },			// one line comments
			{ regex: SyntaxHighlighter.regexLib.multiLineCComments,		css: 'comments' },			// multiline comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },			// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },			// strings
			{ regex: /^ *#.*/gm,										css: 'preprocessor' },
			{ regex: new RegExp(this.getKeywords(datatypes), 'gm'),		css: 'color1 bold' },
			{ regex: new RegExp(this.getKeywords(functions), 'gm'),		css: 'functions bold' },
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword bold' }
			];
	};

	BrushCPP.prototype	= new SyntaxHighlighter.Highlighter();
	BrushCPP.aliases	= ['cpp', 'c'];

	SyntaxHighlighter.brushes.Cpp = BrushCPP;


        	function BrushCss()
	{
		function getKeywordsCSS(str)
		{
			return '\\b([a-z_]|)' + str.replace(/ /g, '(?=:)\\b|\\b([a-z_\\*]|\\*|)') + '(?=:)\\b';
		};
	
		function getValuesCSS(str)
		{
			return '\\b' + str.replace(/ /g, '(?!-)(?!:)\\b|\\b()') + '\:\\b';
		};

		var keywords =	'ascent azimuth background-attachment background-color background-image background-position ' +
						'background-repeat background baseline bbox border-collapse border-color border-spacing border-style border-top ' +
						'border-right border-bottom border-left border-top-color border-right-color border-bottom-color border-left-color ' +
						'border-top-style border-right-style border-bottom-style border-left-style border-top-width border-right-width ' +
						'border-bottom-width border-left-width border-width border bottom cap-height caption-side centerline clear clip color ' +
						'content counter-increment counter-reset cue-after cue-before cue cursor definition-src descent direction display ' +
						'elevation empty-cells float font-size-adjust font-family font-size font-stretch font-style font-variant font-weight font ' +
						'height left letter-spacing line-height list-style-image list-style-position list-style-type list-style margin-top ' +
						'margin-right margin-bottom margin-left margin marker-offset marks mathline max-height max-width min-height min-width orphans ' +
						'outline-color outline-style outline-width outline overflow padding-top padding-right padding-bottom padding-left padding page ' +
						'page-break-after page-break-before page-break-inside pause pause-after pause-before pitch pitch-range play-during position ' +
						'quotes right richness size slope src speak-header speak-numeral speak-punctuation speak speech-rate stemh stemv stress ' +
						'table-layout text-align top text-decoration text-indent text-shadow text-transform unicode-bidi unicode-range units-per-em ' +
						'vertical-align visibility voice-family volume white-space widows width widths word-spacing x-height z-index';

		var values =	'above absolute all always aqua armenian attr aural auto avoid baseline behind below bidi-override black blink block blue bold bolder '+
						'both bottom braille capitalize caption center center-left center-right circle close-quote code collapse compact condensed '+
						'continuous counter counters crop cross crosshair cursive dashed decimal decimal-leading-zero default digits disc dotted double '+
						'embed embossed e-resize expanded extra-condensed extra-expanded fantasy far-left far-right fast faster fixed format fuchsia '+
						'gray green groove handheld hebrew help hidden hide high higher icon inline-table inline inset inside invert italic '+
						'justify landscape large larger left-side left leftwards level lighter lime line-through list-item local loud lower-alpha '+
						'lowercase lower-greek lower-latin lower-roman lower low ltr marker maroon medium message-box middle mix move narrower '+
						'navy ne-resize no-close-quote none no-open-quote no-repeat normal nowrap n-resize nw-resize oblique olive once open-quote outset '+
						'outside overline pointer portrait pre print projection purple red relative repeat repeat-x repeat-y rgb ridge right right-side '+
						'rightwards rtl run-in screen scroll semi-condensed semi-expanded separate se-resize show silent silver slower slow '+
						'small small-caps small-caption smaller soft solid speech spell-out square s-resize static status-bar sub super sw-resize '+
						'table-caption table-cell table-column table-column-group table-footer-group table-header-group table-row table-row-group teal '+
						'text-bottom text-top thick thin top transparent tty tv ultra-condensed ultra-expanded underline upper-alpha uppercase upper-latin '+
						'upper-roman url visible wait white wider w-resize x-fast x-high x-large x-loud x-low x-slow x-small x-soft xx-large xx-small yellow';

		var fonts =		'[mM]onospace [tT]ahoma [vV]erdana [aA]rial [hH]elvetica [sS]ans-serif [sS]erif [cC]ourier mono sans serif';
	
		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.multiLineCComments,		css: 'comments' },	// multiline comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },	// double quoted strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },	// single quoted strings
			{ regex: /\#[a-fA-F0-9]{3,6}/g,								css: 'value' },		// html colors
			{ regex: /(-?\d+)(\.\d+)?(px|em|pt|\:|\%|)/g,				css: 'value' },		// sizes
			{ regex: /!important/g,										css: 'color3' },	// !important
			{ regex: new RegExp(getKeywordsCSS(keywords), 'gm'),		css: 'keyword' },	// keywords
			{ regex: new RegExp(getValuesCSS(values), 'g'),				css: 'value' },		// values
			{ regex: new RegExp(this.getKeywords(fonts), 'g'),			css: 'color1' }		// fonts
			];

		this.forHtmlScript({ 
			left: /(&lt;|<)\s*style.*?(&gt;|>)/gi, 
			right: /(&lt;|<)\/\s*style\s*(&gt;|>)/gi 
			});
	};

	BrushCss.prototype	= new SyntaxHighlighter.Highlighter();
	BrushCss.aliases	= ['css'];

	SyntaxHighlighter.brushes.CSS = BrushCss;


        


        function BrushDiff()
	{
		this.regexList = [
			{ regex: /^\+\+\+.*$/gm,		css: 'color2' },
			{ regex: /^\-\-\-.*$/gm,		css: 'color2' },
			{ regex: /^\s.*$/gm,			css: 'color1' },
			{ regex: /^@@.*@@$/gm,			css: 'variable' },
			{ regex: /^\+[^\+]{1}.*$/gm,	css: 'string' },
			{ regex: /^\-[^\-]{1}.*$/gm,	css: 'comments' }
			];
	};

	BrushDiff.prototype	= new SyntaxHighlighter.Highlighter();
	BrushDiff.aliases	= ['diff', 'patch'];

	SyntaxHighlighter.brushes.Diff = BrushDiff;

        function BrushErlang()
	{
		// Contributed by Jean-Lou Dupont
		// http://jldupont.blogspot.com/2009/06/erlang-syntax-highlighter.html  

		// According to: http://erlang.org/doc/reference_manual/introduction.html#1.5
		var keywords = 'after and andalso band begin bnot bor bsl bsr bxor '+
			'case catch cond div end fun if let not of or orelse '+
			'query receive rem try when xor'+
			// additional
			' module export import define';

		this.regexList = [
			{ regex: new RegExp("[A-Z][A-Za-z0-9_]+", 'g'), 			css: 'constants' },
			{ regex: new RegExp("\\%.+", 'gm'), 						css: 'comments' },
			{ regex: new RegExp("\\?[A-Za-z0-9_]+", 'g'), 				css: 'preprocessor' },
			{ regex: new RegExp("[a-z0-9_]+:[a-z0-9_]+", 'g'), 			css: 'functions' },
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },
			{ regex: new RegExp(this.getKeywords(keywords),	'gm'),		css: 'keyword' }
			];
	};

	BrushErlang.prototype	= new SyntaxHighlighter.Highlighter();
	BrushErlang.aliases	= ['erl', 'erlang'];

	SyntaxHighlighter.brushes.Erland = BrushErlang;


        	function BrushJScript()
	{
		var keywords =	'break case catch continue ' +
						'default delete do else false  ' +
						'for function if in instanceof ' +
						'new null return super switch ' +
						'this throw true try typeof var while with'
						;

		var r = SyntaxHighlighter.regexLib;
		
		this.regexList = [
			{ regex: r.multiLineDoubleQuotedString,					css: 'string' },			// double quoted strings
			{ regex: r.multiLineSingleQuotedString,					css: 'string' },			// single quoted strings
			{ regex: r.singleLineCComments,							css: 'comments' },			// one line comments
			{ regex: r.multiLineCComments,							css: 'comments' },			// multiline comments
			{ regex: /\s*#.*/gm,									css: 'preprocessor' },		// preprocessor tags like #region and #endregion
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),	css: 'keyword' }			// keywords
			];
	
		this.forHtmlScript(r.scriptScriptTags);
	};

	BrushJScript.prototype	= new SyntaxHighlighter.Highlighter();
	BrushJScript.aliases	= ['js', 'jscript', 'javascript'];

	SyntaxHighlighter.brushes.JScript = BrushJScript;


	function BrushJava()
	{
		var keywords =	'abstract assert boolean break byte case catch char class const ' +
						'continue default do double else enum extends ' +
						'false final finally float for goto if implements import ' +
						'instanceof int interface long native new null ' +
						'package private protected public return ' +
						'short static strictfp super switch synchronized this throw throws true ' +
						'transient try void volatile while';

		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'comments' },		// one line comments
			{ regex: /\/\*([^\*][\s\S]*)?\*\//gm,						css: 'comments' },	 	// multiline comments
			{ regex: /\/\*(?!\*\/)\*[\s\S]*?\*\//gm,					css: 'preprocessor' },	// documentation comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },		// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },		// strings
			{ regex: /\b([\d]+(\.[\d]+)?|0x[a-f0-9]+)\b/gi,				css: 'value' },			// numbers
			{ regex: /(?!\@interface\b)\@[\$\w]+\b/g,					css: 'color1' },		// annotation @anno
			{ regex: /\@interface\b/g,									css: 'color2' },		// @interface keyword
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' }		// java keyword
			];

		this.forHtmlScript({
			left	: /(&lt;|<)%[@!=]?/g, 
			right	: /%(&gt;|>)/g 
		});
	};

	BrushJava.prototype	= new SyntaxHighlighter.Highlighter();
	BrushJava.aliases	= ['java'];

	SyntaxHighlighter.brushes.Java = BrushJava;



        	function BrushPerl()
	{
		// Contributed by David Simmons-Duffin and Marty Kube
	
		var funcs = 
			'abs accept alarm atan2 bind binmode chdir chmod chomp chop chown chr ' + 
			'chroot close closedir connect cos crypt defined delete each endgrent ' + 
			'endhostent endnetent endprotoent endpwent endservent eof exec exists ' + 
			'exp fcntl fileno flock fork format formline getc getgrent getgrgid ' + 
			'getgrnam gethostbyaddr gethostbyname gethostent getlogin getnetbyaddr ' + 
			'getnetbyname getnetent getpeername getpgrp getppid getpriority ' + 
			'getprotobyname getprotobynumber getprotoent getpwent getpwnam getpwuid ' + 
			'getservbyname getservbyport getservent getsockname getsockopt glob ' + 
			'gmtime grep hex index int ioctl join keys kill lc lcfirst length link ' + 
			'listen localtime lock log lstat map mkdir msgctl msgget msgrcv msgsnd ' + 
			'oct open opendir ord pack pipe pop pos print printf prototype push ' + 
			'quotemeta rand read readdir readline readlink readpipe recv rename ' + 
			'reset reverse rewinddir rindex rmdir scalar seek seekdir select semctl ' + 
			'semget semop send setgrent sethostent setnetent setpgrp setpriority ' + 
			'setprotoent setpwent setservent setsockopt shift shmctl shmget shmread ' + 
			'shmwrite shutdown sin sleep socket socketpair sort splice split sprintf ' + 
			'sqrt srand stat study substr symlink syscall sysopen sysread sysseek ' + 
			'system syswrite tell telldir time times tr truncate uc ucfirst umask ' + 
			'undef unlink unpack unshift utime values vec wait waitpid warn write';
		var keywords =  
			'bless caller continue dbmclose dbmopen die do dump else elsif eval exit ' +
			'for foreach goto if import last local my next no our package redo ref ' + 
			'require return sub tie tied unless untie until use wantarray while';
		this.regexList = [
			{ regex: new RegExp('#[^!].*$', 'gm'),					css: 'comments' },
			{ regex: new RegExp('^\\s*#!.*$', 'gm'),				css: 'preprocessor' }, // shebang
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,	css: 'string' },
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,	css: 'string' },
			{ regex: new RegExp('(\\$|@|%)\\w+', 'g'),				css: 'variable' },
			{ regex: new RegExp(this.getKeywords(funcs), 'gmi'),	css: 'functions' },
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),	css: 'keyword' }
		    ];
		this.forHtmlScript(SyntaxHighlighter.regexLib.phpScriptTags);
	}
	BrushPerl.prototype	= new SyntaxHighlighter.Highlighter();
	BrushPerl.aliases		= ['perl', 'Perl', 'pl'];
	SyntaxHighlighter.brushes.Perl = BrushPerl;
	function BrushPhp()
	{
		var funcs	=	'abs acos acosh addcslashes addslashes ' +
						'array_change_key_case array_chunk array_combine array_count_values array_diff '+
						'array_diff_assoc array_diff_key array_diff_uassoc array_diff_ukey array_fill '+
						'array_filter array_flip array_intersect array_intersect_assoc array_intersect_key '+
						'array_intersect_uassoc array_intersect_ukey array_key_exists array_keys array_map '+
						'array_merge array_merge_recursive array_multisort array_pad array_pop array_product '+
						'array_push array_rand array_reduce array_reverse array_search array_shift '+
						'array_slice array_splice array_sum array_udiff array_udiff_assoc '+
						'array_udiff_uassoc array_uintersect array_uintersect_assoc '+
						'array_uintersect_uassoc array_unique array_unshift array_values array_walk '+
						'array_walk_recursive atan atan2 atanh base64_decode base64_encode base_convert '+
						'basename bcadd bccomp bcdiv bcmod bcmul bindec bindtextdomain bzclose bzcompress '+
						'bzdecompress bzerrno bzerror bzerrstr bzflush bzopen bzread bzwrite ceil chdir '+
						'checkdate checkdnsrr chgrp chmod chop chown chr chroot chunk_split class_exists '+
						'closedir closelog copy cos cosh count count_chars date decbin dechex decoct '+
						'deg2rad delete ebcdic2ascii echo empty end ereg ereg_replace eregi eregi_replace error_log '+
						'error_reporting escapeshellarg escapeshellcmd eval exec exit exp explode extension_loaded '+
						'feof fflush fgetc fgetcsv fgets fgetss file_exists file_get_contents file_put_contents '+
						'fileatime filectime filegroup fileinode filemtime fileowner fileperms filesize filetype '+
						'floatval flock floor flush fmod fnmatch fopen fpassthru fprintf fputcsv fputs fread fscanf '+
						'fseek fsockopen fstat ftell ftok getallheaders getcwd getdate getenv gethostbyaddr gethostbyname '+
						'gethostbynamel getimagesize getlastmod getmxrr getmygid getmyinode getmypid getmyuid getopt '+
						'getprotobyname getprotobynumber getrandmax getrusage getservbyname getservbyport gettext '+
						'gettimeofday gettype glob gmdate gmmktime ini_alter ini_get ini_get_all ini_restore ini_set '+
						'interface_exists intval ip2long is_a is_array is_bool is_callable is_dir is_double '+
						'is_executable is_file is_finite is_float is_infinite is_int is_integer is_link is_long '+
						'is_nan is_null is_numeric is_object is_readable is_real is_resource is_scalar is_soap_fault '+
						'is_string is_subclass_of is_uploaded_file is_writable is_writeable mkdir mktime nl2br '+
						'parse_ini_file parse_str parse_url passthru pathinfo print readlink realpath rewind rewinddir rmdir '+
						'round str_ireplace str_pad str_repeat str_replace str_rot13 str_shuffle str_split '+
						'str_word_count strcasecmp strchr strcmp strcoll strcspn strftime strip_tags stripcslashes '+
						'stripos stripslashes stristr strlen strnatcasecmp strnatcmp strncasecmp strncmp strpbrk '+
						'strpos strptime strrchr strrev strripos strrpos strspn strstr strtok strtolower strtotime '+
						'strtoupper strtr strval substr substr_compare';
		var keywords =	'abstract and array as break case catch cfunction class clone const continue declare default die do ' +
						'else elseif enddeclare endfor endforeach endif endswitch endwhile extends final for foreach ' +
						'function include include_once global goto if implements interface instanceof namespace new ' +
						'old_function or private protected public return require require_once static switch ' +
						'throw try use var while xor ';
		var constants	= '__FILE__ __LINE__ __METHOD__ __FUNCTION__ __CLASS__';
		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'comments' },			// one line comments
			{ regex: SyntaxHighlighter.regexLib.multiLineCComments,		css: 'comments' },			// multiline comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },			// double quoted strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },			// single quoted strings
			{ regex: /\$\w+/g,											css: 'variable' },			// variables
			{ regex: new RegExp(this.getKeywords(funcs), 'gmi'),		css: 'functions' },			// common functions
			{ regex: new RegExp(this.getKeywords(constants), 'gmi'),	css: 'constants' },			// constants
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' }			// keyword
			];
		this.forHtmlScript(SyntaxHighlighter.regexLib.phpScriptTags);
	};
	BrushPhp.prototype	= new SyntaxHighlighter.Highlighter();
	BrushPhp.aliases	= ['php'];
	SyntaxHighlighter.brushes.Php = BrushPhp;
        function BrushPlain()
	{
   	};
	BrushPlain.prototype	= new SyntaxHighlighter.Highlighter();
	BrushPlain.aliases	= ['text', 'plain'];
	SyntaxHighlighter.brushes.Plain = BrushPlain;
        	function BrushPowerShell()
	{
		// Contributes by B.v.Zanten, Getronics
		// http://confluence.atlassian.com/display/CONFEXT/New+Code+Macro
		var keywords = 'Add-Content Add-History Add-Member Add-PSSnapin Clear(-Content)? Clear-Item ' +
					'Clear-ItemProperty Clear-Variable Compare-Object ConvertFrom-SecureString Convert-Path ' +
					'ConvertTo-Html ConvertTo-SecureString Copy(-Item)? Copy-ItemProperty Export-Alias ' +
					'Export-Clixml Export-Console Export-Csv ForEach(-Object)? Format-Custom Format-List ' +
					'Format-Table Format-Wide Get-Acl Get-Alias Get-AuthenticodeSignature Get-ChildItem Get-Command ' +
					'Get-Content Get-Credential Get-Culture Get-Date Get-EventLog Get-ExecutionPolicy ' +
					'Get-Help Get-History Get-Host Get-Item Get-ItemProperty Get-Location Get-Member ' +
					'Get-PfxCertificate Get-Process Get-PSDrive Get-PSProvider Get-PSSnapin Get-Service ' +
					'Get-TraceSource Get-UICulture Get-Unique Get-Variable Get-WmiObject Group-Object ' +
					'Import-Alias Import-Clixml Import-Csv Invoke-Expression Invoke-History Invoke-Item ' +
					'Join-Path Measure-Command Measure-Object Move(-Item)? Move-ItemProperty New-Alias ' +
					'New-Item New-ItemProperty New-Object New-PSDrive New-Service New-TimeSpan ' +
					'New-Variable Out-Default Out-File Out-Host Out-Null Out-Printer Out-String Pop-Location ' +
					'Push-Location Read-Host Remove-Item Remove-ItemProperty Remove-PSDrive Remove-PSSnapin ' +
					'Remove-Variable Rename-Item Rename-ItemProperty Resolve-Path Restart-Service Resume-Service ' +
					'Select-Object Select-String Set-Acl Set-Alias Set-AuthenticodeSignature Set-Content ' +
					'Set-Date Set-ExecutionPolicy Set-Item Set-ItemProperty Set-Location Set-PSDebug ' +
					'Set-Service Set-TraceSource Set(-Variable)? Sort-Object Split-Path Start-Service ' +
					'Start-Sleep Start-Transcript Stop-Process Stop-Service Stop-Transcript Suspend-Service ' +
					'Tee-Object Test-Path Trace-Command Update-FormatData Update-TypeData Where(-Object)? ' +
					'Write-Debug Write-Error Write(-Host)? Write-Output Write-Progress Write-Verbose Write-Warning';
		var alias = 'ac asnp clc cli clp clv cpi cpp cvpa diff epal epcsv fc fl ' +
					'ft fw gal gc gci gcm gdr ghy gi gl gm gp gps group gsv ' +
					'gsnp gu gv gwmi iex ihy ii ipal ipcsv mi mp nal ndr ni nv oh rdr ' +
					'ri rni rnp rp rsnp rv rvpa sal sasv sc select si sl sleep sort sp ' +
					'spps spsv sv tee cat cd cp h history kill lp ls ' +
					'mount mv popd ps pushd pwd r rm rmdir echo cls chdir del dir ' +
					'erase rd ren type % \\?';
		this.regexList = [
			{ regex: /#.*$/gm,										css: 'comments' },  // one line comments
			{ regex: /\$[a-zA-Z0-9]+\b/g,							css: 'value'   },   // variables $Computer1
			{ regex: /\-[a-zA-Z]+\b/g,								css: 'keyword' },   // Operators    -not  -and  -eq
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,	css: 'string' },    // strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,	css: 'string' },    // strings
			{ regex: new RegExp(this.getKeywords(keywords), 'gmi'),	css: 'keyword' },
			{ regex: new RegExp(this.getKeywords(alias), 'gmi'),	css: 'keyword' }
		];
	};
	BrushPowerShell.prototype	= new SyntaxHighlighter.Highlighter();
	BrushPowerShell.aliases	= ['powershell', 'ps'];
	SyntaxHighlighter.brushes.PowerShell = BrushPowerShell;
	function BrushPython()
	{
		// Contributed by Gheorghe Milas and Ahmad Sherif
		var keywords =  'and assert break class continue def del elif else ' +
						'except exec finally for from global if import in is ' +
						'lambda not or pass print raise return try yield while';
		var funcs = '__import__ abs all any apply basestring bin bool buffer callable ' +
					'chr classmethod cmp coerce compile complex delattr dict dir ' +
					'divmod enumerate eval execfile file filter float format frozenset ' +
					'getattr globals hasattr hash help hex id input int intern ' +
					'isinstance issubclass iter len list locals long map max min next ' +
					'object oct open ord pow print property range raw_input reduce ' +
					'reload repr reversed round set setattr slice sorted staticmethod ' +
					'str sum super tuple type type unichr unicode vars xrange zip';
		var special =  'None True False self cls class_';
		this.regexList = [
				{ regex: SyntaxHighlighter.regexLib.singleLinePerlComments, css: 'comments' },
				{ regex: /^\s*@\w+/gm, 										css: 'decorator' },
				{ regex: /(['\"]{3})([^\1])*?\1/gm, 						css: 'comments' },
				{ regex: /"(?!")(?:\.|\\\"|[^\""\n])*"/gm, 					css: 'string' },
				{ regex: /'(?!')(?:\.|(\\\')|[^\''\n])*'/gm, 				css: 'string' },
				{ regex: /\+|\-|\*|\/|\%|=|==/gm, 							css: 'keyword' },
				{ regex: /\b\d+\.?\w*/g, 									css: 'value' },
				{ regex: new RegExp(this.getKeywords(funcs), 'gmi'),		css: 'functions' },
				{ regex: new RegExp(this.getKeywords(keywords), 'gm'), 		css: 'keyword' },
				{ regex: new RegExp(this.getKeywords(special), 'gm'), 		css: 'color1' }
				];
		this.forHtmlScript(SyntaxHighlighter.regexLib.aspScriptTags);
	};
	BrushPython.prototype	= new SyntaxHighlighter.Highlighter();
	BrushPython.aliases	= ['py', 'python'];
        function BrushRuby()
	{
		// Contributed by Erik Peterson.
		var keywords =	'alias and BEGIN begin break case class def define_method defined do each else elsif ' +
						'END end ensure false for if in module new next nil not or raise redo rescue retry return ' +
						'self super then throw true undef unless until when while yield';
		var builtins =	'Array Bignum Binding Class Continuation Dir Exception FalseClass File::Stat File Fixnum Fload ' +
						'Hash Integer IO MatchData Method Module NilClass Numeric Object Proc Range Regexp String Struct::TMS Symbol ' +
						'ThreadGroup Thread Time TrueClass';
		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLinePerlComments,	css: 'comments' },		// one line comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },		// double quoted strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },		// single quoted strings
			{ regex: /\b[A-Z0-9_]+\b/g,									css: 'constants' },		// constants
			{ regex: /:[a-z][A-Za-z0-9_]*/g,							css: 'color2' },		// symbols
			{ regex: /(\$|@@|@)\w+/g,									css: 'variable bold' },	// $global, @instance, and @@class variables
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' },		// keywords
			{ regex: new RegExp(this.getKeywords(builtins), 'gm'),		css: 'color1' }			// builtins
			];
		this.forHtmlScript(SyntaxHighlighter.regexLib.aspScriptTags);
	};
	BrushRuby.prototype	= new SyntaxHighlighter.Highlighter();
	BrushRuby.aliases	= ['ruby', 'rails', 'ror', 'rb'];
	SyntaxHighlighter.brushes.Ruby = BrushRuby;
        function BrushSass()
	{
		function getKeywordsCSS(str)
		{
			return '\\b([a-z_]|)' + str.replace(/ /g, '(?=:)\\b|\\b([a-z_\\*]|\\*|)') + '(?=:)\\b';
		};
		function getValuesCSS(str)
		{
			return '\\b' + str.replace(/ /g, '(?!-)(?!:)\\b|\\b()') + '\:\\b';
		};
		var keywords =	'ascent azimuth background-attachment background-color background-image background-position ' +
						'background-repeat background baseline bbox border-collapse border-color border-spacing border-style border-top ' +
						'border-right border-bottom border-left border-top-color border-right-color border-bottom-color border-left-color ' +
						'border-top-style border-right-style border-bottom-style border-left-style border-top-width border-right-width ' +
						'border-bottom-width border-left-width border-width border bottom cap-height caption-side centerline clear clip color ' +
						'content counter-increment counter-reset cue-after cue-before cue cursor definition-src descent direction display ' +
						'elevation empty-cells float font-size-adjust font-family font-size font-stretch font-style font-variant font-weight font ' +
						'height left letter-spacing line-height list-style-image list-style-position list-style-type list-style margin-top ' +
						'margin-right margin-bottom margin-left margin marker-offset marks mathline max-height max-width min-height min-width orphans ' +
						'outline-color outline-style outline-width outline overflow padding-top padding-right padding-bottom padding-left padding page ' +
						'page-break-after page-break-before page-break-inside pause pause-after pause-before pitch pitch-range play-during position ' +
						'quotes right richness size slope src speak-header speak-numeral speak-punctuation speak speech-rate stemh stemv stress ' +
						'table-layout text-align top text-decoration text-indent text-shadow text-transform unicode-bidi unicode-range units-per-em ' +
						'vertical-align visibility voice-family volume white-space widows width widths word-spacing x-height z-index';
		
		var values =	'above absolute all always aqua armenian attr aural auto avoid baseline behind below bidi-override black blink block blue bold bolder '+
						'both bottom braille capitalize caption center center-left center-right circle close-quote code collapse compact condensed '+
						'continuous counter counters crop cross crosshair cursive dashed decimal decimal-leading-zero digits disc dotted double '+
						'embed embossed e-resize expanded extra-condensed extra-expanded fantasy far-left far-right fast faster fixed format fuchsia '+
						'gray green groove handheld hebrew help hidden hide high higher icon inline-table inline inset inside invert italic '+
						'justify landscape large larger left-side left leftwards level lighter lime line-through list-item local loud lower-alpha '+
						'lowercase lower-greek lower-latin lower-roman lower low ltr marker maroon medium message-box middle mix move narrower '+
						'navy ne-resize no-close-quote none no-open-quote no-repeat normal nowrap n-resize nw-resize oblique olive once open-quote outset '+
						'outside overline pointer portrait pre print projection purple red relative repeat repeat-x repeat-y rgb ridge right right-side '+
						'rightwards rtl run-in screen scroll semi-condensed semi-expanded separate se-resize show silent silver slower slow '+
						'small small-caps small-caption smaller soft solid speech spell-out square s-resize static status-bar sub super sw-resize '+
						'table-caption table-cell table-column table-column-group table-footer-group table-header-group table-row table-row-group teal '+
						'text-bottom text-top thick thin top transparent tty tv ultra-condensed ultra-expanded underline upper-alpha uppercase upper-latin '+
						'upper-roman url visible wait white wider w-resize x-fast x-high x-large x-loud x-low x-slow x-small x-soft xx-large xx-small yellow';
		var fonts =		'[mM]onospace [tT]ahoma [vV]erdana [aA]rial [hH]elvetica [sS]ans-serif [sS]erif [cC]ourier mono sans serif';
		var statements		= '!important !default';
		var preprocessor	= '@import @extend @debug @warn @if @for @while @mixin @include';
		var r = SyntaxHighlighter.regexLib;
		this.regexList = [
			{ regex: r.multiLineCComments,								css: 'comments' },		// multiline comments
			{ regex: r.singleLineCComments,								css: 'comments' },		// singleline comments
			{ regex: r.doubleQuotedString,								css: 'string' },		// double quoted strings
			{ regex: r.singleQuotedString,								css: 'string' },		// single quoted strings
			{ regex: /\#[a-fA-F0-9]{3,6}/g,								css: 'value' },			// html colors
			{ regex: /\b(-?\d+)(\.\d+)?(px|em|pt|\:|\%|)\b/g,			css: 'value' },			// sizes
			{ regex: /\$\w+/g,											css: 'variable' },		// variables
			{ regex: new RegExp(this.getKeywords(statements), 'g'),		css: 'color3' },		// statements
			{ regex: new RegExp(this.getKeywords(preprocessor), 'g'),	css: 'preprocessor' },	// preprocessor
			{ regex: new RegExp(getKeywordsCSS(keywords), 'gm'),		css: 'keyword' },		// keywords
			{ regex: new RegExp(getValuesCSS(values), 'g'),				css: 'value' },			// values
			{ regex: new RegExp(this.getKeywords(fonts), 'g'),			css: 'color1' }			// fonts
			];
	};
	BrushSass.prototype	= new SyntaxHighlighter.Highlighter();
	BrushSass.aliases	= ['sass', 'scss'];
	SyntaxHighlighter.brushes.Sass = BrushSass;
        function BrushScala()
	{
		// Contributed by Yegor Jbanov and David Bernard.
		var keywords =	'val sealed case def true trait implicit forSome import match object null finally super ' +
						'override try lazy for var catch throw type extends class while with new final yield abstract ' +
						'else do if return protected private this package false';
		var keyops =	'[_:=><%#@]+';
		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,			css: 'comments' },	// one line comments
			{ regex: SyntaxHighlighter.regexLib.multiLineCComments,				css: 'comments' },	// multiline comments
			{ regex: SyntaxHighlighter.regexLib.multiLineSingleQuotedString,	css: 'string' },	// multi-line strings
			{ regex: SyntaxHighlighter.regexLib.multiLineDoubleQuotedString,    css: 'string' },	// double-quoted string
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,				css: 'string' },	// strings
			{ regex: /0x[a-f0-9]+|\d+(\.\d+)?/gi,								css: 'value' },		// numbers
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),				css: 'keyword' },	// keywords
			{ regex: new RegExp(keyops, 'gm'),									css: 'keyword' }	// scala keyword
			];
	}
	BrushScala.prototype	= new SyntaxHighlighter.Highlighter();
	BrushScala.aliases	= ['scala'];
	SyntaxHighlighter.brushes.Scala = BrushScala;
        function BrushSQL()
	{
		var funcs	=	'abs avg case cast coalesce convert count current_timestamp ' +
						'current_user day isnull left lower month nullif replace right ' +
						'session_user space substring sum system_user upper user year';
		var keywords =	'absolute action add after alter as asc at authorization begin bigint ' +
						'binary bit by cascade char character check checkpoint close collate ' +
						'column commit committed connect connection constraint contains continue ' +
						'create cube current current_date current_time cursor database date ' +
						'deallocate dec decimal declare default delete desc distinct double drop ' +
						'dynamic else end end-exec escape except exec execute false fetch first ' +
						'float for force foreign forward free from full function global goto grant ' +
						'group grouping having hour ignore index inner insensitive insert instead ' +
						'int integer intersect into is isolation key last level load local max min ' +
						'minute modify move name national nchar next no numeric of off on only ' +
						'open option order out output partial password precision prepare primary ' +
						'prior privileges procedure public read real references relative repeatable ' +
						'restrict return returns revoke rollback rollup rows rule schema scroll ' +
						'second section select sequence serializable set size smallint static ' +
						'statistics table temp temporary then time timestamp to top transaction ' +
						'translation trigger true truncate uncommitted union unique update values ' +
						'varchar varying view when where with work';
		var operators =	'all and any between cross in join like not null or outer some';
		this.regexList = [
			{ regex: /--(.*)$/gm,												css: 'comments' },			// one line and multiline comments
			{ regex: SyntaxHighlighter.regexLib.multiLineDoubleQuotedString,	css: 'string' },			// double quoted strings
			{ regex: SyntaxHighlighter.regexLib.multiLineSingleQuotedString,	css: 'string' },			// single quoted strings
			{ regex: new RegExp(this.getKeywords(funcs), 'gmi'),				css: 'color2' },			// functions
			{ regex: new RegExp(this.getKeywords(operators), 'gmi'),			css: 'color1' },			// operators and such
			{ regex: new RegExp(this.getKeywords(keywords), 'gmi'),				css: 'keyword' }			// keyword
			];
	};
	BrushSQL.prototype	= new SyntaxHighlighter.Highlighter();
	BrushSQL.aliases	= ['sql'];
	SyntaxHighlighter.brushes.Sql = BrushSQL;
        	function BrushVb()
	{
		var keywords =	'AddHandler AddressOf AndAlso Alias And Ansi As Assembly Auto ' +
						'Boolean ByRef Byte ByVal Call Case Catch CBool CByte CChar CDate ' +
						'CDec CDbl Char CInt Class CLng CObj Const CShort CSng CStr CType ' +
						'Date Decimal Declare Default Delegate Dim DirectCast Do Double Each ' +
						'Else ElseIf End Enum Erase Error Event Exit False Finally For Friend ' +
						'Function Get GetType GoSub GoTo Handles If Implements Imports In ' +
						'Inherits Integer Interface Is Let Lib Like Long Loop Me Mod Module ' +
						'MustInherit MustOverride MyBase MyClass Namespace New Next Not Nothing ' +
						'NotInheritable NotOverridable Object On Option Optional Or OrElse ' +
						'Overloads Overridable Overrides ParamArray Preserve Private Property ' +
						'Protected Public RaiseEvent ReadOnly ReDim REM RemoveHandler Resume ' +
						'Return Select Set Shadows Shared Short Single Static Step Stop String ' +
						'Structure Sub SyncLock Then Throw To True Try TypeOf Unicode Until ' +
						'Variant When While With WithEvents WriteOnly Xor';
		this.regexList = [
			{ regex: /'.*$/gm,										css: 'comments' },			// one line comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,	css: 'string' },			// strings
			{ regex: /^\s*#.*$/gm,									css: 'preprocessor' },		// preprocessor tags like #region and #endregion
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),	css: 'keyword' }			// vb keyword
			];
		this.forHtmlScript(SyntaxHighlighter.regexLib.aspScriptTags);
	};
	BrushVb.prototype	= new SyntaxHighlighter.Highlighter();
	BrushVb.aliases	= ['vb', 'vbnet'];
	SyntaxHighlighter.brushes.Vb = BrushVb;
        function BrushXml()
	{
		function process(match, regexInfo)
		{
			var constructor = SyntaxHighlighter.Match,
				code = match[0],
				tag = new XRegExp('(&lt;|<)[\\s\\/\\?]*(?<name>[:\\w-\\.]+)', 'xg').exec(code),
				result = [];
			if (match.attributes != null) 
			{
				var attributes,
					regex = new XRegExp('(?<name> [\\w:\\-\\.]+)' +
										'\\s*=\\s*' +
										'(?<value> ".*?"|\'.*?\'|\\w+)',
										'xg');
				while ((attributes = regex.exec(code)) != null) 
				{
					result.push(new constructor(attributes.name, match.index + attributes.index, 'color1'));
					result.push(new constructor(attributes.value, match.index + attributes.index + attributes[0].indexOf(attributes.value), 'string'));
				}
			}
			if (tag != null)
				result.push(
					new constructor(tag.name, match.index + tag[0].indexOf(tag.name), 'keyword')
				);
			return result;
		}
		this.regexList = [
			{ regex: new XRegExp('(\\&lt;|<)\\!\\[[\\w\\s]*?\\[(.|\\s)*?\\]\\](\\&gt;|>)', 'gm'),			css: 'color2' },	// <![ ... [ ... ]]>
			{ regex: SyntaxHighlighter.regexLib.xmlComments,												css: 'comments' },	// <!-- ... -->
			{ regex: new XRegExp('(&lt;|<)[\\s\\/\\?]*(\\w+)(?<attributes>.*?)[\\s\\/\\?]*(&gt;|>)', 'sg'), func: process }
		];
	};
	BrushXml.prototype	= new SyntaxHighlighter.Highlighter();
	BrushXml.aliases	= ['xml', 'xhtml', 'xslt', 'html'];
	SyntaxHighlighter.brushes.Xml = BrushXml;
})();
