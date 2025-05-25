from utils import (get_close_prices,
                   get_et_now, get_time_until_next_midday,
                   should_update_prices)

from fastapi import FastAPI
from fastapi.responses import HTMLResponse
from jinja2 import Environment, FileSystemLoader
from datetime import datetime, timedelta, timezone
import pytz
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

    if not price_cache:
        target_date = et_now.strftime("%Y-%m-%d 12:00")
        price_cache.update(get_close_prices(pairs, target_date))
        last_update_date = et_now.date()

    if should_update_prices(et_now, last_update_date):
        target_date = et_now.strftime("%Y-%m-%d 12:00")
        price_cache = get_close_prices(pairs, target_date)
        last_update_date = et_now.date()

    time_only = et_now.strftime("%H:%M:%S")

    time_remaining = get_time_until_next_midday(et_now)

    html_content = template.render(
        time_only=time_only,
        time_remaining=time_remaining,
        price_cache=price_cache
    )
    return HTMLResponse(content=html_content)