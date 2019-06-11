from flask import Flask, request, jsonify
import xda
from urllib.parse import urlparse, parse_qs


app = Flask(__name__)

@app.route("/login")
def login():
    username = request.args.get("username")
    password = request.args.get("password")
    # key = request.args.get("key").replace(" ", "+")

    # bbpassword = 3cb781756feab89c0d33c38ccd442469
    # bbuserid = 5567890

    if not username and not password:
        cookies = request.args.to_dict()
        sessionid = xda.get_sessionid(cookies)
    elif username and password:
        sessionid = xda.login(username, password.replace(" ", "+"))
    else:
        sessionid = {"error": "insert username and password"}

    return jsonify(sessionid)

@app.route("/get_key")
def get_key():
    key = xda.get_key()

    data = {
        "key": key
    }

    return jsonify(data)

@app.route("/dashboard")
def dashboard():
    sessionid = request.args.get("sessionid")

    data = xda.dashboard(sessionid)

    return jsonify(data)

@app.route("/logout")
def logout():
    sessionid = request.args.get("sessionid")

    logout(sessionid)

@app.route("/settings")
def settings():
    sessionid = request.args.get("sessionid")

    data = xda.settings(sessionid)

    return jsonify(data)

@app.route("/change_settings")
def change_settings():
    sessionid = request.args["sessionid"]
    data = request.args.to_dict()
    del data["sessionid"]

    data = xda.change_settings(sessionid, data)

    return jsonify(data)

@app.route("/app")
def application():
    sessionid = request.args.get("sessionid")

    data = xda.apps(sessionid)

    return jsonify(data)

@app.route("/check")
def check():
    sessionid = request.args.get("sessionid")

    data = xda.check_sessiod(sessionid)

    return jsonify(data)

@app.route("/get_app")
def get_app():
    sessionid = request.args.get("sessionid")
    id = request.args.get("id")

    data = xda.get_app(sessionid, id)

    return jsonify(data)


if __name__ == "__main__":
    xda.create_keys()
    app.run(debug=True,  host="0.0.0.0")
