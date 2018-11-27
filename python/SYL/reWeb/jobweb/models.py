from datetime import datetime
from flask_sqlalchemy import SQLAlchemy
from flask_login import UserMixin
from werkzeug.security import generate_password_hash, check_password_hash


db = SQLAlchemy()

class Base(db.Model):
    __abstract__ = True

    create_at = db.Column(db.DateTime, default=datetime.utcnow)
    update_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    isValid = db.Column('isValid', db.Boolean, default=True)

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()


#Status code: 1-active, 2-inprogress, 3-pass, 4-denied
applications = db.Table('applications', db.Column('user_id',db.Integer, db.ForeignKey('user.id'), primary_key=True),
                                        db.Column('job_id', db.Integer, db.ForeignKey('job_detail.id'), primary_key=True),
                                        db.Column('status', db.Integer),
                                        db.Column('created_at', db.DateTime, default = datetime.utcnow)

                                        )

class User(Base,UserMixin):
    __tablename__ = 'user'

    JOBSEEKER = 10
    COMPANY = 20
    ADMIN = 30

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(32), unique=True)
    cellphone = db.Column(db.String(32))
    email = db.Column(db.String(64), unique=True, nullable=False)
    _password = db.Column('password', db.String(256), nullable=False)
    role = db.Column(db.SmallInteger, default = JOBSEEKER)
    seekerDetail = db.relationship('Jobseeker', uselist=False)
    companydetail = db.relationship('Company', uselist=False)
    applied_jobs = db.relationship('Job_detail', secondary=applications)

    def __repr__(self):
        return '<User:{}>'.format(self.name)

    @property
    def password(self):
        return self._password
    @password.setter
    def password(self, pw):
        self._password = generate_password_hash(pw)

    def check_password(self, password):
        return check_password_hash(self._password, password)

    @property
    def is_admin(self):
        return self.role == self.ADMIN

    @property
    def is_company(self):
        return self.role == self.COMPANY
    

class Company(Base):
    __tablename__ = 'company'

    id = db.Column(db.Integer, primary_key=True)
    companyname = db.Column(db.String(64), unique=True, index=True, nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship('User', uselist = False)
    website = db.Column(db.String(64))
    # 公司人数
    num = db.Column(db.String(32))
    logo = db.Column(db.String(64))
    address = db.Column(db.String(64))
    # 公司介绍
    desc = db.Column(db.String(256))
    release_job = db.relationship('Job_detail')

    def __repr__(self):
        return '<Company {}>'.format(self.id)

class Job_detail(Base):
    __tablename__ = 'job_detail'

    id = db.Column(db.Integer, primary_key=True)
    company_id = db.Column(db.Integer, db.ForeignKey('company.id', ondelete = 'SET NULL'))
    company = db.relationship('Company', uselist=False)
    jobname = db.Column(db.String(64))
    salary = db.Column(db.String(32), nullable=False)
    workaddress = db.Column(db.String(64))
    education = db.Column(db.String(32))
    release_time = db.Column(db.String(64), default=datetime.now)
    experience = db.Column(db.String(128))
    desc = db.Column(db.String(256))
    applied_user = db.relationship('User', secondary=applications)

    def __repr__(self):
        return '<Job_detail {}>'.format(self.jobname)

class  Jobseeker(Base):
    __tablename__ = 'jobseeker'

    id = db.Column(db.Integer, primary_key=True)
    seekername = db.Column(db.String(32))
    user_id = db.Column(db.Integer, db.ForeignKey('user.id', ondelete = 'SET NULL'))
    user = db.relationship('User', uselist=False)
    #email = user.email
    #password = user.password
    sex = db.Column(db.String(16))
    age = db.Column(db.Integer)
    address =db.Column(db.String(64))
    # 上传的简历
    resume_up = db.Column(db.String(32))
    resumeId = db.Column(db.Integer, unique=True)
    desc_edu = db.Column(db.String(256))
    desc_experience = db.Column(db.String(256))

class delivery(Base):
    __tablename__ = 'delivery'
    STATUS_PENDING = 1
    STATUS_REJECT = 2
    STATUS_ACCEPT = 3
    id = db.Column(db.Integer, primary_key=True)
    job_id = db.Column(db.Integer, db.ForeignKey('job_detail.id'))
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    company_id = db.Column(db.Integer, db.ForeignKey('company.id'))
    status = db.Column(db.SmallInteger, default=STATUS_PENDING)
    response = db.Column(db.String(256))
    @property
    def user(self):
        return User.query.get(self.user_id)
    @property
    def job(self):
        return Job_detail.query.get(self.job_id)
    @property
    def company(self):
        return Company.query.get(self.company_id)





