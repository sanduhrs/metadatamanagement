/*! modernizr 3.3.1 (Custom Build) | MIT *
 * http://modernizr.com/download/?-ambientlight-applicationcache-audio-canvas-canvastext-contenteditable-contextmenu-cookies-cors-customevent-dart-emoji-eventlistener-flash-geolocation-hashchange-hiddenscroll-history-htmlimports-ie8compat-indexeddb-indexeddbblob-input-inputtypes-intl-json-mathml-notification-performance-pointerevents-postmessage-proximity-queryselector-requestanimationframe-serviceworker-svg-templatestrings-touchevents-unicode-userdata-video-vml-webgl-websockets-xdomainrequest-addtest-atrule-domprefixes-hasevent-mq-prefixed-prefixedcss-prefixedcssvalue-prefixes-printshiv-setclasses-testallprops-testprop-teststyles !*/
!function(window,document,undefined){function is(e,t){return typeof e===t}function testRunner(){var e,t,n,r,o,i,d;for(var a in tests)if(tests.hasOwnProperty(a)){if(e=[],t=tests[a],t.name&&(e.push(t.name.toLowerCase()),t.options&&t.options.aliases&&t.options.aliases.length))for(n=0;n<t.options.aliases.length;n++)e.push(t.options.aliases[n].toLowerCase());for(r=is(t.fn,"function")?t.fn():t.fn,o=0;o<e.length;o++)i=e[o],d=i.split("."),1===d.length?Modernizr[d[0]]=r:(!Modernizr[d[0]]||Modernizr[d[0]]instanceof Boolean||(Modernizr[d[0]]=new Boolean(Modernizr[d[0]])),Modernizr[d[0]][d[1]]=r),classes.push((r?"":"no-")+d.join("-"))}}function setClasses(e){var t=docElement.className,n=Modernizr._config.classPrefix||"";if(isSVG&&(t=t.baseVal),Modernizr._config.enableJSClass){var r=new RegExp("(^|\\s)"+n+"no-js(\\s|$)");t=t.replace(r,"$1"+n+"js$2")}Modernizr._config.enableClasses&&(t+=" "+n+e.join(" "+n),isSVG?docElement.className.baseVal=t:docElement.className=t)}function addTest(e,t){if("object"==typeof e)for(var n in e)hasOwnProp(e,n)&&addTest(n,e[n]);else{e=e.toLowerCase();var r=e.split("."),o=Modernizr[r[0]];if(2==r.length&&(o=o[r[1]]),"undefined"!=typeof o)return Modernizr;t="function"==typeof t?t():t,1==r.length?Modernizr[r[0]]=t:(!Modernizr[r[0]]||Modernizr[r[0]]instanceof Boolean||(Modernizr[r[0]]=new Boolean(Modernizr[r[0]])),Modernizr[r[0]][r[1]]=t),setClasses([(t&&0!=t?"":"no-")+r.join("-")]),Modernizr._trigger(e,t)}return Modernizr}function createElement(){return"function"!=typeof document.createElement?document.createElement(arguments[0]):isSVG?document.createElementNS.call(document,"http://www.w3.org/2000/svg",arguments[0]):document.createElement.apply(document,arguments)}function getBody(){var e=document.body;return e||(e=createElement(isSVG?"svg":"body"),e.fake=!0),e}function injectElementWithStyles(e,t,n,r){var o,i,d,a,s="modernizr",c=createElement("div"),l=getBody();if(parseInt(n,10))for(;n--;)d=createElement("div"),d.id=r?r[n]:s+(n+1),c.appendChild(d);return o=createElement("style"),o.type="text/css",o.id="s"+s,(l.fake?l:c).appendChild(o),l.appendChild(c),o.styleSheet?o.styleSheet.cssText=e:o.appendChild(document.createTextNode(e)),c.id=s,l.fake&&(l.style.background="",l.style.overflow="hidden",a=docElement.style.overflow,docElement.style.overflow="hidden",docElement.appendChild(l)),i=t(c,e),l.fake?(l.parentNode.removeChild(l),docElement.style.overflow=a,docElement.offsetHeight):c.parentNode.removeChild(c),!!i}function contains(e,t){return!!~(""+e).indexOf(t)}function domToCSS(e){return e.replace(/([A-Z])/g,function(e,t){return"-"+t.toLowerCase()}).replace(/^ms-/,"-ms-")}function nativeTestProps(e,t){var n=e.length;if("CSS"in window&&"supports"in window.CSS){for(;n--;)if(window.CSS.supports(domToCSS(e[n]),t))return!0;return!1}if("CSSSupportsRule"in window){for(var r=[];n--;)r.push("("+domToCSS(e[n])+":"+t+")");return r=r.join(" or "),injectElementWithStyles("@supports ("+r+") { #modernizr { position: absolute; } }",function(e){return"absolute"==getComputedStyle(e,null).position})}return undefined}function cssToDOM(e){return e.replace(/([a-z])-([a-z])/g,function(e,t,n){return t+n.toUpperCase()}).replace(/^-/,"")}function testProps(e,t,n,r){function o(){d&&(delete mStyle.style,delete mStyle.modElem)}if(r=is(r,"undefined")?!1:r,!is(n,"undefined")){var i=nativeTestProps(e,n);if(!is(i,"undefined"))return i}for(var d,a,s,c,l,u=["modernizr","tspan"];!mStyle.style;)d=!0,mStyle.modElem=createElement(u.shift()),mStyle.style=mStyle.modElem.style;for(s=e.length,a=0;s>a;a++)if(c=e[a],l=mStyle.style[c],contains(c,"-")&&(c=cssToDOM(c)),mStyle.style[c]!==undefined){if(r||is(n,"undefined"))return o(),"pfx"==t?c:!0;try{mStyle.style[c]=n}catch(f){}if(mStyle.style[c]!=l)return o(),"pfx"==t?c:!0}return o(),!1}function fnBind(e,t){return function(){return e.apply(t,arguments)}}function testDOMProps(e,t,n){var r;for(var o in e)if(e[o]in t)return n===!1?e[o]:(r=t[e[o]],is(r,"function")?fnBind(r,n||t):r);return!1}function testPropsAll(e,t,n,r,o){var i=e.charAt(0).toUpperCase()+e.slice(1),d=(e+" "+cssomPrefixes.join(i+" ")+i).split(" ");return is(t,"string")||is(t,"undefined")?testProps(d,t,r,o):(d=(e+" "+domPrefixes.join(i+" ")+i).split(" "),testDOMProps(d,t,n))}function testAllProps(e,t,n){return testPropsAll(e,undefined,undefined,t,n)}var tests=[],ModernizrProto={_version:"3.3.1",_config:{classPrefix:"",enableClasses:!0,enableJSClass:!0,usePrefixes:!0},_q:[],on:function(e,t){var n=this;setTimeout(function(){t(n[e])},0)},addTest:function(e,t,n){tests.push({name:e,fn:t,options:n})},addAsyncTest:function(e){tests.push({name:null,fn:e})}},Modernizr=function(){};Modernizr.prototype=ModernizrProto,Modernizr=new Modernizr;var classes=[],docElement=document.documentElement,isSVG="svg"===docElement.nodeName.toLowerCase(),omPrefixes="Moz O ms Webkit",domPrefixes=ModernizrProto._config.usePrefixes?omPrefixes.toLowerCase().split(" "):[];ModernizrProto._domPrefixes=domPrefixes;var prefixes=ModernizrProto._config.usePrefixes?" -webkit- -moz- -o- -ms- ".split(" "):[];ModernizrProto._prefixes=prefixes;var hasOwnProp;!function(){var e={}.hasOwnProperty;hasOwnProp=is(e,"undefined")||is(e.call,"undefined")?function(e,t){return t in e&&is(e.constructor.prototype[t],"undefined")}:function(t,n){return e.call(t,n)}}(),ModernizrProto._l={},ModernizrProto.on=function(e,t){this._l[e]||(this._l[e]=[]),this._l[e].push(t),Modernizr.hasOwnProperty(e)&&setTimeout(function(){Modernizr._trigger(e,Modernizr[e])},0)},ModernizrProto._trigger=function(e,t){if(this._l[e]){var n=this._l[e];setTimeout(function(){var e,r;for(e=0;e<n.length;e++)(r=n[e])(t)},0),delete this._l[e]}},Modernizr._q.push(function(){ModernizrProto.addTest=addTest});var cssomPrefixes=ModernizrProto._config.usePrefixes?omPrefixes.split(" "):[];ModernizrProto._cssomPrefixes=cssomPrefixes;var atRule=function(e){var t,n=prefixes.length,r=window.CSSRule;if("undefined"==typeof r)return undefined;if(!e)return!1;if(e=e.replace(/^@/,""),t=e.replace(/-/g,"_").toUpperCase()+"_RULE",t in r)return"@"+e;for(var o=0;n>o;o++){var i=prefixes[o],d=i.toUpperCase()+"_"+t;if(d in r)return"@-"+i.toLowerCase()+"-"+e}return!1};ModernizrProto.atRule=atRule;var hasEvent=function(){function e(e,n){var r;return e?(n&&"string"!=typeof n||(n=createElement(n||"div")),e="on"+e,r=e in n,!r&&t&&(n.setAttribute||(n=createElement("div")),n.setAttribute(e,""),r="function"==typeof n[e],n[e]!==undefined&&(n[e]=undefined),n.removeAttribute(e)),r):!1}var t=!("onblur"in document.documentElement);return e}();ModernizrProto.hasEvent=hasEvent;var mq=function(){var e=window.matchMedia||window.msMatchMedia;return e?function(t){var n=e(t);return n&&n.matches||!1}:function(e){var t=!1;return injectElementWithStyles("@media "+e+" { #modernizr { position: absolute; } }",function(e){t="absolute"==(window.getComputedStyle?window.getComputedStyle(e,null):e.currentStyle).position}),t}}();ModernizrProto.mq=mq;var modElem={elem:createElement("modernizr")};Modernizr._q.push(function(){delete modElem.elem});var mStyle={style:modElem.elem.style};Modernizr._q.unshift(function(){delete mStyle.style}),ModernizrProto.testAllProps=testPropsAll;var prefixed=ModernizrProto.prefixed=function(e,t,n){return 0===e.indexOf("@")?atRule(e):(-1!=e.indexOf("-")&&(e=cssToDOM(e)),t?testPropsAll(e,t,n):testPropsAll(e,"pfx"))},prefixedCSS=ModernizrProto.prefixedCSS=function(e){var t=prefixed(e);return t&&domToCSS(t)},prefixedCSSValue=function(e,t){var n=!1,r=createElement("div"),o=r.style;if(e in o){var i=domPrefixes.length;for(o[e]=t,n=o[e];i--&&!n;)o[e]="-"+domPrefixes[i]+"-"+t,n=o[e]}return""===n&&(n=!1),n};ModernizrProto.prefixedCSSValue=prefixedCSSValue,ModernizrProto.testAllProps=testAllProps;var testProp=ModernizrProto.testProp=function(e,t,n){return testProps([e],undefined,t,n)},testStyles=ModernizrProto.testStyles=injectElementWithStyles,html5;isSVG||!function(e,t){function n(e,t){var n=e.createElement("p"),r=e.getElementsByTagName("head")[0]||e.documentElement;return n.innerHTML="x<style>"+t+"</style>",r.insertBefore(n.lastChild,r.firstChild)}function r(){var e=T.elements;return"string"==typeof e?e.split(" "):e}function o(e,t){var n=T.elements;"string"!=typeof n&&(n=n.join(" ")),"string"!=typeof e&&(e=e.join(" ")),T.elements=n+" "+e,c(t)}function i(e){var t=x[e[E]];return t||(t={},z++,e[E]=z,x[z]=t),t}function d(e,n,r){if(n||(n=t),v)return n.createElement(e);r||(r=i(n));var o;return o=r.cache[e]?r.cache[e].cloneNode():M.test(e)?(r.cache[e]=r.createElem(e)).cloneNode():r.createElem(e),!o.canHaveChildren||g.test(e)||o.tagUrn?o:r.frag.appendChild(o)}function a(e,n){if(e||(e=t),v)return e.createDocumentFragment();n=n||i(e);for(var o=n.frag.cloneNode(),d=0,a=r(),s=a.length;s>d;d++)o.createElement(a[d]);return o}function s(e,t){t.cache||(t.cache={},t.createElem=e.createElement,t.createFrag=e.createDocumentFragment,t.frag=t.createFrag()),e.createElement=function(n){return T.shivMethods?d(n,e,t):t.createElem(n)},e.createDocumentFragment=Function("h,f","return function(){var n=f.cloneNode(),c=n.createElement;h.shivMethods&&("+r().join().replace(/[\w\-:]+/g,function(e){return t.createElem(e),t.frag.createElement(e),'c("'+e+'")'})+");return n}")(T,t.frag)}function c(e){e||(e=t);var r=i(e);return!T.shivCSS||h||r.hasCSS||(r.hasCSS=!!n(e,"article,aside,dialog,figcaption,figure,footer,header,hgroup,main,nav,section{display:block}mark{background:#FF0;color:#000}template{display:none}")),v||s(e,r),e}function l(e){for(var t,n=e.getElementsByTagName("*"),o=n.length,i=RegExp("^(?:"+r().join("|")+")$","i"),d=[];o--;)t=n[o],i.test(t.nodeName)&&d.push(t.applyElement(u(t)));return d}function u(e){for(var t,n=e.attributes,r=n.length,o=e.ownerDocument.createElement(b+":"+e.nodeName);r--;)t=n[r],t.specified&&o.setAttribute(t.nodeName,t.nodeValue);return o.style.cssText=e.style.cssText,o}function f(e){for(var t,n=e.split("{"),o=n.length,i=RegExp("(^|[\\s,>+~])("+r().join("|")+")(?=[[\\s,>+~#.:]|$)","gi"),d="$1"+b+"\\:$2";o--;)t=n[o]=n[o].split("}"),t[t.length-1]=t[t.length-1].replace(i,d),n[o]=t.join("}");return n.join("{")}function p(e){for(var t=e.length;t--;)e[t].removeNode()}function m(e){function t(){clearTimeout(d._removeSheetTimer),r&&r.removeNode(!0),r=null}var r,o,d=i(e),a=e.namespaces,s=e.parentWindow;return!P||e.printShived?e:("undefined"==typeof a[b]&&a.add(b),s.attachEvent("onbeforeprint",function(){t();for(var i,d,a,s=e.styleSheets,c=[],u=s.length,p=Array(u);u--;)p[u]=s[u];for(;a=p.pop();)if(!a.disabled&&S.test(a.media)){try{i=a.imports,d=i.length}catch(m){d=0}for(u=0;d>u;u++)p.push(i[u]);try{c.push(a.cssText)}catch(m){}}c=f(c.reverse().join("")),o=l(e),r=n(e,c)}),s.attachEvent("onafterprint",function(){p(o),clearTimeout(d._removeSheetTimer),d._removeSheetTimer=setTimeout(t,500)}),e.printShived=!0,e)}var h,v,y="3.7.3",w=e.html5||{},g=/^<|^(?:button|map|select|textarea|object|iframe|option|optgroup)$/i,M=/^(?:a|b|code|div|fieldset|h1|h2|h3|h4|h5|h6|i|label|li|ol|p|q|span|strong|style|table|tbody|td|th|tr|ul)$/i,E="_html5shiv",z=0,x={};!function(){try{var e=t.createElement("a");e.innerHTML="<xyz></xyz>",h="hidden"in e,v=1==e.childNodes.length||function(){t.createElement("a");var e=t.createDocumentFragment();return"undefined"==typeof e.cloneNode||"undefined"==typeof e.createDocumentFragment||"undefined"==typeof e.createElement}()}catch(n){h=!0,v=!0}}();var T={elements:w.elements||"abbr article aside audio bdi canvas data datalist details dialog figcaption figure footer header hgroup main mark meter nav output picture progress section summary template time video",version:y,shivCSS:w.shivCSS!==!1,supportsUnknownElements:v,shivMethods:w.shivMethods!==!1,type:"default",shivDocument:c,createElement:d,createDocumentFragment:a,addElements:o};e.html5=T,c(t);var S=/^$|\b(?:all|print)\b/,b="html5shiv",P=!v&&function(){var n=t.documentElement;return!("undefined"==typeof t.namespaces||"undefined"==typeof t.parentWindow||"undefined"==typeof n.applyElement||"undefined"==typeof n.removeNode||"undefined"==typeof e.attachEvent)}();T.type+=" print",T.shivPrint=m,m(t),"object"==typeof module&&module.exports&&(module.exports=T)}("undefined"!=typeof window?window:this,document),Modernizr.addTest("ambientlight",hasEvent("devicelight",window)),Modernizr.addTest("applicationcache","applicationCache"in window),Modernizr.addTest("audio",function(){var e=createElement("audio"),t=!1;try{(t=!!e.canPlayType)&&(t=new Boolean(t),t.ogg=e.canPlayType('audio/ogg; codecs="vorbis"').replace(/^no$/,""),t.mp3=e.canPlayType('audio/mpeg; codecs="mp3"').replace(/^no$/,""),t.opus=e.canPlayType('audio/ogg; codecs="opus"').replace(/^no$/,""),t.wav=e.canPlayType('audio/wav; codecs="1"').replace(/^no$/,""),t.m4a=(e.canPlayType("audio/x-m4a;")||e.canPlayType("audio/aac;")).replace(/^no$/,""))}catch(n){}return t}),Modernizr.addTest("canvas",function(){var e=createElement("canvas");return!(!e.getContext||!e.getContext("2d"))}),Modernizr.addTest("canvastext",function(){return Modernizr.canvas===!1?!1:"function"==typeof createElement("canvas").getContext("2d").fillText}),Modernizr.addTest("contenteditable",function(){if("contentEditable"in docElement){var e=createElement("div");return e.contentEditable=!0,"true"===e.contentEditable}}),Modernizr.addTest("contextmenu","contextMenu"in docElement&&"HTMLMenuItemElement"in window),Modernizr.addTest("cookies",function(){try{document.cookie="cookietest=1";var e=-1!=document.cookie.indexOf("cookietest=");return document.cookie="cookietest=1; expires=Thu, 01-Jan-1970 00:00:01 GMT",e}catch(t){return!1}}),Modernizr.addTest("cors","XMLHttpRequest"in window&&"withCredentials"in new XMLHttpRequest),Modernizr.addTest("customevent","CustomEvent"in window&&"function"==typeof window.CustomEvent),Modernizr.addTest("dart",!!prefixed("startDart",navigator)),Modernizr.addTest("emoji",function(){if(!Modernizr.canvastext)return!1;var e=window.devicePixelRatio||1,t=12*e,n=createElement("canvas"),r=n.getContext("2d");return r.fillStyle="#f00",r.textBaseline="top",r.font="32px Arial",r.fillText("🐨",0,0),0!==r.getImageData(t,t,1,1).data[0]}),Modernizr.addTest("eventlistener","addEventListener"in window),Modernizr.addAsyncTest(function(){var e,t,n=function(e){docElement.contains(e)||docElement.appendChild(e)},r=function(e){e.fake&&e.parentNode&&e.parentNode.removeChild(e)},o=function(e,t){var n=!!e;if(n&&(n=new Boolean(n),n.blocked="blocked"===e),addTest("flash",function(){return n}),t&&c.contains(t)){for(;t.parentNode!==c;)t=t.parentNode;c.removeChild(t)}};try{t="ActiveXObject"in window&&"Pan"in new window.ActiveXObject("ShockwaveFlash.ShockwaveFlash")}catch(i){}if(e=!("plugins"in navigator&&"Shockwave Flash"in navigator.plugins||t),e||isSVG)o(!1);else{var d,a,s=createElement("embed"),c=getBody();if(s.type="application/x-shockwave-flash",c.appendChild(s),!("Pan"in s||t))return n(c),o("blocked",s),void r(c);d=function(){return n(c),docElement.contains(c)?(docElement.contains(s)?(a=s.style.cssText,""!==a?o("blocked",s):o(!0,s)):o("blocked"),void r(c)):(c=document.body||c,s=createElement("embed"),s.type="application/x-shockwave-flash",c.appendChild(s),setTimeout(d,1e3))},setTimeout(d,10)}}),Modernizr.addTest("geolocation","geolocation"in navigator),Modernizr.addTest("hashchange",function(){return hasEvent("hashchange",window)===!1?!1:document.documentMode===undefined||document.documentMode>7}),Modernizr.addTest("hiddenscroll",function(){return testStyles("#modernizr {width:100px;height:100px;overflow:scroll}",function(e){return e.offsetWidth===e.clientWidth})}),Modernizr.addTest("history",function(){var e=navigator.userAgent;return-1===e.indexOf("Android 2.")&&-1===e.indexOf("Android 4.0")||-1===e.indexOf("Mobile Safari")||-1!==e.indexOf("Chrome")||-1!==e.indexOf("Windows Phone")?window.history&&"pushState"in window.history:!1}),addTest("htmlimports","import"in createElement("link")),Modernizr.addTest("ie8compat",!window.addEventListener&&!!document.documentMode&&7===document.documentMode);var indexeddb;try{indexeddb=prefixed("indexedDB",window)}catch(e){}Modernizr.addTest("indexeddb",!!indexeddb),indexeddb&&Modernizr.addTest("indexeddb.deletedatabase","deleteDatabase"in indexeddb),Modernizr.addAsyncTest(function(){var e,t,n,r="detect-blob-support",o=!1;try{e=prefixed("indexedDB",window)}catch(i){}if(!Modernizr.indexeddb||!Modernizr.indexeddb.deletedatabase)return!1;try{e.deleteDatabase(r).onsuccess=function(){t=e.open(r,1),t.onupgradeneeded=function(){t.result.createObjectStore("store")},t.onsuccess=function(){n=t.result;try{n.transaction("store","readwrite").objectStore("store").put(new Blob,"key"),o=!0}catch(i){o=!1}finally{addTest("indexeddbblob",o),n.close(),e.deleteDatabase(r)}}}}catch(i){addTest("indexeddbblob",!1)}});var inputElem=createElement("input"),inputattrs="autocomplete autofocus list placeholder max min multiple pattern required step".split(" "),attrs={};Modernizr.input=function(e){for(var t=0,n=e.length;n>t;t++)attrs[e[t]]=!!(e[t]in inputElem);return attrs.list&&(attrs.list=!(!createElement("datalist")||!window.HTMLDataListElement)),attrs}(inputattrs);var inputtypes="search tel url email datetime date month week time datetime-local number range color".split(" "),inputs={};Modernizr.inputtypes=function(e){for(var t,n,r,o=e.length,i="1)",d=0;o>d;d++)inputElem.setAttribute("type",t=e[d]),r="text"!==inputElem.type&&"style"in inputElem,r&&(inputElem.value=i,inputElem.style.cssText="position:absolute;visibility:hidden;",/^range$/.test(t)&&inputElem.style.WebkitAppearance!==undefined?(docElement.appendChild(inputElem),n=document.defaultView,r=n.getComputedStyle&&"textfield"!==n.getComputedStyle(inputElem,null).WebkitAppearance&&0!==inputElem.offsetHeight,docElement.removeChild(inputElem)):/^(search|tel)$/.test(t)||(r=/^(url|email)$/.test(t)?inputElem.checkValidity&&inputElem.checkValidity()===!1:inputElem.value!=i)),inputs[e[d]]=!!r;return inputs}(inputtypes),Modernizr.addTest("intl",!!prefixed("Intl",window)),Modernizr.addTest("json","JSON"in window&&"parse"in JSON&&"stringify"in JSON),Modernizr.addTest("mathml",function(){var e;return testStyles("#modernizr{position:absolute;display:inline-block}",function(t){t.innerHTML+="<math><mfrac><mi>xx</mi><mi>yy</mi></mfrac></math>",e=t.offsetHeight>t.offsetWidth}),e}),Modernizr.addTest("notification",function(){if(!window.Notification||!window.Notification.requestPermission)return!1;if("granted"===window.Notification.permission)return!0;try{new window.Notification("")}catch(e){if("TypeError"===e.name)return!1}return!0}),Modernizr.addTest("performance",!!prefixed("performance",window)),Modernizr.addTest("pointerevents",function(){var e=!1,t=domPrefixes.length;for(e=Modernizr.hasEvent("pointerdown");t--&&!e;)hasEvent(domPrefixes[t]+"pointerdown")&&(e=!0);return e}),Modernizr.addTest("postmessage","postMessage"in window),Modernizr.addAsyncTest(function(){function e(){clearTimeout(t),window.removeEventListener("deviceproximity",e),addTest("proximity",!0)}var t,n=300;"ondeviceproximity"in window&&"onuserproximity"in window?(window.addEventListener("deviceproximity",e),t=setTimeout(function(){window.removeEventListener("deviceproximity",e),addTest("proximity",!1)},n)):addTest("proximity",!1)}),Modernizr.addTest("queryselector","querySelector"in document&&"querySelectorAll"in document),Modernizr.addTest("requestanimationframe",!!prefixed("requestAnimationFrame",window),{aliases:["raf"]}),Modernizr.addTest("serviceworker","serviceWorker"in navigator),Modernizr.addTest("svg",!!document.createElementNS&&!!document.createElementNS("http://www.w3.org/2000/svg","svg").createSVGRect),Modernizr.addTest("templatestrings",function(){var supports;try{eval("``"),supports=!0}catch(e){}return!!supports}),Modernizr.addTest("touchevents",function(){var e;if("ontouchstart"in window||window.DocumentTouch&&document instanceof DocumentTouch)e=!0;else{var t=["@media (",prefixes.join("touch-enabled),("),"heartz",")","{#modernizr{top:9px;position:absolute}}"].join("");testStyles(t,function(t){e=9===t.offsetTop})}return e}),Modernizr.addTest("unicode",function(){var e,t=createElement("span"),n=createElement("span");return testStyles("#modernizr{font-family:Arial,sans;font-size:300em;}",function(r){t.innerHTML=isSVG?"妇":"&#5987",n.innerHTML=isSVG?"☆":"&#9734",r.appendChild(t),r.appendChild(n),e="offsetWidth"in t&&t.offsetWidth!==n.offsetWidth}),e}),Modernizr.addTest("userdata",!!createElement("div").addBehavior),Modernizr.addTest("video",function(){var e=createElement("video"),t=!1;try{(t=!!e.canPlayType)&&(t=new Boolean(t),t.ogg=e.canPlayType('video/ogg; codecs="theora"').replace(/^no$/,""),t.h264=e.canPlayType('video/mp4; codecs="avc1.42E01E"').replace(/^no$/,""),t.webm=e.canPlayType('video/webm; codecs="vp8, vorbis"').replace(/^no$/,""),t.vp9=e.canPlayType('video/webm; codecs="vp9"').replace(/^no$/,""),t.hls=e.canPlayType('application/x-mpegURL; codecs="avc1.42E01E"').replace(/^no$/,""))}catch(n){}return t}),Modernizr.addTest("vml",function(){var e,t=createElement("div"),n=!1;return isSVG||(t.innerHTML='<v:shape id="vml_flag1" adj="1" />',e=t.firstChild,e.style.behavior="url(#default#VML)",n=e?"object"==typeof e.adj:!0),n}),Modernizr.addTest("webgl",function(){var e=createElement("canvas"),t="probablySupportsContext"in e?"probablySupportsContext":"supportsContext";return t in e?e[t]("webgl")||e[t]("experimental-webgl"):"WebGLRenderingContext"in window}),Modernizr.addTest("websockets","WebSocket"in window&&2===window.WebSocket.CLOSING),Modernizr.addTest("xdomainrequest","XDomainRequest"in window),testRunner(),setClasses(classes),delete ModernizrProto.addTest,delete ModernizrProto.addAsyncTest;for(var i=0;i<Modernizr._q.length;i++)Modernizr._q[i]();window.Modernizr=Modernizr}(window,document);