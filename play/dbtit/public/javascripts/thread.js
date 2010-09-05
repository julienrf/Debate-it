$(document).ready(function(){
  
  setupPostTools();
  
  setupPostRolling();
  
  setupFootNotes();
  
  setupCollapsibles();
  hideCollapsibles();
  
  showFlashMessage();
  
  //hideReadPosts();
});

/** Ajoute le comportement permettant d’afficher/cacher les outils d’un paragraphe quand l’utilisateur le survole
 * ainsi que le comportement lors du clic sur les boutons permettant d’afficher ou cacher les réponses d’un paragraphe
 */
function setupPostTools() {
  // Cache les outils des posts
  $(".post .other-tools").hide();
  
  // Affiche les outils des paragraphes quand le curseur passe au-dessus de ceux-ci  * TODO le faire en CSS
  $(".paragraph .container").hover(
		function(event) { $(this).find(".other-tools").show(); },
	  	function(event) { $(this).find(".other-tools").hide(); }
  );
  
  // Configure le comportement du bouton « Marquer comme lu »
  $(".post a.readPost").click(function(event) {
	  event.preventDefault();
	  read($(this).closest(".post"));
	  return false;
  	});
  
  // Configure le comportement du bouton « Afficher les réponses »
  $(".post a.show-answers").click(function(event) {
	  event.preventDefault();
	  showAnswers($(this).closest(".paragraph"));
	  return false;
  	});
  
  // Configure le comportement du bouton « Cacher les réponses »
  $(".post a.hide-answers").click(function(event) {
	  event.preventDefault();
	  hideAnswers($(this).closest(".paragraph"));
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
	$('.collapsible .content').hide();
}


/** Enroule ou déroule un post */
function togglePost(post) {
	var title = post.children(".title");
	
	if (title.hasClass("rolled")) {
		title.removeClass("rolled");
		title.find(".preview").remove();
		title.find(".roll").html('&darr;');
		post.children(".paragraph").slideDown('fast');
	} else {
		title.addClass("rolled");
		var content = post.children(".paragraph").first().find(".content").first().clone();
		content.addClass("preview");
		title.find(".author").after(content);
		title.find(".roll").html('&rarr;');
		post.children(".paragraph").slideUp('fast');
	}
}

/** Enroule les messages lus dont il y a des réponses */
function hideReadPosts() {
  //$(".post .container.read").parent(".post").siblings(".answer").prev(".post").children(".container").hide();
}

/** Marque un post comme lu */
function read(post) {
	var id = post.attr('id').substring(5);

	$.ajax({
		type: 'PUT',
		url: readPost({postId: id}),
		success: function(data, textStatus, XMLHttpRequest) {
				post.removeClass('unread');
				post.addClass('read');
				post.find("a.readPost").remove();
			}
	});
}

/** Compacte l’affichage d’un post */
function hideAnswers(paragraph) {
	// Supprine les footnotes des réponses
	var footnotes = paragraph.children(".answer").find("a[href*=footnote_]");
	footnotes.each(function(index) {
		var id = $(this).attr("href").substring(10);
		$("#footnote_" + id).parent().remove();
	});
	// Supprime les réponses
	paragraph.children(".answer").remove();
	
	// Remplace le lien « Cacher les réponses » par « Afficher les réponses »
	paragraph.find("a.hide-answers").replaceWith('<a class="sprite show-answers" href="#" title="' + i18n('showPostAnswers') + '" alt="' + i18n('showPostAnswers') + '"></a>');
	paragraph.find("a.show-answers").click(function(event) {
			event.preventDefault();
			showAnswers($(this).closest(".paragraph")); // TODO factoriser avec le code setupPostTools ou faire un truc propre
			return false;
		});
	setupFootNotes();
}

/** Affiche les réponses d’un paragraphe d’un post compacté */
function showAnswers(paragraph) {
	var post = paragraph.parents(".post");
	var id = paragraph.attr('id').substr(10);

	paragraph.append('<img src="public/images/ajax-loader.gif" />');
	// Ajoute les réponses et les notes de bas de page correspondantes
	$.get(getAnswers({paragraphId: id}), function(data) {
		var answers = $(data).children("#answers").children();
		var footnotes = $(data).children("#footnotes").children();
		paragraph.children("img").remove();
		paragraph.append(answers);
		$("#footnotes").append(footnotes);
		setupPostTools(); // Que c’est laid, tous ces setup* appelés 50 fois…
		setupPostRolling();
		setupFootNotes();
	});
	
	// Remplace le lien « Afficher les réponses » par « Cacher les réponses »
	paragraph.find(".tools a.show-answers").replaceWith('<a class="sprite hide-answers" href="#" title="' + i18n('hidePostAnswers') + '" alt="' + i18n('hidePostAnswers') + '"></a>');
}


/**
 * Affiche un éventuel flash message avec un joli effet de transition
 */
function showFlashMessage() {
	var node = $('.flash');
	if (node.length > 0) {
		node.slideToggle('fast');
		node.click(function(event) {
			node.slideToggle('fast');
		});
	}
}
