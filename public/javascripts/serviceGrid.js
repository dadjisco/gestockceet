jQuery(document).ready(function(){
	//cache DOM elements
	var mainContent = $('.cd-main-content'),
		header = $('.header-main'),
		sidebar = $('.cd-side-nav'),
		sidebarTrigger = $('.cd-nav-trigger'),
		topNavigation = $('.cd-top-nav'),
		searchForm = $('.cd-search'),
		accountInfo = $('.account');

	//on resize, move search and top nav position according to window width
	var resizing = false;
	moveNavigation();
	$(window).on('resize', function(){
		if( !resizing ) {
			(!window.requestAnimationFrame) ? setTimeout(moveNavigation, 300) : window.requestAnimationFrame(moveNavigation);
			resizing = true;
		}
	});



	//on window scrolling - fix sidebar nav
	var scrolling = false;
	checkScrollbarPosition();
	$(window).on('scroll', function(){
		if( !scrolling ) {
			(!window.requestAnimationFrame) ? setTimeout(checkScrollbarPosition, 300) : window.requestAnimationFrame(checkScrollbarPosition);
			scrolling = true;
		}
	});

	//mobile only - open sidebar when user clicks the hamburger menu
	sidebarTrigger.on('click', function(event){
		event.preventDefault();
		$([sidebar, sidebarTrigger]).toggleClass('nav-is-visible');
	});

	//click on item and show submenu
	$('.sub_link > a').on('click', function(event){
		var mq = checkMQ(),
			activeItem = $(this);
		if( mq == 'mobile' || mq == 'tablet' ) {
			event.preventDefault();
			if( activeItem.parent('li').hasClass('active')) {
				activeItem.parent('li').removeClass('active');
			} else {
				sidebar.find('.sub_link.active').removeClass('active');
				accountInfo.removeClass('active');
				activeItem.parent('li').addClass('active');
			}
		}else if( mq == 'desktop') {
			event.preventDefault();
			accountInfo.toggleClass('active');
			sidebar.find('.sub_link.active').removeClass('active');
		}
	});



	$(document).on('click', function(event){
		if( !$(event.target).is('.sub_link a') ) {
			sidebar.find('.sub_link.active').removeClass('active');
			accountInfo.removeClass('active');
		}
	});

	//on desktop - differentiate between a user trying to hover over a dropdown item vs trying to navigate into a submenu's contents
	sidebar.children('ul').menuAim({
        activate: function(row) {
        	$(row).addClass('selected');
        	$(row).addClass('hover');
        	
        },
        deactivate: function(row) {
        	$(row).removeClass('selected');
        	$(row).removeClass('hover');
        	
        },
        exitMenu: function() {
        	sidebar.find('.hover').removeClass('hover');
        	sidebar.find('.selected').removeClass('selected');
        	return true;
        },
        submenuSelector: ".sub_link",
    });

	function checkMQ() {
		// check if mobile or desktop device
		return window.getComputedStyle(document.querySelector('.cd-main-content'), '::before').getPropertyValue('content').replace(/'/g, "").replace(/"/g, "");
	}

	function moveNavigation(){
  		var mq = checkMQ();
        
        if ( mq == 'mobile' && topNavigation.parents('.cd-main-content').length == 0 ) {
        	detachElements();
			topNavigation.appendTo(sidebar);
			searchForm.removeClass('is-hidden').prependTo(sidebar);
		} else if ( ( mq == 'tablet' || mq == 'desktop') &&  topNavigation.parents('.cd-side-nav').length > 0 ) {
			detachElements();
			searchForm.insertAfter(header.find('.cd-logo'));
			topNavigation.appendTo(header.find('.cd-nav'));
		}
		checkactive(mq);
		resizing = false;
	}

	function detachElements() {
		topNavigation.detach();
		searchForm.detach();
	}

	function checkactive(mq) {
		//on desktop, remove active class from items active on mobile/tablet version
		if( mq == 'desktop' ) $('.sub_link.active').removeClass('active');
	}

	function checkScrollbarPosition() {
		var mq = checkMQ();
		
		if( mq != 'mobile' ) {
			var sidebarHeight = sidebar.outerHeight(),
				windowHeight = $(window).height(),
				mainContentHeight = mainContent.outerHeight(),
				scrollTop = $(window).scrollTop();

			( ( scrollTop + windowHeight > sidebarHeight ) && ( mainContentHeight - sidebarHeight != 0 ) ) ? sidebar.addClass('is-fixed').css('bottom',0) : sidebar.removeClass('is-fixed').attr('style', '');
		}
		scrolling = false;
	}


	$('.sub_link > a').on('click', function(event){
		var currentItem =$(this);
		$(currentItem).parent('li').addClass("active");

	});

	
	$(document).on('click'),function(event){
		if( !$(event.target).is('.sub_link a') )
		{

			sidebar.find('sub_link active').removeClass('active');
		}
	}
});