from google.appengine.ext import db

class Account(db.Model):
    userid = db.UserProperty(auto_current_user_add=True)
    username = db.StringProperty(multiline=True)
