import os
import json
from random import randint

from jobweb.models import db, User, Company, Job_detail


def iter_user():
	yield User(
		username = 'JD',
		email = 'JD@test.com',
		password = '123456')
def iter_company():
	yield Company(
		companyname = 'JD',
		address='Beijing',
		num = '5000'
		)

def iter_jobs():
	company = Company.query.filter_by(companyname = 'JD').first()
	with open(os.path.join(os.path.dirname(__file__), '..','datas','jobs.json'),encoding='utf-8-sig') as f:
		jobs = json.load(f)
		for job in jobs:
			yield Job_detail(
				jobname = job['name'],
				company = company,
				salary = job['salary'],
				experience = job['experience'],
				release_time = job['create_date']
				)


	
def run():
	"""for user in iter_user():
		db.session.add(user)
	for company in iter_company():
		company.user_id = 2
		db.session.add(company) """
	for job in iter_jobs():
		db.session.add(job)
	try:
		db.session.commit()
	except Exception as e:
		print(e)
		db.session.rollback()


	    
