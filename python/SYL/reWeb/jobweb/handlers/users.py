from flask import Blueprint
from jobweb.models import Job_detail, Jobseeker,User
from flask_login import login_required
from jobweb.forms import UserForm

user = Blueprint('user', __name__, url_prefix='/user')


@user.route('/<int:user_id>')
def profile(user_id):
	user = User.query.get_or_404(user_id)
	form = UserForm()
	jobseeker = user.seekerDetail
	return render_template('user/detail.html', user=user, form = form, jobseeker = jobseeker)



