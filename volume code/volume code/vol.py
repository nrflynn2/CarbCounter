from flask import Flask
import numpy as np
import sys
from stl import mesh

app = Flask(__name__)

@app.route("/")
def hello():
	item = app.config.get('food')
	your_mesh = mesh.Mesh.from_file(item)
	volume, cog, inertia = your_mesh.get_mass_properties()
	return 'Volume  = {0}'.format(volume)

if __name__ == "__main__":
    app.config['food'] = sys.argv[1]
    app.run()
