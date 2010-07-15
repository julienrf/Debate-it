$(document).ready(function(){
  
  setupPostTools();
  
  setupPostRolling();
  
  setupFootNotes();
  
  setupCollapsibles();
  hideCollapsibles();
  
  //hideReadPosts();
});

/** Ajoute le comportement permettant d’afficher/cacher les outils d’un paragraphe quand l’utilisateur le survole
 * ainsi que le comportement lors du clic sur les boutons permettant d’afficher ou cacher les réponses d’un paragraphe  */
function setupPostTools() {
  // Cache les outils des posts
  $(".post .tools .other-tools").hide();
  
  // Affiche les outils des paragraphes quand le curseur passe au-dessus de ceux-ci
  $(".post .container").hover(
		function(event) { $(this).find(".subcontainer .tools .other-tools").show(); },
	  	function(event) { $(this).find(".subcontainer .tools .other-tools").hide(); }
  );
  
  // Configure le comportement du bouton « Afficher les réponses »
  $(".post .tools a.show-answers").click(function(event) {
	  event.preventDefault();
	  showAnswers($(this).parents(".container"));
	  return false;
  	});
  
  // Configure le comportement du bouton « Cacher les réponses »
  $(".post .tools a.hide-answers").click(function(event) {
	  event.preventDefault();
	  hideAnswers($(this).parents(".container"));
	  return false;
  	});
}

/** Ajoute le comportement permettant d’enrouler/dérouler un paragraphe */
function setupPostRolling() {
  // Configure l’enroulement/déroulement d’un message
	$(".post .title .roll").unbind('click');
	$(".post .title .roll").click(function(event) {
		event.preventDefault();
		togglePost($(this).parent().parent());
		return false;
	});
}

/** Modifie les identifiants des footnotes pour obtenir une liste de nombres consécutifs */
function setupFootNotes() {
	var footnotes = $('#footnotes');
	footnotes.children().each(function(index) {
		var ref = $(this).find('a[href*=ref-footnote_]');
		var id = ref.attr('href').substring(14); // Retrieve the footnote identifier, from its id attribute
		$('#ref-footnote_' + id + ' a').html(index + 1);
		ref.html('&uarr;');
	});
}

/** Setup the collapsible behavior */
function setupCollapsibles() {
	$('.collapsible a').click(function(event) {
		event.preventDefault();
		$(this).next().slideToggle('fast');
		return false;
	});
}

function hideCollapsibles() {
	$('.collapsible div').hide();
}


/** Enroule ou déroule un post */
function togglePost(post) {
	post.children(".container").slideToggle('fast');
	if (post.find(".title .preview").length > 0) { // unroll
		post.find(".title .preview").remove();
		post.find(".title .roll").html('&darr;');
	} else { // roll
		var content = post.find(".container").first().children(".content").clone();
		content.addClass("preview");
		post.find(".title .author").after(content);
		post.find(".title .roll").html('&rarr;');
	}
}

/** Enroule les messages lus dont il y a des réponses */
function hideReadPosts() {
  $(".post .container.read").parent(".post").siblings(".answer").prev(".post").children(".container").hide();
}

/** Compacte l’affichage d’un post */
function hideAnswers(paragraph) {
	var post = paragraph.parents(".post");
	
	// Supprine les footnotes des réponses
	var footnotes = post.next(".answer").find("a[href*=footnote_]");
	footnotes.each(function(index) {
		var id = $(this).attr("href").substring(10);
		$("#footnote_" + id).parent().remove();
	});
	// Supprime les réponses
	post.next(".answer").remove();
	
	var nextPost = post.next(".post");
	if (nextPost.length > 0) { // Est-ce que le post comportait d’autres paragraphes ?
		// Supprime le div.title du post du dessous
		nextPost.children(".title").remove();
		// Colle le post du dessous à la fin de celui-ci
		post.append(nextPost.children(".container"));
		// Supprime le post du dessous
		nextPost.remove();
	}
	// Remplace le lien « Cacher les réponses » par « Afficher les réponses »
	paragraph.find(".tools a.hide-answers").replaceWith('<a class="show-answers" href="#" title="' + i18n('showPostAnswers') + '">' + i18n('showAnswers') + '</a>');
	paragraph.find(".tools a.show-answers").click(function(event) {
			event.preventDefault();
			showAnswers($(this).parents(".container"));
			return false;
		});
	setupFootNotes();
}

/** Affiche les réponses d’un paragraphe d’un post compacté */
function showAnswers(paragraph) {
	var post = paragraph.parents(".post");
	var title = post.children(".title");
	var id = paragraph.attr('id').substr(10);
	if (paragraph.nextAll().length > 0) {
		// Crée un nouveau post pour y mettre les paragraphes qui suivent
		post.after('<div class="post box"></div>');
		var nextPost = post.next(".post");
		nextPost.append(title.clone());
		nextPost.append(paragraph.nextAll());
	}
	post.removeClass("unread");
	post.addClass("read");
	post.after('<img src="public/images/ajax-loader.gif" />');
	// Ajoute les réponses et les notes de bas de page correspondantes
	$.get(getAnswers({paragraphId: id}), function(data) {
		var answers = $(data).children("#answers").children();
		var footnotes = $(data).children("#footnotes").children();
		post.next("img").remove();
		post.after(answers);
		$("#footnotes").append(footnotes);
		setupPostTools();
		setupPostRolling();
		setupFootNotes();
	});
	
	// Remplace le lien « Afficher les réponses » par « Cacher les réponses »
	paragraph.find(".tools a.show-answers").replaceWith('<a class="hide-answers" href="#" title="' + i18n('hidePostAnswers') + '">' + i18n('hideAnswers') + '</a>');
}
