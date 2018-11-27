from flask import abort
from flask_login import current_user
from functools import wraps
from jobweb.models import User

def role_required(role):
    def decorator(func) :
        @wraps(func)
        def wrapper(*args, **kwrargs):
            if not current_user.is_authenticated or current_user.role < role:
                boart(404)
            return func(*args, **kwrargs)
        return wrapper
    return decorator

#company_required = role.required(User.JOBSEEKER)
admin_required = role_required(User.ADMIN)
