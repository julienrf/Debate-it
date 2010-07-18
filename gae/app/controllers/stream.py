import cgi
import os

from google.appengine.ext.webapp import template
from google.appengine.ext import webapp

class Stream(webapp.RequestHandler):
	def get(self):
		vars = {}
		path = os.path.join(os.path.dirname(__file__), '../views/home.html')
		self.response.out.write(template.render(path, vars))
