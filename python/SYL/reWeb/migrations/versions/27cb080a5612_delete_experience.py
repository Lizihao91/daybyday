"""delete experience

Revision ID: 27cb080a5612
Revises: 262f2d3f7998
Create Date: 2018-01-19 08:19:23.472908

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

# revision identifiers, used by Alembic.
revision = '27cb080a5612'
down_revision = '262f2d3f7998'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('experience')
    op.add_column('jobseeker', sa.Column('desc_edu', sa.String(length=256), nullable=True))
    op.add_column('jobseeker', sa.Column('desc_experience', sa.String(length=256), nullable=True))
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('jobseeker', 'desc_experience')
    op.drop_column('jobseeker', 'desc_edu')
    op.create_table('experience',
    sa.Column('create_at', mysql.DATETIME(), nullable=True),
    sa.Column('upodate_at', mysql.DATETIME(), nullable=True),
    sa.Column('id', mysql.INTEGER(display_width=11), nullable=False),
    sa.Column('ep_id', mysql.INTEGER(display_width=11), autoincrement=False, nullable=True),
    sa.Column('desc_edu', mysql.VARCHAR(length=256), nullable=True),
    sa.Column('desc_job', mysql.VARCHAR(length=256), nullable=True),
    sa.ForeignKeyConstraint(['ep_id'], ['jobseeker.id'], name='experience_ibfk_1'),
    sa.PrimaryKeyConstraint('id'),
    mysql_default_charset='utf8',
    mysql_engine='InnoDB'
    )
    # ### end Alembic commands ###
