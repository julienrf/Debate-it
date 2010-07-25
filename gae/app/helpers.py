import markdown

from google.appengine.ext import webapp

register = webapp.template.create_template_register()

def md(text):
	""" parses the markup string into HTML """
	return markdown.markdown(text)

register.filter(md)