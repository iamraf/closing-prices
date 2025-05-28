from utils import (get_et_now, get_time_until_next_midday,
                    update_prices_if_needed)

from fastapi import FastAPI
from fastapi.responses import HTMLResponse
from jinja2 import Environment, FileSystemLoader

import os

app = FastAPI()

price_cache = {}
last_update_date = None

env = Environment(loader=FileSystemLoader(os.path.join(os.path.dirname(__file__), "templates")))
template = env.get_template("price_page.html")

@app.get("/", response_class=HTMLResponse)
def fetch_default_close_prices_html():
    global price_cache, last_update_date

    et_now = get_et_now()
    pairs = ["BTC_USDT", "ETH_USDT", "SOL_USDT", "XRP_USDT"]

    price_cache, last_update_date = update_prices_if_needed(price_cache, last_update_date, pairs, et_now)

    time_only = et_now.strftime("%H:%M:%S")
    time_remaining = get_time_until_next_midday(et_now)

    html_content = template.render(
        time_only=time_only,
        time_remaining=time_remaining,
        price_cache=price_cache
    )
    return HTMLResponse(content=html_content)