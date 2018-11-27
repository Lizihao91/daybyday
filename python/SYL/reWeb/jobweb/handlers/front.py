from flask import Blueprint, render_template, redirect, url_for
from jobweb.models import Job_detail,User,Company
from jobweb.forms import LoginForm, UserRegisterForm, CompanyRegisterForm
from flask import flash
from flask_login import login_user, logout_user, login_required

front = Blueprint('front', __name__)

@front.route('/')
def index():
    jobs = Job_detail.query.filter_by(isValid=True).limit(10).all()
    companies = Company.query.filter_by(isValid=True).limit(10).all()
    return render_template('index.html', jobs=jobs, companies = companies)

@front.route('/companyRegister', methods=['GET', 'POST'])
def company_register():
    form = CompanyRegisterForm()
    if form.validate_on_submit():
        form.create_companyProfile()
        flash("Success!", 'success')
        return redirect(url_for('.login'))
    return render_template('company_register.html', form = form)

@front.route('/userRegister', methods=['GET', 'POST'])
def user_register():
    form = UserRegisterForm()
    if form.validate_on_submit():
        form.create_user()
        flash("Success!", 'success')
        return redirect(url_for('.login'))
    return render_template('user_register.html', form  = form)

@front.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter_by(email=form.email.data).first()
        login_user(user, form.remember_me.data)
        if user.is_admin: 
            return redirect(url_for('admin.index'))  # 建立了admin/index.html页面
        elif user.is_company:
            return redirect(url_for('company.profile', company_id = user.companydetail.id))
        else:
            return redirect( url_for('user.profile', user_id = user.id))
    return render_template('login.html', form = form)

@front.route('/logout', methods=['GET','POST'])
@login_required
def logout():
    logout_user()
    flash("You have successfull logged out")
    return redirect(url_for('.index'))

