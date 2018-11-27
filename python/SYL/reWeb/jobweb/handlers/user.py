from flask import Blueprint, render_template,flash, request, abort
from flask_login import login_required, current_user

from jobweb.forms import baseUserForm
from jobweb.models import Job_detail, Jobseeker,User

user = Blueprint('user', __name__, url_prefix='/user')


@user.route('/<int:user_id>/profile', methods = ['GET','POST'])
@login_required
def profile(user_id):
    user = User.query.get_or_404(user_id)
    if current_user != user:
        abort(404)
    jobseeker = user.seekerDetail

    if request.method == 'GET':
        form = baseUserForm(obj=user)
        # form.email.data = user.email
        # form.password.data = user._password
        # form.cellphone.data = user.cellphone
        form.seekername.data = jobseeker.seekername
        form.desc_edu.data = jobseeker.desc_edu
        form.desc_experience.data = jobseeker.desc_experience
    else:
        form = baseUserForm()

    if form.validate_on_submit():
         form.saveUser(user)
         flash("Success!", 'success')
    return render_template('user/detail.html', user=user, form = form)



