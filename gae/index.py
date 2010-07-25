import sys
import cgi
import os

from google.appengine.ext.webapp      import template
from google.appengine.api             import users
from google.appengine.ext             import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from app.controllers.home    import Home
from app.controllers.stream  import Stream

webapp.template.register_template_library('app.helpers')

application = webapp.WSGIApplication(
									 [('/', Home),
									  (r'/(.*)', Stream)],
									 debug = True)

# Duh, it's the main!
def main():
	run_wsgi_app(application)

# And its friend, __main__ !
if __name__ == "__main__":
	main()
