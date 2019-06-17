import requests as r
from bs4 import BeautifulSoup
import base64
from PyQL import PyQL
from Crypto.PublicKey import RSA

def decode(text):
    binary = base64.b64decode(text)

    private_key = RSA.importKey(get_key("private", False))

    try:
        return private_key.decrypt(binary).decode("utf8")
    except:
        return None

def encode(text, key):
    public_key = RSA.importKey(key)

    encrypt = public_key.encrypt(text.encode("utf8"), 32)[0]

    return base64.b64encode(encrypt).decode("utf8")

def create_keys():
    sql = PyQL("./keys.db")
    sql.create_table(
        "IF NOT EXISTS keys",
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
    check = sql.select("*", "keys")

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
        f"{type}",
        "keys"
    )[0][0]


    if base_64: key = base64.b64encode(key).decode("utf8")

    return key

def login(username, password):
    """ function to get login """
    password = decode(password)

    if password:
        # remove salt from password
        password = password[:-8]

        data = {
            "username": username,
            "password": password
        }

        response = r.post("https://api.xda-developers.com/user/login", data=data)

        if "success" in response.json():
            cookies = response.cookies.get_dict() # get cookies
            result = {}
            result["bbuserid"] = response.cookies.get_dict()["bbuserid"]
            result["bbpassword"] = response.cookies.get_dict()["bbpassword"]
            # get sessionid from response
            result["sessionid"] = get_sessionid(cookies)["sessionid"]
        else:
            result = response.json()

        return result

    else:

        result = {"error": "password encode error"}

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

    # get element to parse
    title = soup.find("div", {"id": "page-title"}).get_text().strip()

    elements = soup.find_all("div", {"class": "col-md-4"})

    # insert parsed value in dict struct
    result = {"dashboard": []}

    result["title"] = title

    for i, element in enumerate(elements):
        name = element.find("div", {"class": "tile-header"}).get_text().strip()
        value = element.find("div", {"class": "tile-content-wrapper"}).get_text().replace(" ", "").replace("\n", " ").strip()
        color = "#2ecc71"
        if i == 1: color = "#3498db"
        if i == 2: color = "#e67e22"
        result["dashboard"].append({
            "name": name,
            "value": value,
            "color": color
        })


    return result

def settings(sessionid):
    """ function to get settings information """
    cookies = { "sessionid": sessionid }
    response = r.get("https://labs.xda-developers.com/manage/settings", cookies=cookies).text

    data = {}

    soup = BeautifulSoup(response, "html.parser")

    # get element to parse
    title = soup.find("div", {"id": "page-title"}).find("h2")

    groups = soup.find_all("div", {"class": "form-group"})

    # insert parsed value in dict struct
    data["settings"] = []

    data["settings"].append(
        {
            "name": "title",
            "value": title.get_text().strip(),
            "tag": title.name
        }
    )

    form_elements = soup.find("form", {"class": ["form-horizontal", "row"]}).find_all()

    for element in form_elements:
        add = {}
        if element.get("class") and element.get("class")[0] == "form-group":
            input = element.find("input")

            id_name = input.get("name")
            name = element.find("label", {"class": ["col-sm-1", "control-label"]}).get_text().strip()
            value = input.get("value")
            alert = element.find("div", {"class": "alert"})

            add["name"] = name
            add["id_name"] = id_name
            add["value"] = value
            add["tag"] = element.name

            if alert: add["alert"] = alert.get_text().strip()

        elif element.name == "h2":
            value = element.get_text().strip()
            add["value"] = value
            add["name"] = "title"
            add["tag"] = element.name

        if add: data["settings"].append(add)

    csrf = soup.find("input", {"type": "hidden"})
    add = {}
    name = csrf.get("name")
    value = csrf.get("value")

    add["name"] = name
    add["id_name"] = name
    add["value"] = value
    add["tag"] = csrf.name

    data["settings"].append(add)

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

    return settings(sessionid)

def apps(sessionid):

    cookies = {
        "sessionid": sessionid
    }
    response = r.get("https://labs.xda-developers.com/manage/apps/", cookies=cookies).text

    soup = BeautifulSoup(response, "html.parser")
    rows = soup.find_all("div", {"class": "app-listing-row"})

    title = soup.find("div", {"id": "page-title"}).get_text().strip()

    data = { "apps": [] }

    data["title"] = title

    for row in rows:
        info = {}
        info["name"] = row.find("div", {"class": "tile-header"}).get_text().strip()
        info["img"] = row.find("img").get("src")
        info["id"] = row.find('a').get('href').split("/")[-1]
        info["color"] = "#2ecc71"
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

def get_xposed(sessionid):
    cookies = {
        "sessionid": sessionid
    }

    response = r.get("https://labs.xda-developers.com/manage/modules/", cookies=cookies).text

    soup = BeautifulSoup(response, "html.parser")

    title = soup.find("div", {"id": "page-title"}).get_text().strip()

    data = {}

    data["title"] = title

    return data
