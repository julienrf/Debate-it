window.addEvent('domready', function() {
	$$('.editor').each(function(editor) {
		editor.addEvent('focus', function() {
			editor.getChildren('.bold').each(function(button) {
				button.addEvent('click', function() {
					alert("yay!");
				});
			});
		});
	});

	$$('.bold').each(function(button) {
		button.addEvent('click', function() {
			alert("yay!");
		});
	});
})
