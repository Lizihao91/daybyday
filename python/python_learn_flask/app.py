from flask import Flask, render_template
from datetime import datetime
from flask_sqlalchemy import SQLAlchemy
import os
import json

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root@localhost/news'
db = SQLAlchemy(app)


@app.route('/')
def index():
    title = []
    list_t = db.query(File).all()
    for l in list_t:
        title.append(l.title)
    return render_template('index.html', title_list=title)


@app.route('/files/<filename>')
def file(filename):
    list_t = db.query(File).all()
    for l in list_t:
        if filename == l.title:
            return render_template('file.html', title=l.title, created_time=l.create_time, content=l.content)


@app.errorhandler(404)
def not_found(error):
    return render_template('404.html'), 404


class File(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(80))
    create_time = db.column(db.Datetime)
    content = db.Column(db.Text)
    category_id = db.Column(db.Integer, db.ForeignKey('category.id'))
    category = db.relationship('Category', backref=db.backref('files'), lazy='dynamic')

    def __init__(self, title, category_id, content, create_time=None):
        self.title = title
        if create_time is None:
            create_time = datetime.utcnow()
        self.category_id = category_id
        self.content = content


class Category(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(80))

    def __init__(self, name):
        self.name = name



if __name__ == '__main__':
    app.run()
