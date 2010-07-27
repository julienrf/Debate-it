import cgi
import os

from google.appengine.ext.webapp import template
from google.appengine.ext import webapp

class Debate(webapp.RequestHandler):
	def get(self, debateHash=None):
		vars = {}
		path = os.path.join(os.path.dirname(__file__), '../views/debate.html')
		self.response.out.write(template.render(path, vars))
