from flask import Flask, render_template
from datetime import datetime
from flask_sqlalchemy import SQLAlchemy
from pymongo import MongoClient
import os
import json
import random

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root@localhost/news'
db = SQLAlchemy(app)

client = MongoClient('127.0.0.1', 27017)
mgdb = client.newstag

@app.route('/')
def index():
    title = []
    tag_dict = {}
    list_t = db.session.query(File).all()
    if not list_t:
        abort(404)
    for l in list_t:
        tags = l.tags
        title.append(l.title)
        if tags==None:
            tag_dict[l.title] = ''
        else:
            tag_dict[l.title] = tags
    return render_template('index.html', title_list=title, tag=tag_dict)


@app.route('/files/<filename>')
def file(filename):
    list_t = db.session.query(File).all()
    for l in list_t:
        if filename == l.title:
            return render_template('file.html', title=l.title, created_time=l.create_time, content=l.content)


@app.errorhandler(404)
def not_found(error):
    return render_template('404.html'), 404


class File(db.Model):
    find = []
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(80))
    create_time = db.Column(db.DateTime)
    content = db.Column(db.Text)

    category_id = db.Column(db.Integer, db.ForeignKey('category.id'))
    category = db.relationship('Category', backref=db.backref('files'))

    def __init__(self, title, category_id, content, create_time=None):
        self.title = title
        if create_time is None:
            create_time = datetime.utcnow()
        self.create_time = create_time
        self.category_id = category_id
        self.content = content 

    def add_tag(self, tag_name):
        mgdb.tags.insert_one({'title':self.title,'tags':tag_name})


    def remove_tag(self, tag_name):
        mgdb.tags.delete_one(tag_name)


    @property
    def tags(self):
        find = []
        for f in mgdb.tags.find({'title':self.title}):
            find.append(f.get('tags'))
        return find


class Category(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(80))

    def __init__(self, name):
        self.name = name



if __name__ == '__main__':
    app.run()
