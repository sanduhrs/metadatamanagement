$(document).ready(	
		 $(".datepicker").each(function() {
			 locale = $('html').attr('lang');
			$.datepicker.setDefaults($.datepicker.regional[locale]);
			$(this).datepicker({
				altField :"#"+$(this).attr('id')+"_alt",
				altFormat : "yy-mm-dd",
				gotoCurrent : true,
				onSelect : function() {
					$("form").trigger("oninput");
				},
				changeMonth : true,
				changeYear : true,
				yearRange: '1970:2040',
				firstDay: 1 
			}).on(
					'keyup',
					function() {
						$(this).datepicker("hide");
						var date='';
						try {
							switch(locale) {
						    case 'de':
						    	date = $.datepicker.parseDate('dd.mm.yy', $(this).val());
						        break;
						    case 'en':
						    	date = $.datepicker.parseDate('mm/dd/yy', $(this).val());
						        break;
						    default:
						        break;
						    }
								var day = ("0" + (date.getDate())).slice(-2);
								var year = date.getFullYear();
								// JavaScript months are 0-11
								var month = ("0" + (date.getMonth() + 1))
										.slice(-2);
								$("#"+$(this).attr('id')+"_alt").val(
										year + '-' + month + '-' + day);
						} catch (e) {
							$("#"+$(this).attr('id')+"_alt").val('invalid date');
						}
					});
		 })
		);
		 
