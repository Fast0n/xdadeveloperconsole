import requests as r
from bs4 import BeautifulSoup
import base64
from PyQL import PyQL
from Crypto.PublicKey import RSA

def decode(text):
    binary = base64.b64decode(text)

    private_key = RSA.importKey(get_key("private", False))

    return private_key.decrypt(binary).decode("utf8")

def encode(text, key):
    public_key = RSA.importKey(key)

    encrypt = public_key.encrypt(text.encode("utf8"), 32)[0]

    return base64.b64encode(encrypt).decode("utf8")

def create_keys():
    sql = PyQL("./keys.db")
    sql.create_table(
        "keys",
        exist=True,
        id = {
            "INTEGER": True,
            "PRIMARY KEY": True
        },
        private = {
            "BINARY": 2048,
            "NOT NULL": True
        },
        public = {
            "BINARY": 2048,
            "NOT NULL": True
        }
    )
    check = sql.select("keys")

    if not check:

        private_key = RSA.generate(2048)
        public_key = private_key.publickey().exportKey("DER")
        private_key = private_key.exportKey("DER")

        sql.insert(
            "keys",
            ("private", "public"),
            (private_key, public_key)
        )

def get_sessionid(cookies):
    params = {
        "response_type": "code",
        "client_id": "XDA-AMC-LB",
        "redirect_uri": "https://labs.xda-developers.com/auth"
    }
    response = r.get("https://api.xda-developers.com/oauth2/authorize", params=params, cookies=cookies).cookies.get_dict()
    return response

def get_key(type="public", base_64=True):
    sql = PyQL("./keys.db")

    key = sql.select(
        "keys",
        f"{type}"
    )[0][0]


    if base_64:
        key = base64.b64encode(key).decode("utf8")

    return key

def login(username, password):
    """ function to get login """
    password = decode(password)

    data = {
        "username": username,
        "password": password
    }
    response = r.post("https://api.xda-developers.com/user/login", data=data)
    if "success" in response.json():
        cookies = response.cookies.get_dict()
        result = {}
        result["bbuserid"] = response.cookies.get_dict()["bbuserid"]
        result["bbpassword"] = response.cookies.get_dict()["bbpassword"]
        result["sessionid"] = get_sessionid(cookies)["sessionid"]
    else:
        result = response.json()

    return result

def check_sessiod(sessionid):
    cookies = { "sessionid": sessionid }
    response = r.get("https://labs.xda-developers.com/manage/", cookies=cookies)

    result = {}
    if response.headers.get('Content-Length') > '2908':
        result["success"] = True
    else:
        result["success"] = False

    return result

def logout(sessionid):
    """ function to get logout """
    cookies = { "sessionid": sessionid }
    r.get("https://labs.xda-developers.com/manage/logout/", cookies=cookies)

def dashboard(sessionid):
    """ function to get dashboard information """
    cookies = { "sessionid": sessionid }
    response = r.get("https://labs.xda-developers.com/manage/", cookies=cookies).text

    soup = BeautifulSoup(response, "html.parser")

    container = soup.find_all("div", {"class": "col-md-8"})[0]
    rows = container.find_all("div", {"class": "col-md-4"})

    apps = rows[0].find("div", {"class": "tile-content-wrapper"}).get_text().strip()
    downloads = str(int(rows[1].find("div", {"class": "tile-content-wrapper"}).get_text().strip()) - 1)
    balance = rows[2].find("div", {"class": "tile-content-wrapper"}).get_text().strip().split('\n')[0]

    data = {
        "apps": apps,
        "downloads": downloads,
        "balance": balance
    }

    return data

def settings(sessionid):
    """ function to get settings information """
    cookies = { "sessionid": sessionid }
    response = r.get("https://labs.xda-developers.com/manage/settings", cookies=cookies).text

    data = {}

    soup = BeautifulSoup(response, "html.parser")

    inputs = soup.find_all("input", {"type": ["text", "email", "hidden"]})

    for input in inputs:
        camp = input.get("name")
        data[camp] = {}
        data[camp]["value"] = input.get("value")

    return data

def change_settings(sessionid, data):
    cookies = {
        "sessionid": sessionid,
        "csrftoken": data.get("csrfmiddlewaretoken")
    }
    headers = {
        "Origin": "https://labs.xda-developers.com",
        "Referer": "https://labs.xda-developers.com/manage/settings"
    }
    data["submit"] = "Save"

    response = r.post("https://labs.xda-developers.com/manage/settings", headers=headers, cookies=cookies, data=data, allow_redirects=True).text

    result = {}
    soup = BeautifulSoup(response, "html.parser")
    inputs = soup.find_all("input", {"type": ["text", "email", "hidden"]})

    for input in inputs:
        camp = input.get("name")
        result[camp] = {}
        result[camp]["value"] = input.get("value")

    print(result)

    return result

def apps(sessionid):

    cookies = {
        "sessionid": sessionid
    }
    response = r.get("https://labs.xda-developers.com/manage/apps/", cookies=cookies).text

    soup = BeautifulSoup(response, "html.parser")
    rows = soup.find_all("div", {"class": "app-listing-row"})

    data = {
        "apps": []
    }

    for row in rows:
        info = {}
        info["name"] = row.find("div", {"class": "tile-header"}).get_text().strip()
        info["img"] = row.find("img").get("src")
        info["id"] = row.find('a').get('href').split("/")[-1]
        data["apps"].append(info)

    return data

def get_app(sessionid, id):

    cookies = { "sessionid": sessionid }
    response = r.get(f"https://labs.xda-developers.com/manage/app/{id}", cookies=cookies).text

    soup = BeautifulSoup(response, "html.parser")

    app = soup.find("div", {"id": "app"})
    elements = app.find_all("div", {"class", "form-group"})

    result = {}

    # insert token
    token = app.find("input", {"type": "hidden"})
    result[token.get("name")] = {}
    result[token.get("name")]["value"] = token.get("value")


    for element in elements:
        label = element.find("label").get_text().split('\n')[0]
        key = label.lower()
        result[key] = {}
        result[key]["title"] = label

        # create var to check
        text = element.find("input", {"type": "text"})
        textarea = element.find("textarea")
        checkbox = element.find("input", {"type": "checkbox"})
        select = element.find("select")
        a = element.find("a")

        # checking
        if text:
            result[key]["text"] = text.get("value")
        elif textarea:
            result[key]["text"] = textarea.get_text()
        elif checkbox:
            if checkbox.get("checked") == "checked":
                result[key]["checked"] = True
            else:
                result[key]["checked"] = False
        elif select:
            options = select.find_all("option")
            result[key]["options"] = []
            for option in options:
                if option.get("selected") == "selected":
                    result[key]["selected"] = option.get_text()
                else:
                    result[key]["options"].append(option.get_text())
        elif a:
            result[key]["img"] = a.get("href")

    return result
