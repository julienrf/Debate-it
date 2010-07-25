import cgi
import os
import yaml
import markdown

from google.appengine.ext        import webapp
from google.appengine.api        import users
from google.appengine.ext.webapp import template
from app.models.account          import Account

class Home(webapp.RequestHandler):
	def get(self):
		
		# Get the language header
		langheader = self.request.headers['Accept-Language']
        
        # Check for language
		if langheader.startswith('fr'):
			langfile = file(os.path.join(os.path.dirname(__file__), '../i18n/fr/home.yaml'))
		else:
			langfile = file(os.path.join(os.path.dirname(__file__), '../i18n/en/home.yaml'))

		user = users.get_current_user()

		if user:
			account = Account.gql("WHERE userid = :1", user).get()
			if account:
				vars = {
				  "lang": yaml.load(langfile),
				  "logouturl": users.create_logout_url(self.request.uri),
				  "username": account.username
				}
				path = os.path.join(os.path.dirname(__file__), '../views/userhome.html')
			else:
				vars = {
				  "lang": yaml.load(langfile),
				  "logouturl": users.create_logout_url(self.request.uri),
				  "useremail": user.nickname()
				}
				path = os.path.join(os.path.dirname(__file__), '../views/userform.html')
		else:
			vars = {
			  "lang": yaml.load(langfile),
			  "loginurl": users.create_login_url(self.request.uri)
			}
			path = os.path.join(os.path.dirname(__file__), '../views/home.html')

		# Close the file, not sure this works
		langfile.close()
		
		# Load the template		
		self.response.out.write(template.render(path, vars))

	def post(self):
		user = users.get_current_user()
		username = self.request.get("username")

		# Get the language header
		langheader = self.request.headers['Accept-Language']
        
        # Check for language
		if langheader.startswith('fr'):
			langfile = file(os.path.join(os.path.dirname(__file__), '../i18n/fr/home.yaml'))
		else:
			langfile = file(os.path.join(os.path.dirname(__file__), '../i18n/en/home.yaml'))
		
		if user and username and username != "":
			account = Account.gql("WHERE username = :1", username).get()
			
			if account:
				vars = {
				  "lang": yaml.load(langfile),
				  "logouturl": users.create_logout_url(self.request.uri),
				  "useremail": user.nickname(),
				  "error": True,
				  "username": username
				}
				path = os.path.join(os.path.dirname(__file__), '../views/userform.html')
			else:
				account = Account(username=username)
				account.put()
				vars = {
				  "lang": yaml.load(langfile),
				  "logouturl": users.create_logout_url(self.request.uri),
				  "username": username
				}
				path = os.path.join(os.path.dirname(__file__), '../views/userhome.html')

			# Load the template		
			self.response.out.write(template.render(path, vars))
