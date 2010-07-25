import cgi
import os

from google.appengine.ext.webapp import template
from google.appengine.ext import webapp

import yaml
import markdown

class Home(webapp.RequestHandler):
	def get(self):
		langheader = self.request.headers['Accept-Language']
        
		if langheader.startswith('fr'):
			langfile = file(os.path.join(os.path.dirname(__file__), '../i18n/fr/home.yaml'))
		else:
			langfile = file(os.path.join(os.path.dirname(__file__), '../i18n/en/home.yaml'))

		vars = {
		  "lang": yaml.load(langfile)
		}
		langfile.close()
		
		path = os.path.join(os.path.dirname(__file__), '../views/home.html')
		self.response.out.write(template.render(path, vars))
